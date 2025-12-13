package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PinChatConfigScreen extends Screen {
  private final Screen parent;
  private EditBox maxPinnedMessagesField;
  private EditBox maxLineWidthField;
  private EditBox chatSensitivityField;

  public PinChatConfigScreen(Screen parent) {
    super(Component.translatable("pinchat.config.title"));
    this.parent = parent;
  }

  @Override
  protected void init() {
    int y = 40;
    int center = this.width / 2;
    int fieldWidth = 100;
    int labelWidth = 100;

    this.addRenderableWidget(Button.builder(Component.translatable("pinchat.config.maxPinnedMessages"), b -> {
    }).bounds(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    maxPinnedMessagesField = new EditBox(this.font, center + 10, y, fieldWidth, 20,
        Component.translatable("pinchat.config.maxPinnedMessages"));
    maxPinnedMessagesField.setValue(String.valueOf(PinChatConfig.maxPinnedMessages));
    this.addRenderableWidget(maxPinnedMessagesField);
    y += 25;

    this.addRenderableWidget(Button.builder(Component.translatable("pinchat.config.maxLineWidth"), b -> {
    }).bounds(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    maxLineWidthField = new EditBox(this.font, center + 10, y, fieldWidth, 20,
        Component.translatable("pinchat.config.maxLineWidth"));
    maxLineWidthField.setValue(String.valueOf(PinChatConfig.maxLineWidth));
    this.addRenderableWidget(maxLineWidthField);
    y += 25;

    this.addRenderableWidget(Button.builder(Component.translatable("pinchat.config.chatSensitivity"), b -> {
    }).bounds(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    chatSensitivityField = new EditBox(this.font, center + 10, y, fieldWidth, 20,
        Component.translatable("pinchat.config.chatSensitivity"));
    chatSensitivityField.setValue(String.valueOf(PinChatConfig.chatSensitivity));
    this.addRenderableWidget(chatSensitivityField);
    y += 25;

    this.addRenderableWidget(Button.builder(Component.translatable("pinchat.config.save"), button -> {
      save();
      this.minecraft.setScreen(this.parent);
    }).bounds(center - 100, this.height - 40, 200, 20).build());
  }

  private void save() {
    try {
      PinChatConfig.maxPinnedMessages = Integer.parseInt(maxPinnedMessagesField.getValue());
    } catch (NumberFormatException ignored) {
    }
    try {
      PinChatConfig.maxLineWidth = Integer.parseInt(maxLineWidthField.getValue());
    } catch (NumberFormatException ignored) {
    }
    try {
      PinChatConfig.chatSensitivity = Double.parseDouble(chatSensitivityField.getValue());
    } catch (NumberFormatException ignored) {
    }

    PinChatConfig.save();
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    super.render(context, mouseX, mouseY, delta);
  }
}
