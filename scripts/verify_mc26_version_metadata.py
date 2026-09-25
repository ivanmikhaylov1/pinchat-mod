#!/usr/bin/env python3
"""Verify protocol and pack versions from each downloaded official client JAR."""
import json
import pathlib
import zipfile

root = pathlib.Path(__file__).resolve().parents[1]
expected = {
    '26.1': (775, 4786, 101, 1, 84, 0),
    '26.1.1': (775, 4788, 101, 1, 84, 0),
    '26.1.2': (775, 4790, 101, 1, 84, 0),
    '26.2': (776, 4903, 107, 1, 88, 0),
}
for version, values in expected.items():
    jar = root / 'build/client26-smoke/versions' / version / f'{version}.jar'
    with zipfile.ZipFile(jar) as archive:
        data = json.loads(archive.read('version.json'))
    pack = data['pack_version']
    found = (data['protocol_version'], data['world_version'], pack['data_major'],
             pack['data_minor'], pack['resource_major'], pack['resource_minor'])
    assert found == values, f'{version}: expected {values}, got {found}'
    assert data['java_version'] == 25
    print(f'Minecraft {version}: protocol {found[0]}, world {found[1]}, '
          f'data {found[2]}.{found[3]}, resources {found[4]}.{found[5]}')
