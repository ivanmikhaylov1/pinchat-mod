# PinChat

[![Build Status](https://github.com/ivanmikhaylov1/pinchat-mod/actions/workflows/build.yml/badge.svg)](https://github.com/yourusername/PinChatMod/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.10-brightgreen.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/modloader-Fabric-blue.svg)](https://fabricmc.net/)

A Minecraft Fabric mod that enhances chat functionality with message pinning and a moveable chat screen for better
visibility while playing.

## Features

### 📌 Pin Chat Messages

- Right-click on any chat message to pin/unpin it
- Pinned messages display persistently on your HUD
- Configurable maximum number of pinned messages
- Configurable maximum line width for pinned messages

### 🎮 Moveable Chat Screen

- Open a special chat mode where you can move while typing (default: `U`)
- Look around freely with your mouse while the chat is open
- Smooth movement transitions - no abrupt stops when opening/closing chat
- Configurable mouse sensitivity for chat mode

### ⚙️ Configuration

- Built-in MaLiLib configuration system
- Access via `/pinchat config` command or hotkey (`P, C` by default)
- Customizable keybinds for all features
- Persistent settings saved to config file

## Requirements

- **Minecraft**: 1.21.10
- **Fabric Loader**: Latest
- **Fabric API**: Latest
- **MaLiLib**: 0.26.3+

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download [MaLiLib](https://modrinth.com/mod/malilib)
4. Download PinChat from [Releases](../../releases)
5. Place all mods in your `.minecraft/mods` folder

## Usage

### Pinning Messages

1. Open the chat (T or /)
2. Right-click on any message to pin it
3. Right-click again to unpin

### Moveable Chat

1. Press `U` (or your configured key) to open moveable chat
2. Type your message while moving and looking around
3. Press `U` again or `ESC` to close

### Configuration

- **Command**: `/pinchat config`
- **Hotkey**: `P` then `C` (default)

#### Available Settings

- **maxPinnedMessages**: Maximum number of pinned messages (default: 5)
- **maxLineWidth**: Maximum width of pinned messages in pixels (default: 200)
- **chatSensitivity**: Mouse sensitivity multiplier in chat mode (default: 3.0)

## Building from Source

```bash
git clone https://github.com/ivanmikhaylov1/pinchat-mod.git
cd pinchat-mod
./gradlew build
```

The compiled JAR will be in `build/libs/`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- Built with [Fabric](https://fabricmc.net/)
- Uses [MaLiLib](https://github.com/maruohon/malilib) for configuration

## Support

If you encounter any issues or have suggestions, please [open an issue](../../issues).
