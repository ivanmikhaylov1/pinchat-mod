package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PinChatConfigScreen extends Screen {
  private final Screen parent;

  public PinChatConfigScreen(Screen parent) {
    super(Text.translatable("pinchat.config.title"));
    this.parent = parent;
  }

  @Override
  protected void init() {
    int center = this.width / 2;
    this.addDrawableChild(ButtonWidget.builder(toggleLabel(), button -> {
      PinChatConfig.moveableChatEnabled = !PinChatConfig.moveableChatEnabled;
      PinChatConfig.save();
      button.setMessage(toggleLabel());
    }).dimensions(center - 100, this.height / 2 - 10, 200, 20).build());
    this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> close())
        .dimensions(center - 100, this.height / 2 + 20, 200, 20).build());
  }

  private Text toggleLabel() {
    return Text.translatable("pinchat.config.moveableChat", Text.translatable(
        PinChatConfig.moveableChatEnabled ? "options.on" : "options.off"));
  }

  @Override
  public void close() {
    this.client.setScreen(this.parent);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderInGameBackground(context);
    context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
    super.render(context, mouseX, mouseY, delta);
  }
}
