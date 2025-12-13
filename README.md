# PinChat

[![Build Status](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml/badge.svg)](https://github.com/ivanmikhaylov1/pinchat-mod/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.11-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/loader-Fabric-dbd0b4.svg)](https://fabricmc.net/)
[![Forge](https://img.shields.io/badge/loader-Forge-1f1f1f.svg)](https://minecraftforge.net/)

A Minecraft mod that enhances chat functionality with message pinning, grouping, and a moveable chat screen. Works on both **Fabric** and **Forge** mod loaders!

## ✨ Features

### 📌 Pin Chat Messages

- **Right-click** on any chat message to pin/unpin it
- Pinned messages are displayed **persistently** on your HUD
- Create multiple **message groups** to organize your pinned messages
- **Drag and drop** groups anywhere on your screen
- Configurable maximum number of pinned messages
- Adjustable text width for pinned messages

### 📂 Message Groups

- Organize pinned messages into separate groups
- Each group can be positioned independently on the screen
- **Collapse/expand** groups with a single click
- Create new groups on the fly when pinning a message
- Rename groups to keep things organized

### 🎮 Moveable Chat Screen

- Open a special chat mode where you can **move while typing** (default: `U`)
- Look around freely with your mouse while the chat is open
- Smooth movement transitions — no abrupt stops when opening/closing chat
- Configurable mouse sensitivity for chat mode

### ⚙️ Flexible Configuration

PinChat works **standalone**, but also integrates with popular configuration mods for enhanced settings experience:

| Mod | Integration |
|-----|-------------|
| **Cloth Config** | ✅ Full GUI integration |
| **YACL** | ✅ Full GUI integration |
| **MaLiLib** | ✅ Hotkey configuration |
| **ModMenu** | ✅ Settings button in mod list |

> All dependencies are **optional**! The mod works perfectly without any of them.

## 📦 Installation

### Requirements

- **Minecraft**: 1.21.11
- **Java**: 21 or higher
- **Mod Loader**: Fabric or Forge (choose one)

### For Fabric

1. Install [Fabric Loader](https://fabricmc.net/use/) (0.18.1+)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download PinChat for Fabric from [Releases](../../releases)
4. Place all mods in your `.minecraft/mods` folder

### For Forge

1. Install [Forge](https://files.minecraftforge.net/) (1.21.11-61.0.2+)
2. Download PinChat for Forge from [Releases](../../releases)
3. Place the mod in your `.minecraft/mods` folder

### Optional Mods (Fabric only)

For enhanced configuration UI, you can optionally install:

- [Cloth Config](https://modrinth.com/mod/cloth-config) — Config GUI
- [YACL](https://modrinth.com/mod/yacl) — Alternative config GUI
- [MaLiLib](https://modrinth.com/mod/malilib) — Advanced hotkey configuration
- [ModMenu](https://modrinth.com/mod/modmenu) — Access settings from mod list

## 🎮 Usage

### Pinning Messages

1. Open the chat (`T` or `/`)
2. **Right-click** on any message to open the pin menu
3. Select **Pin to [Group Name]** or **Create New Group**
4. Right-click on a pinned message header to **unpin** or manage the group

### Managing Groups

- **Left-click and drag** the group header to move it on screen
- **Click the collapse button** to minimize a group
- Right-click for more options (rename, delete, etc.)

### Moveable Chat

1. Press `U` (or your configured key) to open moveable chat
2. Type your message while moving and looking around
3. Press `U` again or `ESC` to close

### Configuration

Access settings through one of these methods:

- **Command**: `/pinchat config`
- **ModMenu**: Click the settings icon next to PinChat in the mod list
- **In-game**: Use the keybind if configured

#### Available Settings

| Setting | Description | Default |
|---------|-------------|---------|
| **Max Pinned Messages** | Maximum number of pinned messages per group | 5 |
| **Max Line Width** | Maximum width of pinned messages in pixels | 200 |
| **Chat Sensitivity** | Mouse sensitivity multiplier in moveable chat | 1.0 |
| **Pinned Position X/Y** | Default position for new groups | 10, 10 |
| **Pinned Scale** | Text scale for pinned messages | 1.0 |

## 🛠️ Building from Source

```bash
git clone https://github.com/ivanmikhaylov1/pinchat-mod.git
cd pinchat-mod

# Build both Fabric and Forge versions
./gradlew buildAll

# Build only Fabric
./gradlew :fabric:build

# Build only Forge
./gradlew :forge:build

# Run tests
./gradlew :fabric:test
```

Compiled JARs will be in:

- Fabric: `fabric/build/libs/`
- Forge: `forge/build/libs/`

## 📁 Project Structure

```
pinchat-mod/
├── common/          # Shared code (configuration, data classes)
├── fabric/          # Fabric-specific implementation
├── forge/           # Forge-specific implementation
├── gradle/          # Gradle wrapper files
└── .github/         # GitHub Actions workflows
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## 🙏 Credits

- Built with [Fabric](https://fabricmc.net/) and [Forge](https://minecraftforge.net/)
- Uses [Architectury Loom](https://github.com/architectury/architectury-loom) for multi-platform builds
- Optional integration with [Cloth Config](https://github.com/shedaniel/cloth-config), [YACL](https://github.com/isXander/YetAnotherConfigLib), and [MaLiLib](https://github.com/maruohon/malilib)

## ❓ Support

If you encounter any issues or have suggestions:

- [Open an Issue](../../issues)
- Make sure to include your Minecraft version, mod loader, and any crash logs

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/ivanmikhaylov1">Sfafy</a>
</p>
