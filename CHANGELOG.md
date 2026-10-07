**English** · [Русский](CHANGELOG.ru.md)

# Changelog

## 3.2.0

- Fixed special-chat camera turning on GLFW builds by forwarding native mouse deltas; both input backends respect X/Y inversion.
- Add dedicated Minecraft 26.3 builds for Fabric, Quilt and NeoForge, with SDL3 keyboard/mouse input, cursor capture, resource format 97.1, and packaged-JAR gameplay coverage.
- Fixed invisible settings and rename-screen titles by using opaque text colors and drawing titles after the base screen.
- Fixed missing bundled translations and icons in Fabric/Quilt 26.x; release preparation now rejects JARs without English/Russian locales.
- Conflict-free defaults on every supported loader: F8 for settings and F9 for special chat; existing custom bindings are retained.
- Removed unused Forge/NeoForge key actions and added the localized PinChat controls category.
- Fixed mouse-bound and unbound controls in special chat, and original input restoration after screen resizing.
- Gameplay checks now exercise actual defaults and saved custom keyboard/mouse bindings after restart.
- English-first user README with a complete Russian translation, installation, controls, and an in-game example.
- Separate bilingual guides for development, testing, compatibility, and releases.
- English and Russian issue/PR templates and descriptions for all published releases.
- Tests for limits, groups, normalization, and configuration persistence without hiding initialization failures.
- Fabric 1.21.11 Client GameTest and native gameplay checks of packaged release JARs across all 19 supported game/loader combinations.
- Checks for dragging, resizing, movement, camera turning, and persistence after a real client restart.
- Consistent release filenames, SHA-256 checksums, manifest, and gameplay reports tied to the tested JAR.
- Release publication gated on the complete gameplay matrix, with English and Russian release notes.

## 3.1.0

Support for Minecraft 1.21.11, 26.1–26.1.2, and 26.2 on available loaders. See the [release notes](docs/releases/3.1.0.md).

Historical descriptions and original changes for 1.0.0–3.0.0 are in [docs/releases](docs/releases).
