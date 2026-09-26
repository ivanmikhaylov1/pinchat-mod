package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PinChatConfigScreen extends Screen {
  private final Screen parent;

  public PinChatConfigScreen(Screen parent) {
    super(Component.translatable("pinchat.config.title"));
    this.parent = parent;
  }

  @Override
  protected void init() {
    int center = this.width / 2;
    this.addRenderableWidget(Button.builder(toggleLabel(), button -> {
      PinChatConfig.moveableChatEnabled = !PinChatConfig.moveableChatEnabled;
      PinChatConfig.save();
      button.setMessage(toggleLabel());
    }).bounds(center - 100, this.height / 2 - 10, 200, 20).build());
    this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
        .bounds(center - 100, this.height / 2 + 20, 200, 20).build());
  }

  private Component toggleLabel() {
    return Component.translatable("pinchat.config.moveableChat", Component.translatable(
        PinChatConfig.moveableChatEnabled ? "options.on" : "options.off"));
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(this.parent);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
    context.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    super.render(context, mouseX, mouseY, delta);
  }
}
