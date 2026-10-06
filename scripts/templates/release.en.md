<a id="english"></a>

# PinChat {version}

Pin messages, organize groups, and move with special chat mode open.

{changes}## Choose your file

| Minecraft | Java | Loaders |
|---|---|---|
{matrix}

Download `pinchat-mod-<loader>-{version}-mc<game-version>.jar` from Assets below.
The 26.1.1 and 26.1.2 files are identical copies of the 26.1 build; Quilt uses a Fabric-compatible binary.

## Installation

1. Install the loader for your Minecraft version.
2. Put one matching PinChat JAR in your game profile’s `mods` folder.
3. For Fabric and Quilt, install Fabric API for your exact game version. Forge and NeoForge require no additional mods.

## Controls

Open normal chat: right-click pins a message; Shift + right-click creates a group.
Left-drag groups, resize with ↘, and collapse through the header; [R] renames, [X] deletes.
`F9` opens special chat with movement and camera control; `F8` opens settings. Data is saved in `config/pinchat.json`.
F8 and F9 are free in the standard Minecraft controls. Existing profiles retain saved bindings: assign F8/F9 or reset the two PinChat actions individually in Controls → PinChat. Older P bindings conflict with the multiplayer social menu; O opens the friends list in 26.2.

## Validation

The workflow permits publication only after builds, unit tests, Client GameTest, and the entire packaged-JAR gameplay matrix pass.
Gameplay checks cover pinning, groups, dragging, resizing, renaming, movement, the settings toggle, persistence after a real client restart, and saved custom keyboard/mouse bindings.
`SHA256SUMS` contains hashes for every JAR; `manifest.json` records loader versions and game/file mappings; `GAMEPLAY_RESULTS.json` records gameplay results and the tested JAR hashes.
Screenshots are diagnostic; automatic reference-image comparison is not implemented.

## Limitations

Forge 26.x and Minecraft 26.3 are not supported.

[User guide](https://github.com/ivanmikhaylov1/pinchat-mod#readme) · [Report a bug](https://github.com/ivanmikhaylov1/pinchat-mod/issues/new/choose)
