#!/usr/bin/env python3
"""Prove a NeoForge 26.1 hotfix build emits the exact classes of the 26.1 JAR."""
import pathlib
import zipfile

root = pathlib.Path(__file__).resolve().parents[1]
jar = root / 'mc26_1/build/libs/pinchat-mod-neoforge-mc26.1-3.0.0.jar'
classes = root / 'mc26_1/build/classes/java/main'
with zipfile.ZipFile(jar) as archive:
    expected = {name for name in archive.namelist() if name.endswith('.class')}
    actual = {str(path.relative_to(classes)) for path in classes.rglob('*.class')}
    assert actual == expected, f'Class set differs: missing={expected - actual}, extra={actual - expected}'
    for name in sorted(actual):
        assert (classes / name).read_bytes() == archive.read(name), f'Hotfix changed compiled class: {name}'
print(f'NeoForge hotfix emits the same {len(actual)} class files as the 26.1 release JAR')
