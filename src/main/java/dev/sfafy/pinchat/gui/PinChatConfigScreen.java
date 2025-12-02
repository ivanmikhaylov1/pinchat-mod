package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class PinChatConfigScreen extends Screen {
  private final Screen parent;
  private TextFieldWidget maxPinnedMessagesField;
  private TextFieldWidget maxLineWidthField;
  private TextFieldWidget chatSensitivityField;

  public PinChatConfigScreen(Screen parent) {
    super(Text.translatable("pinchat.config.title"));
    this.parent = parent;
  }

  @Override
  protected void init() {
    int y = 40;
    int center = this.width / 2;
    int fieldWidth = 100;
    int labelWidth = 100;

    // Max Pinned Messages
    this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("pinchat.config.maxPinnedMessages"), b -> {
    }).dimensions(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    maxPinnedMessagesField = new TextFieldWidget(this.textRenderer, center + 10, y, fieldWidth, 20,
        Text.translatable("pinchat.config.maxPinnedMessages"));
    maxPinnedMessagesField.setText(String.valueOf(PinChatConfig.maxPinnedMessages));
    this.addDrawableChild(maxPinnedMessagesField);
    y += 25;

    // Max Line Width
    this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("pinchat.config.maxLineWidth"), b -> {
    }).dimensions(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    maxLineWidthField = new TextFieldWidget(this.textRenderer, center + 10, y, fieldWidth, 20,
        Text.translatable("pinchat.config.maxLineWidth"));
    maxLineWidthField.setText(String.valueOf(PinChatConfig.maxLineWidth));
    this.addDrawableChild(maxLineWidthField);
    y += 25;

    // Chat Sensitivity
    this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("pinchat.config.chatSensitivity"), b -> {
    }).dimensions(center - 100 - 10, y, labelWidth, 20).build()).active = false;
    chatSensitivityField = new TextFieldWidget(this.textRenderer, center + 10, y, fieldWidth, 20,
        Text.translatable("pinchat.config.chatSensitivity"));
    chatSensitivityField.setText(String.valueOf(PinChatConfig.chatSensitivity));
    this.addDrawableChild(chatSensitivityField);
    y += 25;

    this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("pinchat.config.save"), button -> {
      save();
      this.client.setScreen(this.parent);
    }).dimensions(center - 100, this.height - 40, 200, 20).build());
  }

  private void save() {
    try {
      PinChatConfig.maxPinnedMessages = Integer.parseInt(maxPinnedMessagesField.getText());
    } catch (NumberFormatException ignored) {
    }
    try {
      PinChatConfig.maxLineWidth = Integer.parseInt(maxLineWidthField.getText());
    } catch (NumberFormatException ignored) {
    }
    try {
      PinChatConfig.chatSensitivity = Double.parseDouble(chatSensitivityField.getText());
    } catch (NumberFormatException ignored) {
    }

    PinChatConfig.save();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderInGameBackground(context);
    context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    super.render(context, mouseX, mouseY, delta);
  }
}
