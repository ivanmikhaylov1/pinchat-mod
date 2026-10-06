"""Keep bilingual entry points and in-game translations usable across loaders."""
import json
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[2]


class DocumentationTest(unittest.TestCase):
    def test_guides_link_to_existing_counterparts(self):
        pairs = [(ROOT / f'{name}.md', ROOT / f'{name}.ru.md')
                 for name in ('README', 'CONTRIBUTING', 'CHANGELOG')]
        pairs += [(ROOT / 'docs' / f'{name}.md', ROOT / 'docs/ru' / f'{name}.md')
                  for name in ('DEVELOPMENT', 'TESTING', 'COMPATIBILITY', 'RELEASING')]
        for english, russian in pairs:
            for source, target, label in ((english, russian, 'Русский'),
                                          (russian, english, 'English')):
                with self.subTest(document=str(source.relative_to(ROOT))):
                    text = source.read_text(encoding='utf-8')
                    link = re.search(rf'\[{label}\]\(([^)]+)\)', text)
                    self.assertIsNotNone(link)
                    self.assertEqual((source.parent / link.group(1)).resolve(), target)
                    self.assertTrue(target.is_file())

    def test_all_loader_locales_have_matching_keys_and_format_placeholders(self):
        for loader in ('fabric', 'forge', 'neoforge'):
            directory = ROOT / loader / 'src/main/resources/assets/pinchat/lang'
            english = json.loads((directory / 'en_us.json').read_text(encoding='utf-8'))
            russian = json.loads((directory / 'ru_ru.json').read_text(encoding='utf-8'))
            with self.subTest(loader=loader):
                self.assertEqual(english.keys(), russian.keys())
                for key in english:
                    self.assertTrue(english[key].strip(), key)
                    self.assertTrue(russian[key].strip(), key)
                    self.assertEqual(re.findall(r'%(?:\d+\$)?[sd]', english[key]),
                                     re.findall(r'%(?:\d+\$)?[sd]', russian[key]), key)
