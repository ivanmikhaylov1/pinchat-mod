# PinChat

Pin chat messages on the HUD, organize them into movable groups, and optionally move while the chat screen is open.

## Supported versions and loaders

| Minecraft | Java | Fabric | Quilt | Forge | NeoForge |
|---|---:|:---:|:---:|:---:|:---:|
| 1.21.11 | 21 | ✓ | ✓* | ✓ | ✓† |
| 26.1 | 25 | — | — | — | ✓† |

\* The Quilt artifact is the same binary as Fabric, repackaged with a loader-specific filename. Quilt Loader supports Fabric mods and Fabric API. CI verifies its metadata, entrypoint and mixin classes, but does not launch a Quilt client. The current Gradle wrapper and Quilt Loom versions could not provide a reliable Quilt dev launch: Fabric Loom's Quilt launch failed during Minecraft bootstrap, and recent Quilt Loom requires Gradle 9. A manual Quilt client check is required before release.

† CI launches both NeoForge clients under Xvfb after `buildAll` and requires the PinChat client setup event, completed GUI atlas and a visible Minecraft window for 15 seconds. This checks startup, not in-game interactions. The same script passed locally for 1.21.11 and 26.1 using the render-ready log checkpoint and a live client process.

Minecraft 26.1.1, 26.1.2, 26.2 and 26.3 are released, but **PinChat does not currently support them**. Minecraft 26.1 introduced Java 25 and unobfuscated game binaries; 26.3 changed input handling to SDL3. Each additional version requires version-specific client code and runtime checks before it can be added to this table.

## Features and usage

- Open chat with `T` or `/`, then **right-click a message** to pin it in the default group. Right-click it again to unpin it.
- **Shift + right-click a message** to create a new group containing that message in one action.
- **Drag a group** with the left mouse button to place it on screen. Click its header to collapse or expand it; hover over the group for rename and delete buttons. Right-click a pinned line to remove it.
- Press **U** to open the moveable chat screen. Press **U** again or **Esc** to close it. Movement uses your normal game keys.
- Press **P** for PinChat settings. The built-in screen has one switch: moveable chat on or off. It works without any configuration mod. On Fabric, ModMenu can also open this same screen if installed.

PinChat stores groups, positions and the moveable chat switch in `config/pinchat.json`. Existing files retain their saved groups and positions. Older numeric fields remain readable for compatibility but are no longer exposed as settings.

## Installation

Choose **one** JAR matching your Minecraft version and loader, then put it in `.minecraft/mods`:

- **Fabric:** Fabric Loader 0.18.4 or newer and Fabric API for 1.21.11.
- **Quilt:** Quilt Loader and Fabric API for 1.21.11. Use the Quilt artifact; it contains the Fabric-compatible mod binary.
- **Forge:** Forge 1.21.11-61.0.2 or newer in the 61.x series.
- **NeoForge:** NeoForge 21.11.38-beta or newer in the 21.11 series.
- **NeoForge 26.1:** NeoForge 26.1.0.19-beta and the `mc26.1` JAR.

Minecraft 1.21.11 requires Java 21; Minecraft 26.1 requires **Java 25**. No Cloth Config, YACL, MaLiLib, or ModMenu installation is required.

## Build

```bash
./gradlew buildAll
```

This command runs the common and Fabric tests, builds all four 1.21.11 loader artifacts, compiles the Java 25 toolchain probe, and assembles the NeoForge 26.1 artifact. NeoForge is assembled without NeoGradle's in-game JUnit task because these client-only modules have no game tests; that task downloads the complete Minecraft asset set. Output JARs are in `fabric/build/libs/`, `quilt/build/libs/`, `forge/build/libs/`, `neoforge/build/libs/` and `mc26_1/build/libs/`.

The build needs JDK 21 and JDK 25 installed. The root Gradle build runs on JDK 21 and selects JDK 25 for `java25-toolchain-check/`. The 26.1 module uses a nested Gradle 9.2 wrapper and Java 25 because NeoForge 26.1 requires Gradle 9.1 or newer. In CI, both Temurin versions are installed. Locally, set `JAVA_25_HOME` to the JDK 25 home if Gradle cannot discover it automatically.

CI also checks the packaged 1.21.11 and 26.1 NeoForge and Quilt-compatible JAR metadata, entrypoint classes and mixin classes. NeoForge client smoke tests run separately under Xvfb and fail if the client exits, PinChat's client event does not fire, the GUI atlas is not built, or the Minecraft window does not remain open. Quilt has no automated client launch yet; its metadata check cannot prove runtime compatibility.

Before a release, manually launch the Quilt artifact with Quilt Loader and Fabric API for Minecraft 1.21.11 and check:

1. The main menu opens without a loader, mixin or entrypoint error.
2. Right-click a chat line to pin and unpin it; Shift + right-click creates a group.
3. Drag a group, close and reopen the game, and confirm its position persists.
4. Press `P` and toggle moveable chat; press `U` and verify movement while chat is open.

Run the same interaction checks once on each NeoForge version. CI's NeoForge smoke tests cover startup only.

```text
common/src/main/java       shared model and config serialization
common/src/mojang/java     Minecraft client code shared by Forge and NeoForge
fabric/                    Fabric implementation (Yarn mappings)
quilt/                     Fabric-compatible Quilt artifact, no copied Java code
forge/                     Forge registration and configuration path
neoforge/                  NeoForge registration and configuration path
java25-toolchain-check/     empty Java 25 build probe for future 26.1 modules
mc26_1/                    NeoForge 26.1 sources and Java 25 / Gradle 9.2 build
```

### Minecraft 26.x migration plan

The 26.1 module shares the model in `common/src/main/java`, reuses the Mojang-mapped client source and NeoForge registration, and overlays only classes whose 26.1 APIs changed. Later 26.x versions need their own compatibility checks. SDL3 input work for 26.3 is a separate task.

### Babric / Minecraft Beta 1.7.3

Babric is a separate Java 8-era port, not a source set of this modern build. A small amount of model logic can be adapted: storing pinned message text, grouping messages and saving group positions. Chat HUD interception, mouse hit testing, rendering, key handling and the moveable chat screen need new implementations. Babric can use Mixins, but its game classes and available APIs do not match the modern chat and movement code. The [old Babric example](https://github.com/babric/babric-example-mod) is archived and points to the [StationAPI example](https://github.com/calmilamsy/stationapi-example-mod) as a starting point.

Estimate **2–4 weeks for one experienced modder** for a usable prototype with pinning and groups, plus **1–2 additional weeks** to investigate moveable chat and test compatibility with Babric mods. This is an engineering estimate, not a release commitment. A separate `babric` branch/repository should first establish a Beta 1.7.3 client build, then add chat interception and persistence, then groups and HUD dragging, and finally assess movement while typing. It does not block modern-loader releases.

## License

MIT. See [LICENSE](LICENSE).
