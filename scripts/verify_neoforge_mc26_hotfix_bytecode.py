#!/usr/bin/env python3
"""Prove a NeoForge 26.1 hotfix build emits the exact classes of the 26.1 JAR."""
import pathlib
import zipfile

root = pathlib.Path(__file__).resolve().parents[1]
properties = dict(line.split('=', 1) for line in (root / 'gradle.properties').read_text().splitlines()
                  if '=' in line and not line.startswith('#'))
jar = root / f"neoforge-mc26/build/libs/pinchat-mod-neoforge-mc26.1-{properties['mod_version']}.jar"
classes = root / 'neoforge-mc26/build/classes/java/main'
with zipfile.ZipFile(jar) as archive:
    expected = {name for name in archive.namelist() if name.endswith('.class')}
    actual = {str(path.relative_to(classes)) for path in classes.rglob('*.class')}
    assert actual == expected, f'Class set differs: missing={expected - actual}, extra={actual - expected}'
    for name in sorted(actual):
        assert (classes / name).read_bytes() == archive.read(name), f'Hotfix changed compiled class: {name}'
print(f'NeoForge hotfix emits the same {len(actual)} class files as the 26.1 release JAR')
