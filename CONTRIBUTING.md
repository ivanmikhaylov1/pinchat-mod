**English** · [Русский](CONTRIBUTING.ru.md)

# Contributing

Submit fixes and suggestions through [issues](https://github.com/ivanmikhaylov1/pinchat-mod/issues) and pull requests. English and Russian are both welcome; use English first when preparing public documentation.

1. Read the [module map and build commands](docs/DEVELOPMENT.md).
2. Keep each change focused on one task. Put game-independent logic in `common/src/main/java`.
3. Check observable behavior: JUnit for the model and [Client GameTest](docs/TESTING.md) for screens and input.
4. Check affected code in both Yarn and Mojang mappings. Do not edit generated files.
5. Update both English and Russian instructions when controls, dependencies, or behavior change.
6. Describe the problem, resulting behavior, validation, and limitations in your PR.

Minimum check: `./gradlew -PtestPlatform=common :common:test`. For Fabric, also run unit tests and Client GameTest. Shared client changes require builds and gameplay checks for affected loaders; use a manual pass where automated coverage is missing.

Follow `.editorconfig` and the surrounding code style. Avoid unrelated bulk formatting in functional changes. Do not commit game worlds, logs, local settings, or built JARs.

English documents use the default filenames. Russian documents use `*.ru.md` at the root and `docs/ru/` for guides. Keep language links and matching instructions up to date. Historical release pages in `docs/releases/` and generated release notes contain English first, followed by Russian. Update both `CHANGELOG.md` and `CHANGELOG.ru.md` for release changes.

Code is licensed under [MIT](LICENSE).
