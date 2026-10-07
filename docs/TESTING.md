**English** · [Русский](ru/TESTING.md)

# Testing

[← README](../README.md) · [Development](DEVELOPMENT.md)

## Coverage levels

| Level | What it verifies | Outside its scope |
|---|---|---|
| JUnit, `common` | Limits, groups, normalization, disk persistence, migration | Mixins, clicks, rendering |
| JUnit, `fabric` | Model and adapters outside the game process | Minecraft startup and world loading |
| Client GameTest, Fabric 1.21.11 | A real world, clicks through ChatScreenMixin, keys, movement, configuration loading | Other loaders, JVM restart, image comparison |
| Native gameplay, full matrix | Packaged JAR, dragging, resizing, real restart, server coordinates | Visual comparison with a reference |
| Manual pass | Visual quality and compatibility with other mods | Automatic regression protection |

## Quick tests

```bash
./gradlew -PtestPlatform=common :common:test
./gradlew -PtestPlatform=fabric :common:test :fabric:test
python3 -m unittest discover -s scripts/tests -v
```

Reports: `common/build/reports/tests/test/index.html` and `fabric/build/reports/tests/test/index.html`. Check the result of an action, not the presence of a class. Do not turn exceptions or initialization failures into passing checks.

## Automated in-game tests

Requires JDK 21 and an OpenGL display. Fabric API Client GameTest creates a separate flat world, runs the scenario, and closes the client. A Minecraft account is not required for this test launch.

```bash
# Desktop with a display
./gradlew -PtestPlatform=fabric :fabric:runClientGameTest

# Headless Linux, after installing Xvfb and Mesa
LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe \
  xvfb-run -a -s '-screen 0 1280x720x24' \
  ./gradlew -PtestPlatform=fabric :fabric:runClientGameTest --no-daemon
```

Ubuntu packages: `xvfb xauth libgl1-mesa-dri libglx-mesa0 libasound2t64`. This is a development client with a test mod; native gameplay checks separately validate the packaged release JAR.

Scenario: `fabric/src/gametest/java/dev/sfafy/pinchat/gametest/PinChatClientGameTest.java`.

- Right-click chat: pin, unpin, and pin again.
- `Shift` + right-click: create a group without changing the original.
- Group header: collapse and expand.
- Rename through the real screen and save button.
- Write the file, clear memory, and load: names, contents, positions, scale, and collapsed state.
- Right-click a pinned line: remove it.
- Group limit: retain the first message and reject the second.
- `F9`: open special chat, walk forward, close with `Esc`, and restore the input handler.
- `F8`: disable the mode, reload the toggle from disk, and verify that `F9` does not open the screen.

Positions and scale are set programmatically to check persistence. This GameTest does not automate physical dragging or resizing: the code reads the mouse button through GLFW, while GameTest sends game events.

Loom clears `fabric/build/run/clientGameTest` before launch. The test uses Minecraft’s normal packet handling for reliable world loading; Fabric API’s additional network synchronizer is disabled for this run. PinChat is client-side; the native scenario separately checks actual network movement through RCON.

Your `.minecraft` directory is not used, and the test mod is excluded from release JARs. Screenshots, including failure captures, are saved in the test directory. They are diagnostics, not automatic reference-image comparisons.

## Packaged JARs: every version and loader

`config/targets.json` is the shared matrix of **16 combinations**:

| Minecraft | Fabric | Quilt | Forge | NeoForge |
|---|---|---|---|---|
| 1.21.11 | ✓ | ✓ | ✓ | ✓ |
| 26.1 | ✓ | ✓ | — | ✓ |
| 26.1.1 | ✓ | ✓ | — | ✓ |
| 26.1.2 | ✓ | ✓ | — | ✓ |
| 26.2 | ✓ | ✓ | — | ✓ |

`scripts/gameplay_client.py` installs the matrix’s pinned loader version, launches a real client with the **packaged JAR**, connects it to an isolated vanilla server on `127.0.0.1`, and sends physical events through `xdotool`. This exercises GLFW in every current build, checking Shift, dragging, and resizing without replacing game code.

The server runs without authentication, is reachable only locally, and does not use your worlds. The script creates a test instance and server with the Minecraft EULA accepted; use it if you accept the [Minecraft terms](https://www.minecraft.net/eula).

```bash
python3 -m venv .venv
.venv/bin/pip install -r scripts/requirements-gameplay.txt
./gradlew buildAll
python3 scripts/release.py

# Java is selected through JAVA_HOME, JAVA_21_HOME / JAVA_25_HOME, or --java-home
LIBGL_ALWAYS_SOFTWARE=1 GALLIUM_DRIVER=llvmpipe \
  xvfb-run -a -s '-screen 0 1280x1024x24' \
  .venv/bin/python scripts/gameplay_client.py --loader neoforge --minecraft-version 26.1.2
```

Change the loader and game version for other combinations. Use `--jar /absolute/path/mod.jar` to test a particular packaged file built from this revision. Ubuntu needs `xvfb xauth xdotool libgl1-mesa-dri libglx-mesa0 libasound2t64`.

The scenario checks world loading, mod initialization, pinning/unpinning, independent groups, physical dragging, collapsing, renaming, resizing, limits, movement in special chat, and disabling the mode. The client **actually restarts**; the test then clicks a restored line and checks the other groups. Movement is confirmed through server RCON coordinates, not by the presence of an input handler.

The first launch uses the real defaults, `F8` for settings and `F9` for special chat, with no seeded PinChat binding override. After the real restart, the client uses Russian and the scenario verifies custom settings on `F7`, special chat on mouse button 4, and forward movement on the left mouse button. It checks opening, movement, and closing through these saved bindings, and captures the Russian settings screen. These intentionally changed test bindings do not alter the defaults. Client GameTest also checks default-key conflicts, an unbound key, a released mouse binding, and restoration of the original player input after screen resizing.

Output is in `build/gameplay/<loader>-<version>`: `result.json`, client/server logs, and PNG screenshots. Repeated runs archive the previous instance under `build/gameplay/archive`; assets and libraries are reused from `build/game-cache`. The test RCON password is excluded from artifacts. JSON records the tested JAR’s SHA-256; a `failed` result includes the cause and completed steps.

Checks establish functional behavior. Screenshots serve as diagnostics; automatic reference-image comparisons are not implemented.

## CI and publication

`client-tests.yml` runs quick JUnit tests and Fabric Client GameTest. In parallel, `build.yml` builds all JARs and runs **16 independent gameplay jobs**. A failure on one platform does not cancel diagnostics for the others. Logs, screenshots, and JSON are attached to every run.

A release tagged `3.1.0` or `v3.1.0` publishes only after the entire matrix passes. The tag must match `mod_version`. Before publication, the workflow checks complete combination coverage and verifies that each report’s SHA-256 belongs to the file being published. See [RELEASING.md](RELEASING.md).

## Before a release

Complete one full pass per unique JAR: Fabric/Quilt 1.21.11, Forge 1.21.11, NeoForge 1.21.11, Fabric/Quilt 26.1, NeoForge 26.1, Fabric/Quilt 26.2, and NeoForge 26.2. Also check Quilt 1.21.11 loader startup. Hotfixes in 26.1.x use one JAR but undergo separate gameplay checks.

| Step | Expected result |
|---|---|
| Enter a world and right-click a message | Full text visible on the HUD |
| Right-click again, then `Shift` + right-click | Message unpinned; new group independent |
| Fill a group to its limit | New line rejected; old lines can be removed |
| Drag a group and pull `↘` | Group follows the mouse; scale stays within 0.5–3.0 |
| Collapse, rename, expand | Correct name and contents |
| Restart the client | Messages, position, scale, and state retained |
| Open `F9`, walk, turn the camera, close | Movement and camera work; normal controls restored |
| Disable with `F8`, press `F9`, restart | Chat does not open; setting retained |
| Delete a line and delete a group through `[X]` | Deleted data stays deleted after restart |
| Check `logs/latest.log` | No PinChat, mixin, or loader errors |

Record the game, loader, JAR, and each step’s result in the release description. Do not mark untested platforms as having passed gameplay checks.
