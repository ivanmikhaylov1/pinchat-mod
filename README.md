# PinChat

[![Build and Release](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml/badge.svg)](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml)

PinChat pins Minecraft chat messages on screen, organizes them into draggable groups, and lets you move while chat is open. It helps players keep coordinates, instructions, or a conversation visible. Groups can be moved and scaled, and normal use requires no configuration mod.

## Supported Minecraft versions

“Supported” means the JAR builds and passes the automated checks below. In-world mouse interaction and saved positions still need the [release check](#release-check).

| Minecraft | Java | Fabric | Quilt | Forge | NeoForge |
|---|---:|---|---|---|---|
| 1.21.11 | 21 | Supported | Supported via Fabric JAR; manual client check pending | Supported | Supported |
| 26.1 | 25 | Supported | Identical Fabric JAR | Unsupported: no Java 25 port | Supported |
| 26.1.1 | 25 | Supported with 26.1 JAR | Supported with 26.1 Fabric JAR | Unsupported: no Java 25 port | Supported with 26.1 JAR |
| 26.1.2 | 25 | Supported with 26.1 JAR | Supported with 26.1 Fabric JAR | Unsupported: no Java 25 port | Supported with 26.1 JAR |
| 26.2 | 25 | Supported | Identical 26.2 Fabric JAR | Unsupported: no Java 25 port | Supported |
| 26.3 | — | Unsupported: SDL3 port pending | Unsupported: SDL3 port pending | Unsupported: no port | Unsupported: SDL3 port pending |

The 26.1 JAR is reused unchanged on 26.1.1 and 26.1.2. Fabric metadata accepts `>=26.1 <26.2`; NeoForge accepts `[26.1,26.2)`. The workflow boots the packaged Fabric JAR on Fabric and Quilt for each 26.x version, boots NeoForge on each hotfix loader, and compares all 23 compiled NeoForge classes against the 26.1 release JAR. The 26.2 JARs are built separately because game screen/HUD APIs and pack formats changed. See the official [26.1.1](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-1), [26.1.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-2), and [26.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-2) release notes.

## Install

1. Install the matching game version and **one** loader from the table. Use [Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21) for 1.21.11 or [Temurin JDK 25](https://adoptium.net/temurin/releases/?version=25) for 26.1–26.2. Configure your launcher to run the matching Java version.
2. Download the corresponding PinChat JAR from [GitHub Releases](https://github.com/ivanmikhaylov1/pinchat-mod/releases) or a successful [Build and Release run](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml) under **Artifacts**. Use the `mc26.1` JAR for both hotfixes. For Quilt 26.x use the Fabric JAR; the CI `quilt` artifact is a byte-identical alias.
3. Put **one** PinChat JAR in your instance's `mods/` folder (usually `.minecraft/mods`). On Fabric or Quilt, also install [Fabric API](https://modrinth.com/mod/fabric-api) for the **exact** game version. Fabric API is required; Cloth Config, YACL, MaLiLib, and ModMenu are not.

Install the loader normally before copying mods. The 1.21.11 Forge build targets Forge 61.x; the 26.1 and 26.2 NeoForge builds target their matching loader series. Optional ModMenu on Fabric 1.21.11 can open the built-in settings screen.

| Loader | PinChat JAR | Additional mod |
|---|---|---|
| Fabric | Fabric JAR for the game version | Fabric API for that exact version |
| Quilt | Quilt alias on 1.21.11; Fabric JAR or its identical Quilt alias on 26.x | Fabric API for that exact version |
| Forge | Forge 1.21.11 JAR only | None required |
| NeoForge | NeoForge JAR for the game version | None required |

## Use PinChat

| Action | Control |
|---|---|
| Pin or unpin a message in the default group | Open chat (`T` or `/`), then right-click the message |
| Create a group containing a message | Shift + right-click the message |
| Move a pinned group | Drag it with the left mouse button |
| Scale a pinned group | Drag the ↘ handle at its bottom-right corner |
| Collapse or expand a group | Click its header |
| Remove a pinned line | Right-click that line in its group |
| Open or close moveable chat | `U` to open; `U` again or `Esc` to close |
| Switch moveable chat on or off | `P` opens the built-in one-switch settings screen |

Hover over a group for rename and delete buttons. Normal movement keys work while moveable chat is open. Groups, positions, and the switch are saved in `config/pinchat.json`; older numeric settings remain readable but are not exposed in the UI.

## Known limits

- **Forge 26.x:** no Java 25 Forge port exists. Its 1.21.11 JAR cannot be used on 26.x.
- **Minecraft 26.3:** [SDL3 replaces GLFW for input/window handling](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3). PinChat directly reads GLFW mouse/key state, so it needs a separate input port and interaction tests. Estimate: 4–8 development days plus 2–3 testing days; this is not a release promise.
- **Quilt 1.21.11:** it packages the Fabric binary, but a manual client run remains before release. For 26.x, Quilt Loader 0.30.1 and upstream Fabric API booted the same Fabric JAR in local and CI smoke tests. [QFAPI was retired for 26.1](https://quiltmc.org/en/blog/2026-02-03-non-obfuscated-updates/).
- **NeoForge 26.1.2:** all 23 compiled classes match the 26.1 JAR byte for byte. A local macOS startup attempt failed to obtain a primary monitor from GLFW. The packaged mod did pass a direct client startup check with the 26.1.2 loader under Linux/Xvfb in CI. Neither result verifies in-world clicks.
- **Babric / Beta 1.7.3:** a separate Java 8 branch/repository is needed. Message text, groups, and saved positions can be adapted; interception, rendering, hit testing, key handling, and moveable chat need new code. Estimate: 2–4 weeks for pinning/groups and 1–2 more weeks to investigate moveable chat and mod compatibility. First establish a Beta client build, then add capture/persistence, groups and dragging, and finally assess movement while typing.

## Release check

CI builds and packages every supported JAR, verifies metadata, boots NeoForge 1.21.11/26.1/26.1.1/26.1.2/26.2, and boots Fabric and Quilt 26.1/26.1.1/26.1.2/26.2 under Xvfb. Startup must invoke PinChat, build the GUI atlas, and keep a visible window alive for 15 seconds. It does not drive mouse interaction or check persistence.

Perform **one full interaction pass per unique JAR**: the shared Fabric/Quilt 1.21.11 binary, Forge 1.21.11, NeoForge 1.21.11, the shared Fabric/Quilt 26.1 binary, NeoForge 26.1, the shared Fabric/Quilt 26.2 binary, and NeoForge 26.2 (**seven passes total**). The three 26.1.x versions share their platform's release JAR and have separate automated startup tests, so do not repeat the full interaction pass for each hotfix. Because CI does not boot Quilt 1.21.11, also launch that loader once and confirm PinChat initializes; its binary does not need a second full interaction pass. For each full pass:

1. Enter a world; right-click to pin/unpin a chat line and Shift + right-click to create a group.
2. Drag a group, restart the client, and verify its position and contents persist.
3. Press `P`, toggle moveable chat, then press `U` and verify movement while chat is open.
4. Check the client log for mixin, entrypoint, and loader errors.

## Develop and build

Install JDK 21 and 25, then run:

```bash
./gradlew buildAll
python3 scripts/verify_loader_jars.py
```

Gradle uses Java 21 for the root 1.21.11 build and Java 25 for the nested 26.x builds. If it cannot find Java 25, set `JAVA_25_HOME` to its JDK directory. Artifacts appear under each loader's `build/libs/` and under `fabric-mc26/build/libs/` and `neoforge-mc26/build/libs/`; CI copies release JARs to `dist/`. [Build and Release](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml) runs `buildAll`, metadata/bytecode checks, and client smoke tests.

### Module layout

```text
common/src/main/java       Shared model and config serialization
common/src/mojang/java     Mojang-named client code shared by 26.x builds
fabric/                    Fabric 1.21.11 (Yarn mappings)
quilt/                     Quilt 1.21.11 Fabric-compatible artifact
forge/                     Forge 1.21.11 registration
neoforge/                  NeoForge 1.21.11 registration, reused by 26.x
java25-toolchain-check/     Java 25 toolchain probe
fabric-mc26/               Fabric 26.1.x and 26.2; Quilt uses its JAR
neoforge-mc26/             NeoForge 26.1.x and 26.2; Gradle 9.2 wrapper
scripts/                   Metadata, bytecode, and client-startup checks
.github/workflows/build.yml CI build, smoke tests, release artifacts
```

Platform names come first; `-mc26` marks the additional multi-version build. The original platform directories remain 1.21.11 modules. The 26.x builds reuse `common`, with loader bindings and version-specific API adaptations. There is no Quilt 26.x module because it uses the identical Fabric JAR.

### Add a game version or loader

1. Read the official game/loader migration notes; record JDK, mappings, and API changes.
2. For a hotfix, check packaged dependency ranges, compile against the patch, compare class bytes, and run its client smoke test before declaring compatibility.
3. For a breaking version, add bindings/adaptations to `fabric-mc26/` or `neoforge-mc26/` while keeping reusable logic in `common/`; create a module only if the existing build cannot represent the target cleanly.
4. Add version parameters and a Gradle task to root `buildAll`; keep the JDK selection local to the target.
5. Extend script checks, the CI smoke matrix, and the distinctly named release JAR copy in `.github/workflows/build.yml`.
6. Run `./gradlew buildAll`, metadata checks, and client smoke tests; perform the interaction checklist once per unique binary.
7. Update the support table and install instructions only when the evidence supports the claim.

## License

MIT. See [LICENSE](LICENSE).
