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

for game in ('26.2', '26.3'):
    for loader in ('fabric', 'neoforge'):
        path = root / f"{loader}-mc26/build/libs/pinchat-mod-{loader}-mc{game}-{version}.jar"
        with zipfile.ZipFile(path) as archive:
            if loader == 'fabric':
                metadata = json.loads(archive.read('fabric.mod.json'))
                assert metadata['id'] == 'pinchat'
                assert metadata['version'] == version
                assert metadata['depends']['minecraft'] == game
                assert metadata['depends']['java'] == '>=25'
                for entrypoint in metadata['entrypoints']['client']:
                    assert entrypoint.replace('.', '/') + '.class' in archive.namelist()
            else:
                metadata = archive.read('META-INF/neoforge.mods.toml').decode()
                assert 'modId="pinchat"' in metadata
                assert f'version="{version}"' in metadata
                assert f'versionRange="[{game}]"' in metadata
                assert 'config="pinchat.mixins.json"' in metadata
            check_mixin(archive)
            mixins = json.loads(archive.read('pinchat.mixins.json'))['client']
            assert 'MoveableChatMouseMixin' in mixins
            if game == '26.3':
                pack = json.loads(archive.read('pack.mcmeta'))['pack']
                assert pack['min_format'] == pack['max_format'] == [97, 1]
                for name in archive.namelist():
                    if name.endswith('.class'):
                        assert b'org/lwjgl/glfw' not in archive.read(name), f'GLFW reference in SDL build: {name}'

print("Fabric/Quilt and NeoForge 1.21.11, 26.1.x, 26.2 and 26.3 JAR metadata: OK")
