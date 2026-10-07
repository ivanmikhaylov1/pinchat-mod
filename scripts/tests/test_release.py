import hashlib
import importlib.util
import json
import pathlib
import tempfile
import unittest
from unittest.mock import patch
import zipfile

SPEC = importlib.util.spec_from_file_location("release", pathlib.Path(__file__).parents[1] / "release.py")
release = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(release)


class ReleaseTest(unittest.TestCase):
    def test_matrix_has_every_supported_combination_and_alias_uses_base_binary(self):
        rows = release.matrix()
        self.assertEqual(len(rows), 19)
        self.assertEqual(len({(r['loader'], r['minecraft']) for r in rows}), 19)
        for row in rows:
            if row['minecraft'] in ('26.1.1', '26.1.2'):
                self.assertEqual(row['artifact_version'], '26.1')
            if row['minecraft'] == '26.3':
                self.assertEqual(row['artifact_version'], '26.3')
                self.assertIn('mc26.3-', str(release.source_jar('3.1.0', row)))
            if row['loader'] == 'quilt' and row['minecraft'] != '1.21.11':
                self.assertIn('fabric-mc26', str(release.source_jar('3.1.0', row)))

    def fixture(self, root):
        (root / 'config').mkdir()
        (root / 'config/targets.json').write_bytes((release.ROOT / 'config/targets.json').read_bytes())
        for name in ('fabric-mc26', 'neoforge-mc26'):
            (root / name).mkdir()
            (root / name / 'gradle.properties').write_text('mod_version=3.1.0\n')
        with patch.object(release, 'ROOT', root):
            for row in release.matrix():
                path = release.source_jar('3.1.0', row)
                path.parent.mkdir(parents=True, exist_ok=True)
                with zipfile.ZipFile(path, 'w') as archive:
                    archive.writestr('fabric.mod.json', json.dumps({'version': '3.1.0'}))
                    for language in ('en_us', 'ru_ru'):
                        archive.writestr(f'assets/pinchat/lang/{language}.json',
                                         json.dumps({'pinchat.config.title': 'PinChat'}))

    def test_prepare_names_all_files_and_hashes_the_exact_bytes(self):
        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            self.fixture(root)
            with patch.object(release, 'ROOT', root):
                release.prepare(root / 'dist', '3.1.0', 'v3.1.0')
            manifest = json.loads((root / 'dist/manifest.json').read_text())
            self.assertEqual(len(manifest['artifacts']), 19)
            for row in manifest['artifacts']:
                actual = hashlib.sha256((root / 'dist' / row['file']).read_bytes()).hexdigest()
                self.assertEqual(row['sha256'], actual)
                self.assertIn(f"{actual}  {row['file']}", (root / 'dist/SHA256SUMS').read_text())
            for loader in ('fabric', 'quilt', 'neoforge'):
                names = [f'pinchat-mod-{loader}-3.1.0-mc{game}.jar' for game in ('26.1', '26.1.1', '26.1.2')]
                self.assertEqual(len({(root / 'dist' / n).read_bytes() for n in names}), 1)

    def test_bilingual_notes_use_each_changelog_and_resolvable_release_links(self):
        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            self.fixture(root)
            (root / 'CHANGELOG.md').write_text(
                '# Changelog\n\n## 3.1.0\n\nEnglish change [guide](docs/TESTING.md).'
                '\n\n## 3.0.0\n\nOld change.\n', encoding='utf-8')
            (root / 'CHANGELOG.ru.md').write_text(
                '# История\n\n## 3.1.0\n\nРусское изменение [инструкция](docs/ru/TESTING.md).'
                '\n\n## 3.0.0\n\nСтарое изменение.\n', encoding='utf-8')
            with patch.object(release, 'ROOT', root):
                release.prepare(root / 'dist', '3.1.0', 'v3.1.0')
            notes = (root / 'dist/RELEASE_NOTES.md').read_text(encoding='utf-8')
            english, russian = notes.split('\n\n---\n\n')
            self.assertIn('English change', english)
            self.assertNotIn('Русское изменение', english)
            self.assertIn('Русское изменение', russian)
            self.assertNotIn('English change', russian)
            self.assertNotIn('Old change', notes)
            self.assertNotIn('Старое изменение', notes)
            self.assertIn('blob/master/docs/TESTING.md', english)
            self.assertIn('blob/master/docs/ru/TESTING.md', russian)
            self.assertNotIn('](docs/', notes)
            for game in release.targets():
                self.assertIn(f'| {game} |', english)
                self.assertIn(f'| {game} |', russian)

    def test_tag_mismatch_is_rejected_before_any_output(self):
        with tempfile.TemporaryDirectory() as directory:
            root = pathlib.Path(directory)
            self.fixture(root)
            with patch.object(release, 'ROOT', root):
                with self.assertRaisesRegex(ValueError, 'does not match'):
                    release.prepare(root / 'dist', '3.1.0', 'v3.2.0')
            self.assertFalse((root / 'dist').exists())

    def test_missing_bundled_translation_blocks_publication(self):
        with tempfile.TemporaryDirectory() as directory:
            path = pathlib.Path(directory) / 'missing-russian.jar'
            with zipfile.ZipFile(path, 'w') as archive:
                archive.writestr('fabric.mod.json', '{"version":"3.1.0"}')
                archive.writestr('assets/pinchat/lang/en_us.json', '{"title":"PinChat"}')
            with self.assertRaisesRegex(ValueError, 'Missing ru_ru localization'):
                release.validate_jar(path, '3.1.0')

    def test_wrong_jar_version_and_bundled_test_mod_are_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            path = pathlib.Path(directory) / 'bad.jar'
            with zipfile.ZipFile(path, 'w') as archive:
                archive.writestr('fabric.mod.json', '{"version":"3.0.0"}')
            with self.assertRaisesRegex(ValueError, 'Expected 3.1.0'):
                release.validate_jar(path, '3.1.0')
            with zipfile.ZipFile(path, 'w') as archive:
                archive.writestr('fabric.mod.json', '{"version":"3.1.0"}')
                archive.writestr('dev/sfafy/pinchat/gametest/Test.class', b'test')
            with self.assertRaisesRegex(ValueError, 'Test code included'):
                release.validate_jar(path, '3.1.0')


if __name__ == '__main__':
    unittest.main()
