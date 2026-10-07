**English** · [Русский](ru/DEVELOPMENT.md)

# Developing PinChat

[← README](../README.md) · [Testing](TESTING.md) · [Compatibility](COMPATIBILITY.md)

## Quick start

Install JDK 21 for Minecraft 1.21.11 and JDK 25 for a complete build. Use the repository’s Gradle Wrapper; a separate Gradle installation is unnecessary.

```bash
# Model without Minecraft or loaders
./gradlew -PtestPlatform=common :common:test

# Fabric 1.21.11
./gradlew -PtestPlatform=fabric :fabric:build

# All platforms and versions
./gradlew buildAll
python3 scripts/verify_loader_jars.py
```

Run commands from the repository root. On Windows, use `gradlew.bat`. Gradle runs on Java 21 and selects Java 25 for the corresponding modules. If detection fails, set `JAVA_25_HOME` or `JAVA_HOME_25_X64`.

`testPlatform` limits included modules to `common` or `common` + `fabric`. Omit it for a complete build. Model tests need only JDK 21 and Maven Central.

## Where to change code

| Path | Purpose |
|---|---|
| `common/src/main/java` | Group model, limits, and serialization without Minecraft dependencies |
| `common/src/mojang/java` | Client code using Mojang mappings for Forge/NeoForge and 26.x |
| `fabric/src/main/java` | Fabric 1.21.11 using Yarn mappings: screens, mixins, input |
| `fabric/src/test/java` | JUnit tests without launching the game |
| `fabric/src/gametest` | Separate test mod for the Fabric 1.21.11 client |
| `forge`, `neoforge` | Loader registration for 1.21.11 |
| `quilt` | Packaging the compatible Fabric build for 1.21.11 |
| `fabric-mc26`, `neoforge-mc26` | Nested Gradle projects for 26.1.x and 26.2 |
| `java25-toolchain-check` | Java 25 validation |
| `scripts` | Packaged-JAR gameplay checks, metadata, bytecode, and release preparation |
| `config/targets.json` | Shared matrix of game, Java, loader, and Fabric API versions |
| `.github/workflows` | Builds, tests, diagnostics, and releases |

Some client behavior has both Yarn and Mojang implementations: check both when fixing behavior. `PinnedMessagesManager` is the shared model; client `PinnedMessages` still has a separate implementation. Real-client tests therefore complement JUnit.

In 26.x, `syncModSources` combines sources and adapts APIs for the game version. Edit source files, **not** `build/generated`. The order of `from` entries in `build.gradle` determines precedence for duplicate classes.

## Builds

Set the mod version in `gradle.properties` and keep the nested 26.x projects’ properties in sync. Loader/API versions are also in properties; plugin versions are in `build.gradle`.

Module JARs appear in their `build/libs`. CI collects user downloads in `dist` with loader and game versions in the filenames. Quilt 26.x is an identical copy of the Fabric JAR.

`buildAll` builds 26.1 and 26.2 sequentially within each nested project. Do not build different versions of one project concurrently: they share the same `build` directory.

## Adding a game version

1. Review changes to Java, mappings, input, GUI, and resource formats.
2. For a hotfix, check metadata ranges, compilation against the patch, bytecode, and client startup.
3. Adapt new APIs in the 26.x project. Add a separate module only when the architecture requires it.
4. Update `buildAll` and `config/targets.json`: CI, filenames, and release notes use this matrix. Check `scripts/release.py --matrix`.
5. Complete the [pre-release checklist](TESTING.md#before-a-release), then update compatibility in both languages.

## Documentation and translations

The README is for players. Build commands and architecture belong here; tests belong in `TESTING.md`, platform limitations in `COMPATIBILITY.md`.

English is primary. Maintain root `*.ru.md` files and matching `docs/ru/` guides alongside English changes. Release pages and generated notes contain both languages. Keep `en_us.json` and `ru_ru.json` key sets aligned for every loader; 26.x builds reuse the source resource directories.
