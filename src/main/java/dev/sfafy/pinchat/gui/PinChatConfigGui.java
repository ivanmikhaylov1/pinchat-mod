package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import dev.sfafy.pinchat.input.PinChatInputHandler;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collections;
import java.util.List;

public class PinChatConfigGui extends GuiConfigsBase {
  private static ConfigTab tab = ConfigTab.GENERIC;

  public PinChatConfigGui(Screen parent) {
    super(10, 50, PinChatMod.MOD_ID, parent, "pinchat.gui.title.configs");
  }

  @Override
  public void initGui() {
    super.initGui();
    this.clearOptions();

    int x = 10;
    int y = 26;

    for (ConfigTab tab : ConfigTab.values()) {
      x += this.createButton(x, y, tab);
    }

    int editBtnWidth = 100;
    int editBtnX = this.width - editBtnWidth - 10;
    ButtonGeneric editPosBtn = new ButtonGeneric(editBtnX, y, editBtnWidth, 20, "Edit Position");
    this.addButton(editPosBtn, (b, mouseButton) -> {
      if (this.client != null) {
        this.client.setScreen(new PositionEditScreen(this));
      }
    });
  }

  private int createButton(int x, int y, ConfigTab tab) {
    ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getDisplayName());
    button.setEnabled(PinChatConfigGui.tab != tab);
    this.addButton(button, (b, mouseButton) -> {
      PinChatConfigGui.tab = tab;
      this.reCreateListWidget();
      this.initGui();
    });

    return button.getWidth() + 2;
  }

  @Override
  public List<ConfigOptionWrapper> getConfigs() {
    List<IConfigBase> configs;

    switch (tab) {
      case ConfigTab.GENERIC -> configs = PinChatConfigMalilib.OPTIONS;
      case ConfigTab.HOTKEYS -> configs = java.util.Arrays.asList(
          PinChatInputHandler.OPEN_CONFIG_GUI,
          PinChatInputHandler.OPEN_MOVEABLE_CHAT);
      default -> {
        return Collections.emptyList();
      }
    }

    return ConfigOptionWrapper.createFor(configs);
  }

  @Override
  public void removed() {
    super.removed();
    PinChatConfigMalilib.saveConfig();
  }

  public enum ConfigTab {
    GENERIC("pinchat.config.tab.generic"),
    HOTKEYS("pinchat.config.tab.hotkeys");

    private final String name;

    ConfigTab(String name) {
      this.name = name;
    }

    public String getDisplayName() {
      return StringUtils.translate(this.name);
    }
  }
}
