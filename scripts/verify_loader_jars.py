#!/usr/bin/env python3
"""Check the packaged loader metadata and the classes it names."""

import json
import pathlib
import sys
import zipfile


root = pathlib.Path(__file__).resolve().parents[1]
properties = dict(
    line.split("=", 1)
    for line in (root / "gradle.properties").read_text().splitlines()
    if "=" in line and not line.startswith("#")
)
version = properties["mod_version"]
fabric = root / "quilt/build/libs" / f"pinchat-mod-quilt-{version}.jar"
neoforge = root / "neoforge/build/libs" / f"pinchat-mod-neoforge-{version}.jar"
neoforge_26 = root / "neoforge-mc26/build/libs" / f"pinchat-mod-neoforge-mc26.1-{version}.jar"
fabric_26 = root / "fabric-mc26/build/libs" / f"pinchat-mod-fabric-mc26.1-{version}.jar"
fabric_26_2 = root / "fabric-mc26/build/libs" / f"pinchat-mod-fabric-mc26.2-{version}.jar"
neoforge_26_2 = root / "neoforge-mc26/build/libs" / f"pinchat-mod-neoforge-mc26.2-{version}.jar"


def check_mixin(archive):
    mixin = json.loads(archive.read("pinchat.mixins.json"))
    package = mixin["package"].replace(".", "/")
    for kind in ("mixins", "client"):
        for name in mixin.get(kind, []):
            target = f"{package}/{name.replace('.', '/')}.class"
            if target not in archive.namelist():
                raise AssertionError(f"Missing mixin class {target}")


with zipfile.ZipFile(fabric) as archive:
    metadata = json.loads(archive.read("fabric.mod.json"))
    assert metadata["id"] == "pinchat"
    assert metadata["environment"] == "client"
    assert metadata["version"] == version
    assert metadata["depends"]["minecraft"] == "~1.21.11"
    for entrypoint in metadata["entrypoints"]["client"]:
        assert entrypoint.replace(".", "/") + ".class" in archive.namelist()
    check_mixin(archive)

with zipfile.ZipFile(neoforge) as archive:
    metadata = archive.read("META-INF/neoforge.mods.toml").decode()
    assert 'modId="pinchat"' in metadata
    assert f'version="{version}"' in metadata
    assert 'config="pinchat.mixins.json"' in metadata
    assert "dev/sfafy/pinchat/PinChatMod.class" in archive.namelist()
    assert "dev/sfafy/pinchat/ClientSetup.class" in archive.namelist()
    check_mixin(archive)

with zipfile.ZipFile(neoforge_26) as archive:
    metadata = archive.read("META-INF/neoforge.mods.toml").decode()
    assert 'modId="pinchat"' in metadata
    assert f'version="{version}"' in metadata
    assert 'versionRange="[26.1,26.2)"' in metadata
    assert 'config="pinchat.mixins.json"' in metadata
    assert json.loads(archive.read("pinchat.mixins.json"))["compatibilityLevel"] == "JAVA_25"
    assert int.from_bytes(archive.read("dev/sfafy/pinchat/PinChatMod.class")[6:8], "big") == 69
    check_mixin(archive)

with zipfile.ZipFile(fabric_26) as archive:
    metadata = json.loads(archive.read("fabric.mod.json"))
    assert metadata["id"] == "pinchat"
    assert metadata["environment"] == "client"
    assert metadata["version"] == version
    assert metadata["depends"]["minecraft"] == ">=26.1 <26.2"
    assert metadata["depends"]["java"] == ">=25"
    for entrypoint in metadata["entrypoints"]["client"]:
        assert entrypoint.replace(".", "/") + ".class" in archive.namelist()
    assert int.from_bytes(archive.read("dev/sfafy/pinchat/PinChatMod.class")[6:8], "big") == 69
    check_mixin(archive)

with zipfile.ZipFile(fabric_26_2) as archive:
    metadata = json.loads(archive.read("fabric.mod.json"))
    assert metadata["id"] == "pinchat"
    assert metadata["depends"]["minecraft"] == "26.2"
    assert metadata["depends"]["java"] == ">=25"
    for entrypoint in metadata["entrypoints"]["client"]:
        assert entrypoint.replace(".", "/") + ".class" in archive.namelist()
    check_mixin(archive)

with zipfile.ZipFile(neoforge_26_2) as archive:
    metadata = archive.read("META-INF/neoforge.mods.toml").decode()
    assert 'modId="pinchat"' in metadata
    assert 'versionRange="[26.2]"' in metadata
    assert 'config="pinchat.mixins.json"' in metadata
    check_mixin(archive)

print("Fabric/Quilt and NeoForge 1.21.11, 26.1.x and 26.2 JAR metadata: OK")
