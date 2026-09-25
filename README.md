# PinChat

Pin chat messages on the HUD, organize them into movable groups, and optionally move while the chat screen is open.

## Supported versions and loaders

| Minecraft | Java | Fabric | Quilt | Forge | NeoForge |
|---|---:|:---:|:---:|:---:|:---:|
| 1.21.11 | 21 | ✓ | ✓* | ✓ | ✓ |
| 26.1 | 25 | ✓ | ✓† | — | ✓ |
| 26.1.1 | 25 | ✓‡ | ✓‡ | — | ✓‡ |
| 26.1.2 | 25 | ✓‡ | ✓‡ | — | ✓‡ |
| 26.2 | 25 | ✓ | ✓† | — | ✓ |
| 26.3 | 25 | — | — | — | — |

\* The 1.21.11 Quilt artifact is the Fabric binary with a loader-specific filename. Quilt 1.21.11 still needs a manual client check before release.

† Quilt 26.x uses the **identical Fabric JAR**. Quilt Loader 0.30.1 accepts Fabric mod metadata and the upstream Fabric API; the previous Quilted Fabric API was [retired starting with 26.1](https://quiltmc.org/en/blog/2026-02-03-non-obfuscated-updates/). Local Quilt client smoke tests reached the GUI atlas with PinChat's entrypoint on 26.1, 26.1.1, 26.1.2 and 26.2. The CI workflow repeats these startup checks.

‡ Use the **26.1 JAR** from the [Build and Release artifacts](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml) for both hotfixes. The Fabric/Quilt filename is `pinchat-mod-fabric-3.0.0-mc26.1.jar` (or its byte-identical Quilt alias); the NeoForge filename is `pinchat-mod-neoforge-3.0.0-mc26.1.jar`. Fabric 26.1.1 and 26.1.2, and Quilt 26.1.2, were launched locally with the exact same 26.1 PinChat JAR. The metadata accepts `>=26.1 <26.2` on Fabric/Quilt and `[26.1,26.2)` on NeoForge. On NeoForge 26.1.1, all 23 compiled class files matched the 26.1 release JAR byte for byte and the client passed a startup smoke test. The same comparison and launch check is run for 26.1.2 in CI.

[Minecraft 26.1.1](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-1) fixes a chat reporting bug; [26.1.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-2) is another hotfix. Neither official release note announces new obfuscation or a modding API migration. The actual 26.1 binary is exercised on hotfix clients because release notes alone cannot prove compatibility. [Minecraft 26.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-2) changes the resource pack version to 88.0 and client screen/HUD APIs used by PinChat, so it has separately compiled Fabric and NeoForge JARs built from the same shared code.

The official client JARs contain `version.json`. Their values, checked from downloaded binaries, are:

| Minecraft | Protocol | World data version | Data pack | Resource pack |
|---|---:|---:|---:|---:|
| 26.1 | 775 | 4786 | 101.1 | 84.0 |
| 26.1.1 | 775 | 4788 | 101.1 | 84.0 |
| 26.1.2 | 775 | 4790 | 101.1 | 84.0 |
| 26.2 | 776 | 4903 | 107.1 | 88.0 |

The hotfixes change the world data number but keep the protocol and pack formats. All four ship unobfuscated game classes and require Java 25; [Fabric Loom uses the official unobfuscated names](https://www.fabricmc.net/2026/03/14/261.html) without a separate mappings artifact. These values inform the compatibility decision; actual client startup and bytecode comparisons provide the runtime evidence.

Forge 26.x releases exist, but PinChat has no Forge 26.x port. A 1.21.11 Forge JAR is **not** compatible with 26.x; Forge needs a separate Java 25 client registration and runtime port. Minecraft 26.3 is outside this release because its [SDL3 input migration](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3) replaces the GLFW input used by PinChat.

## Features and usage

- Open chat with `T` or `/`, then **right-click a message** to pin it in the default group. Right-click it again to unpin it.
- **Shift + right-click a message** to create a new group containing that message in one action.
- **Drag a group** with the left mouse button to place it on screen. Click its header to collapse or expand it; hover over the group for rename and delete buttons. Right-click a pinned line to remove it.
- Press **U** to open the moveable chat screen. Press **U** again or **Esc** to close it. Movement uses your normal game keys.
- Press **P** for PinChat settings. The built-in screen has one switch: moveable chat on or off. It works without any configuration mod. On Fabric, ModMenu can also open this same screen if installed.

PinChat stores groups, positions and the moveable chat switch in `config/pinchat.json`. Existing files retain their saved groups and positions. Older numeric fields remain readable for compatibility but are no longer exposed as settings.

## Installation

Choose **one** JAR matching your Minecraft version and loader, then put it in `.minecraft/mods`:

- **1.21.11:** use the platform-specific 1.21.11 JAR and Java 21. Fabric and Quilt need Fabric API for 1.21.11.
- **26.1 / 26.1.1 / 26.1.2:** use the `mc26.1` JAR and Java 25. Fabric Loader 0.19.5 or Quilt Loader 0.30.1 also need Fabric API for the exact game patch version. NeoForge uses the matching 26.1.x loader; PinChat was compiled against 26.1.0.19-beta.
- **26.2:** use the `mc26.2` JAR and Java 25. Fabric Loader 0.19.5 or Quilt Loader 0.30.1 need Fabric API for 26.2. The NeoForge build targets 26.2.0.88.
- **Forge:** supported only on 1.21.11 (Forge 61.x).

On Quilt 26.x, select the Fabric 26.x JAR. CI provides a second `quilt` filename containing the same bytes for convenience. No Cloth Config, YACL, MaLiLib or ModMenu installation is required.

## Build

```bash
./gradlew buildAll
```

The command builds all existing 1.21.11 loaders, plus Fabric/Quilt-compatible and NeoForge artifacts for 26.1.x and 26.2. The 26.x builds share `common/src/main/java` and `common/src/mojang/java`; only loader bindings and version-specific API adaptations differ. The root build runs on Java 21, and the nested Gradle 9.2 builds select Java 25. Install both JDKs; if Gradle cannot discover Java 25, set `JAVA_25_HOME`.

Output JARs are in `fabric/build/libs/`, `quilt/build/libs/`, `forge/build/libs/`, `neoforge/build/libs/`, `fabric26_1/build/libs/` and `mc26_1/build/libs/`. `mc26_1/` also builds the distinct 26.2 NeoForge target through version parameters. CI copies release artifacts to `dist/` with unambiguous version and loader names.

CI validates packaged metadata and mixin classes, then launches NeoForge 1.21.11/26.1/26.2 under Xvfb. It also launches the **packaged** Fabric JAR on Fabric 26.1, 26.1.1, 26.1.2 and 26.2, and on Quilt 26.1, 26.1.1, 26.1.2 and 26.2 with Fabric API. NeoForge hotfix runs also compare their compiled class files with the 26.1 JAR to ensure the distributed binary is unchanged. Each client must invoke the PinChat entrypoint, build the GUI atlas and keep a visible window alive for 15 seconds. These are startup checks; they cannot prove mouse interaction or persistence.

Before each release, on each advertised 26.x loader and game version:

1. Open a world, right-click a chat line to pin and unpin it, and Shift + right-click to create a group.
2. Drag a group, restart the game and verify its position persists.
3. Press `P`, toggle moveable chat, then press `U` and check movement while chat is open.
4. Confirm no mixin, event bus or loader errors in the client log. Smoke tests cover startup only; repeat the in-game interaction checks on NeoForge 26.1.1 and 26.1.2 before release.

```text
common/src/main/java       shared model and config serialization
common/src/mojang/java     Minecraft client code shared by Java 25 targets
fabric/                    Fabric 1.21.11 implementation (Yarn mappings)
quilt/                     1.21.11 Fabric-compatible Quilt artifact
forge/                     Forge 1.21.11 registration
neoforge/                  NeoForge registration reused by Java 25 target
java25-toolchain-check/     Java 25 build probe
fabric26_1/                Fabric 26.1.x and 26.2 Java 25 / Gradle 9.2 builds
mc26_1/                    NeoForge 26.1.x and 26.2 Java 25 / Gradle 9.2 builds
```

### Minecraft 26.3 port estimate

[26.3 replaces GLFW with SDL3](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3) for windows and input. PinChat directly reads GLFW mouse state in its chat mixin and uses GLFW key codes for shortcuts, so a simple dependency bump is insufficient. Estimate **4–8 development days plus 2–3 days of platform and mouse interaction testing** for Fabric/Quilt and NeoForge. This is an estimate, not a release commitment; the work belongs in a separate task.

### Babric / Minecraft Beta 1.7.3

Babric is a separate Java 8-era port, not a source set of this modern build. A small amount of model logic can be adapted: storing pinned message text, grouping messages and saving group positions. Chat HUD interception, mouse hit testing, rendering, key handling and the moveable chat screen need new implementations. Babric can use Mixins, but its game classes and available APIs do not match the modern chat and movement code. The [old Babric example](https://github.com/babric/babric-example-mod) is archived and points to the [StationAPI example](https://github.com/calmilamsy/stationapi-example-mod) as a starting point.

Estimate **2–4 weeks for one experienced modder** for a usable prototype with pinning and groups, plus **1–2 additional weeks** to investigate moveable chat and test compatibility with Babric mods. This is an engineering estimate, not a release commitment. A separate `babric` branch/repository should first establish a Beta 1.7.3 client build, then add chat interception and persistence, then groups and HUD dragging, and finally assess movement while typing. It does not block modern-loader releases.

## License

MIT. See [LICENSE](LICENSE).
