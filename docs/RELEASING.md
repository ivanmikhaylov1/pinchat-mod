**English** · [Русский](ru/RELEASING.md)

# Releasing

[← README](../README.md) · [Testing](TESTING.md)

## Preparation

1. Update `mod_version` in root `gradle.properties`, `fabric-mc26/gradle.properties`, and `neoforge-mc26/gradle.properties`.
2. For a new game or loader, update `config/targets.json` and its build. The matrix drives tests, JAR names, compatibility tables, and the manifest.
3. Run unit tests, `./gradlew buildAll`, and `python3 scripts/verify_loader_jars.py`.
4. Run `python3 scripts/release.py --tag v<mod_version>` and review both languages in `dist/RELEASE_NOTES.md`. The script rejects mismatched tags, inconsistent nested-project versions, incorrect JAR versions, bundled test code, and missing English/Russian translations.
5. Check gameplay and visual quality. Write user-facing changes in both `CHANGELOG.md` and `CHANGELOG.ru.md`; archive release descriptions in `docs/releases`. Do not substitute a technical commit list for user-facing notes.
6. After review, changes can go to the default branch with a `v<mod_version>` or `<mod_version>` tag. The preparation script itself neither pushes tags nor publishes a GitHub Release.

## What players receive

- `pinchat-mod-<loader>-<version>-mc<minecraft>.jar`: explicit mod, loader, and game version.
- Separate filenames for 26.1.1 and 26.1.2, even though their bytes match 26.1.
- `SHA256SUMS`: SHA-256 hashes for all 16 current matrix files.
- `manifest.json`: mod, Java, loader, and Fabric API versions; base binary version; filename and SHA-256.
- `GAMEPLAY_RESULTS.json`: results for every combination, including the tested JAR’s SHA-256.
- English-first release notes with a complete Russian section: file selection, dependencies, installation, controls, validation, and limitations.

Verify all downloaded JARs on Linux with `sha256sum --check SHA256SUMS`, or on macOS with `shasum -a 256 --check SHA256SUMS`. If you downloaded one JAR, compare its hash with the corresponding line.

## Publication gates

`build.yml` separates builds, gameplay checks, and publication. Only the release job has GitHub write permissions. It runs for a tag after successful build and gameplay jobs, checks hashes, and compares the complete JSON report set with the manifest. A report for another JAR does not validate the file being published.

Tags with a suffix, such as `v3.2.0-beta.1`, create a prerelease. Plain version tags create stable releases. The version inside each JAR must match the tag after removing its leading `v`.

## Historical releases

Published descriptions are archived in `docs/releases`, with English first and Russian below an explicit language anchor. Preserve historical binaries when updating presentation. Do not attribute new automated tests to old versions without a separate run.

Metadata in 1.0.0 and 1.1.0 says `~1.21.1`, while the original release title says 1.21.10. Version 2.0.1 uses `1.21.11.x` instead of an explicit pre1–pre5 range and is marked as a prerelease. The notes explain these discrepancies; original JARs are preserved.
