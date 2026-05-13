# PinChat

[![Build Status](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml/badge.svg)](https://github.com/ivanmikhaylov1/pinchat-mod/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.11-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/loader-Fabric-dbd0b4.svg)](https://fabricmc.net/)
[![Forge](https://img.shields.io/badge/loader-Forge-1f1f1f.svg)](https://minecraftforge.net/)

A Minecraft mod that enhances chat with message pinning, keyword highlighting, and a moveable chat screen. Works on both **Fabric** and **Forge**.

## Features

### Pin Messages to Your HUD
- **Right-click** any chat message to pin or unpin it
- Pinned messages stay visible on screen at all times
- Configurable maximum number of pinned messages per group

### Message Groups
- Organize pinned messages into named groups
- **Drag groups** anywhere on the screen
- **Collapse / expand** groups with one click
- Rename or delete groups on the fly
- Each group has its own position and scale

### Keyword Highlighting
- Define words (e.g. your username) that should stand out in chat
- Matching words turn **yellow** in incoming messages
- A sound plays when a highlight is triggered, so you never miss a mention
- Configure via `/pinchat config` or the settings screen

### Moveable Chat
- Press `U` to open a chat mode where you can **move and look around** while typing
- Smooth transitions when opening and closing
- Configurable mouse sensitivity

### Optional Integrations (Fabric)

| Mod | What it adds |
|-----|-------------|
| [Cloth Config](https://modrinth.com/mod/cloth-config) | Full settings GUI |
| [YACL](https://modrinth.com/mod/yacl) | Alternative settings GUI |
| [MaLiLib](https://modrinth.com/mod/malilib) | Hotkey configuration |
| [ModMenu](https://modrinth.com/mod/modmenu) | Settings button in mod list |

All integrations are **optional** — the mod works without any of them.

## Installation

**Requirements:** Minecraft 1.21.11 · Java 21+

### Fabric
1. Install [Fabric Loader](https://fabricmc.net/use/) 0.18.1+
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Drop both JARs into `.minecraft/mods/`

### Forge
1. Install [Forge](https://files.minecraftforge.net/) 1.21.11-61.0.2+
2. Drop the PinChat JAR into `.minecraft/mods/`

## Usage

### Pinning messages
1. Open chat (`T`)
2. Right-click any message
3. Choose a group or create a new one

### Managing groups
- **Left-click and drag** the group header to reposition it
- Click **▼/▶** to collapse or expand
- Click **[R]** to rename, **[X]** to delete

### Keyword highlighting
1. Open config via `/pinchat config`, ModMenu, or the keybind
2. In the **Highlight Keywords** field enter comma-separated words — for example: `Steve, admin, !help`
3. Save — those words will glow yellow in chat and play a ping sound when received

### Moveable chat
- Press `U` to toggle; type while walking and looking around freely

## Configuration

| Setting | Default | Description |
|---------|---------|-------------|
| Max Pinned Messages | 5 | Per-group limit |
| Max Line Width | 200 px | Wrapping width for pinned text |
| Chat Sensitivity | 1.0 | Mouse speed in moveable chat |
| Highlight Keywords | _(empty)_ | Comma-separated trigger words |
| Pinned Position X / Y | 10, 10 | Default spawn position for new groups |
| Pinned Scale | 1.0 | Text scale for pinned messages |

## Building from Source

```bash
git clone https://github.com/ivanmikhaylov1/pinchat-mod.git
cd pinchat-mod

# Build both loaders
./gradlew buildAll

# Build only Fabric
./gradlew :fabric:build

# Build only Forge
./gradlew :forge:build

# Run tests
./gradlew :fabric:test
```

Output JARs:
- Fabric → `fabric/build/libs/`
- Forge → `forge/build/libs/`

## Project Structure

```
pinchat-mod/
├── common/    # Shared data classes and config logic
├── fabric/    # Fabric implementation
├── forge/     # Forge implementation
└── .github/   # CI workflows
```

## Contributing

Pull requests are welcome.

1. Fork the repository
2. Create a branch — `feature/your-feature` or `fix/your-fix`
3. Commit and push, then open a PR against `master`

## License

MIT — see [LICENSE](LICENSE).

---

<p align="center">Made with ❤️ by <a href="https://github.com/ivanmikhaylov1">Sfafy</a></p>
