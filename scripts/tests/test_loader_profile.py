"""Protect installer artifact checksums from duplicate download destinations."""
import json
import pathlib
import sys
import tempfile
import unittest

sys.path.insert(0, str(pathlib.Path(__file__).parents[1]))
from gameplay_client import deduplicate_profile_libraries


class LoaderProfileTest(unittest.TestCase):
    def test_identical_duplicates_are_removed_before_parallel_downloads(self):
        library = {'name': 'org.ow2.asm:asm:9.8', 'downloads': {'artifact': {
            'path': 'org/ow2/asm/asm/9.8/asm-9.8.jar', 'sha1': 'verified-digest', 'url': 'https://example.test/asm.jar'}}}
        other = {'name': 'other:dependency:1'}
        profile = {'id': 'forge-profile', 'libraries': [library, other, dict(reversed(list(library.items())))],
                   'arguments': {'game': ['--example']}}
        with tempfile.TemporaryDirectory() as directory:
            path = pathlib.Path(directory) / 'profile.json'
            path.write_text(json.dumps(profile))
            self.assertEqual(deduplicate_profile_libraries(path), 1)
            expected = dict(profile, libraries=[library, other])
            self.assertEqual(json.loads(path.read_text()), expected)
            saved = path.read_bytes()
            self.assertEqual(deduplicate_profile_libraries(path), 0)
            self.assertEqual(path.read_bytes(), saved)

    def test_distinct_platform_rules_and_checksums_are_preserved(self):
        libraries = [
            {'name': 'native:library:1', 'rules': [{'action': 'allow', 'os': {'name': os}}]}
            for os in ('linux', 'windows')]
        libraries += [{'name': 'same:artifact:1', 'downloads': {'artifact': {'sha1': value}}}
                      for value in ('first-digest', 'second-digest')]
        with tempfile.TemporaryDirectory() as directory:
            path = pathlib.Path(directory) / 'profile.json'
            path.write_text(json.dumps({'libraries': libraries}))
            saved = path.read_bytes()
            self.assertEqual(deduplicate_profile_libraries(path), 0)
            self.assertEqual(path.read_bytes(), saved)
