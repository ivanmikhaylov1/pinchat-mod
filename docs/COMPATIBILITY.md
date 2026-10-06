**English** · [Русский](ru/COMPATIBILITY.md)

# Compatibility

[← README](../README.md) · [Testing](TESTING.md)

## Available builds

| Minecraft | Java | Fabric | Quilt | Forge | NeoForge |
|---|---|---|---|---|---|
| 1.21.11 | 21 | Dedicated build | Compatible Fabric JAR | Forge 61.x | Dedicated build |
| 26.1 / 26.1.1 / 26.1.2 | 25 | Shared mc26.1 JAR | Same Fabric JAR | No port | Shared mc26.1 JAR |
| 26.2 | 25 | Dedicated mc26.2 JAR | Same Fabric JAR | No port | Dedicated mc26.2 JAR |
| 26.3 | — | No port | No port | No port | No port |

Fabric API is required on Fabric and Quilt and must match the exact game version. Quilt 26.x uses upstream Fabric API: [QFAPI development ended for 26.1](https://quiltmc.org/en/blog/2026-02-03-non-obfuscated-updates/).

## What the checks establish

Before gameplay coverage was expanded, CI built JARs and checked metadata. Historical smoke runs launched NeoForge 1.21.11, 26.1, 26.1.1, 26.1.2, and 26.2, plus Fabric/Quilt for each 26.x version. They required PinChat initialization, a GUI atlas, and a stable window for 15 seconds.

Quilt 1.21.11 previously had automatic packaging/metadata checks and a manual startup confirmed by the project owner. The owner also reported a manual gameplay pass for release 3.1.0. These are historical results, not validation of new changes.

Client GameTest checks the Fabric 1.21.11 development client. The additional CI matrix from `config/targets.json` runs native gameplay checks on packaged JARs across all 16 supported combinations, including Quilt, Forge, NeoForge, and 26.1.x hotfixes. See [TESTING.md](TESTING.md) for scenarios and coverage limits.

## Hotfixes in 26.1.x

The 26.1 JAR is unchanged on 26.1.1 and 26.1.2. Fabric’s range is `>=26.1 <26.2`; NeoForge’s is `[26.1,26.2)`. Gameplay tests run each game version with the same base JAR. Earlier CI also compared all 23 compiled NeoForge classes with the 26.1 JAR; the script remains in `scripts/verify_neoforge_mc26_hotfix_bytecode.py`.

An earlier local NeoForge 26.1.2 launch on macOS failed to obtain a GLFW monitor; Linux/Xvfb startup passed. Neither observation alone establishes correct in-world clicks.

26.2 has separate builds because screen/HUD APIs and resource formats changed. Official notes: [26.1.1](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-1), [26.1.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-1-2), [26.2](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-2).

## Unsupported targets

- Forge 26.x: a Java 25 port is not implemented; the 1.21.11 JAR is incompatible.
- Minecraft 26.3: [SDL3 replaces GLFW](https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3). PinChat reads GLFW directly, so input adaptation and new gameplay checks are needed.
- Babric / Beta 1.7.3: requires a separate Java 8 build and new chat, HUD, and input integration.

No port release dates are set. Claim new compatibility only after a build, startup check, and corresponding gameplay pass.
