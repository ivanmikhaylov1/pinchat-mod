#!/usr/bin/env python3
"""One target matrix for CI, named release files, checksums and user-facing notes."""
import argparse
import hashlib
import json
import pathlib
import shutil
import zipfile

ROOT = pathlib.Path(__file__).resolve().parents[1]


def properties(path):
    return dict(line.split("=", 1) for line in path.read_text().splitlines()
                if "=" in line and not line.startswith("#"))


def targets():
    return json.loads((ROOT / "config/targets.json").read_text())


def matrix():
    return [{"minecraft": game, "loader": loader, "loader_version": version,
             "java": target["java"], "artifact_version": target["artifact_version"],
             "fabric_api": target["fabric_api"]}
            for game, target in targets().items() for loader, version in target["loaders"].items()]


def artifact_name(version, row):
    return f"pinchat-mod-{row['loader']}-{version}-mc{row['minecraft']}.jar"


def source_jar(version, row):
    loader, game = row["loader"], row["artifact_version"]
    if game == "1.21.11":
        name = f"pinchat-mod{'-' + loader if loader != 'fabric' else ''}-{version}.jar"
        return ROOT / loader / "build/libs" / name
    platform = "fabric" if loader == "quilt" else loader
    return ROOT / f"{platform}-mc26/build/libs" / f"pinchat-mod-{platform}-mc{game}-{version}.jar"


def validate_jar(path, version):
    with zipfile.ZipFile(path) as jar:
        bad = jar.testzip()
        if bad:
            raise ValueError(f"Corrupt ZIP entry {bad} in {path}")
        if any("gametest" in name.lower() or "client_tests" in name for name in jar.namelist()):
            raise ValueError(f"Test code included in release JAR: {path}")
        if "fabric.mod.json" in jar.namelist():
            actual = json.loads(jar.read("fabric.mod.json"))["version"]
        else:
            import tomllib
            name = "META-INF/neoforge.mods.toml" if "META-INF/neoforge.mods.toml" in jar.namelist() else "META-INF/mods.toml"
            actual = tomllib.loads(jar.read(name).decode())["mods"][0]["version"]
        if actual != version:
            raise ValueError(f"Expected {version}, found {actual} in {path}")


def prepare(dist, version, tag=None):
    for module in ("fabric-mc26", "neoforge-mc26"):
        if properties(ROOT / module / "gradle.properties")["mod_version"] != version:
            raise ValueError(f"Version mismatch in {module}/gradle.properties")
    if tag and tag.removeprefix("v") != version:
        raise ValueError(f"Tag {tag} does not match mod_version={version}")
    # Validate everything before writing; stale JARs cannot silently enter a release.
    rows = matrix()
    for row in rows:
        validate_jar(source_jar(version, row), version)
    expected = {artifact_name(version, row) for row in rows}
    dist.mkdir(parents=True, exist_ok=True)
    if any(p.name not in expected for p in dist.glob("*.jar")):
        raise ValueError(f"Unexpected JARs in {dist}; use a clean output directory")
    manifest = []
    for row in rows:
        path = dist / artifact_name(version, row)
        shutil.copy2(source_jar(version, row), path)
        manifest.append({**row, "file": path.name, "sha256": hashlib.sha256(path.read_bytes()).hexdigest()})
    (dist / "SHA256SUMS").write_text("".join(f"{r['sha256']}  {r['file']}\n" for r in manifest))
    (dist / "manifest.json").write_text(json.dumps({"version": version, "artifacts": manifest}, indent=2) + "\n")
    (dist / "RELEASE_NOTES.md").write_text(notes(version, rows))


def notes(version, rows):
    result = [f"# PinChat {version}", "", "Закрепляйте сообщения, управляйте группами и двигайтесь с открытым специальным чатом.",
              "", "## Выберите файл", "", "| Minecraft | Java | Загрузчики |", "|---|---|---|"]
    for game, target in targets().items():
        result.append(f"| {game} | {target['java']} | {', '.join(target['loaders'])} |")
    changelog = ROOT / "CHANGELOG.md"
    if changelog.exists():
        text = changelog.read_text()
        marker = f"## {version}\n"
        if marker in text:
            section = text.split(marker, 1)[1].split("\n## ", 1)[0].strip()
            # Local relative Markdown links cannot be resolved on a GitHub Release page.
            section = section.replace("](" + "docs/", "](https://github.com/ivanmikhaylov1/pinchat-mod/blob/master/docs/")
            index = result.index("## Выберите файл")
            result[index:index] = ["## Изменения", "", section, ""]
    result += ["", f"Скачайте `pinchat-mod-<загрузчик>-{version}-mc<версия игры>.jar` из Assets ниже.",
               "Файлы 26.1.1 и 26.1.2 — идентичные копии сборки 26.1; Quilt использует Fabric-совместимый бинарник.",
               "", "## Установка", "", "1. Установите загрузчик для своей версии Minecraft.",
               "2. Положите один подходящий JAR PinChat в `mods` игрового профиля.",
               "3. На Fabric и Quilt установите Fabric API для точной версии игры; Forge и NeoForge не требуют дополнительных модов.",
               "", "## Управление", "", "Откройте обычный чат: ПКМ закрепляет сообщение, Shift + ПКМ создаёт группу.",
               "Перетаскивайте группы ЛКМ, меняйте масштаб за ↘, сворачивайте по заголовку; [R] — имя, [X] — удалить.",
               "`U` открывает специальный режим с движением и камерой; `P` — настройки. Данные сохраняются в `config/pinchat.json`.",
               "В мультиплеере P конфликтует с социальным меню: переназначьте настройки PinChat, например на F8, в управлении Minecraft.", "", "## Проверки", "", "Публикация этого релиза разрешена workflow только после сборки, unit-тестов, клиентского GameTest и всей матрицы игровых проверок упакованных JAR.",
               "Игровой прогон проверяет закрепление, группы, перетаскивание, масштабирование, переименование, движение, переключатель настроек и сохранение после перезапуска клиента.",
               "`SHA256SUMS` содержит SHA-256 всех JAR; `manifest.json` — версии загрузчиков и соответствие файлов игре.",
               "", "## Ограничения", "", "Forge 26.x и Minecraft 26.3 не поддерживаются.",
               "", "[Инструкция](https://github.com/ivanmikhaylov1/pinchat-mod#readme) · [Сообщить об ошибке](https://github.com/ivanmikhaylov1/pinchat-mod/issues/new/choose)", ""]
    return "\n".join(result)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--matrix", action="store_true")
    parser.add_argument("--dist", type=pathlib.Path, default=ROOT / "dist")
    parser.add_argument("--tag")
    args = parser.parse_args()
    if args.matrix:
        print(json.dumps({"include": matrix()}))
    else:
        version = properties(ROOT / "gradle.properties")["mod_version"]
        prepare(args.dist, version, args.tag)
        print(f"Prepared {len(matrix())} release JARs with SHA-256 checksums")


if __name__ == "__main__":
    main()
