package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PositionEditScreen extends Screen {
  private final Screen parent;
  private boolean isDragging = false;
  private boolean isResizing = false;
  private int dragOffsetX = 0;
  private int dragOffsetY = 0;
  private double initialMouseX = 0;
  private double initialMouseY = 0;
  private double initialScale = 1.0;

  public PositionEditScreen(Screen parent) {
    super(Text.of("Position Edit Screen"));
    this.parent = parent;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    context.fill(0, 0, this.width, this.height, 0x80000000);

    context.drawCenteredTextWithShadow(this.textRenderer,
        Text.of("Drag to move • Drag corner to resize • ESC to save"),
        this.width / 2, 20, 0xFFFFFF);

    int startX = PinChatConfigMalilib.PINNED_X.getIntegerValue();
    int startY = PinChatConfigMalilib.PINNED_Y.getIntegerValue();
    int lineHeight = 12;

    // Calculate dimensions
    int width = 100;
    int height = 20;

    if (!PinnedMessages.pinnedList.isEmpty()) {
      int maxWidth = 0;
      for (Text msg : PinnedMessages.pinnedList) {
        int w = this.textRenderer.getWidth(msg);
        if (w > maxWidth)
          maxWidth = w;
      }
      width = maxWidth;
      height = PinnedMessages.pinnedList.size() * lineHeight;
    } else {
      Text dummy = Text.of("Example Pinned Message");
      width = this.textRenderer.getWidth(dummy);
      height = lineHeight;
    }

    if (PinnedMessages.pinnedList.isEmpty()) {
      Text dummy = Text.of("Example Pinned Message");
      context.fill(startX - 2, startY - 2, startX + width + 2, startY + lineHeight + 2, 0x80000000);
      context.drawText(this.textRenderer, dummy, startX, startY, 0xFFFFFFFF, true);
    } else {
      for (int i = 0; i < PinnedMessages.pinnedList.size(); i++) {
        Text msg = PinnedMessages.pinnedList.get(i);
        int y = startY + (i * lineHeight);
        int w = this.textRenderer.getWidth(msg);

        context.fill(startX - 2, y - 2, startX + w + 2, y + 8, 0x80000000);
        context.drawText(this.textRenderer, msg, startX, y, 0xFFFFFFFF, true);
      }
    }

    int scaledWidth = width;
    int scaledHeight = height;
    int borderX = startX - 4;
    int borderY = startY - 4;
    int borderW = scaledWidth + 8;
    int borderH = scaledHeight + 8;
    int color = 0xFF00FF00;

    context.fill(borderX, borderY, borderX + borderW, borderY + 1, color);
    context.fill(borderX, borderY + borderH - 1, borderX + borderW, borderY + borderH, color);
    context.fill(borderX, borderY, borderX + 1, borderY + borderH, color);
    context.fill(borderX + borderW - 1, borderY, borderX + borderW, borderY + borderH, color);

    int handleSize = 8;
    int handleX = startX + scaledWidth;
    int handleY = startY + scaledHeight;
    context.fill(handleX - handleSize, handleY - handleSize, handleX, handleY, 0xFFFF0000);

    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean isRightClick) {
    if (!isRightClick && click.buttonInfo().button() == 0) {
      int startX = PinChatConfigMalilib.PINNED_X.getIntegerValue();
      int startY = PinChatConfigMalilib.PINNED_Y.getIntegerValue();
      double scale = PinChatConfigMalilib.PINNED_SCALE.getDoubleValue();

      double mouseX = click.x();
      double mouseY = click.y();

      int width = 100;
      int height = 20;
      int lineHeight = 12;

      if (!PinnedMessages.pinnedList.isEmpty()) {
        int maxWidth = 0;
        for (Text msg : PinnedMessages.pinnedList) {
          int w = this.textRenderer.getWidth(msg);
          if (w > maxWidth)
            maxWidth = w;
        }
        width = maxWidth;
        height = PinnedMessages.pinnedList.size() * lineHeight;
      } else {
        Text dummy = Text.of("Example Pinned Message");
        width = this.textRenderer.getWidth(dummy);
        height = lineHeight;
      }

      int handleSize = 8;
      int handleX = startX + width;
      int handleY = startY + height;

      if (mouseX >= handleX - handleSize && mouseX <= handleX &&
          mouseY >= handleY - handleSize && mouseY <= handleY) {
        this.isResizing = true;
        this.initialMouseX = mouseX;
        this.initialMouseY = mouseY;
        this.initialScale = scale;
        return true;
      }

      if (mouseX >= startX - 4 && mouseX <= startX + width + 4 &&
          mouseY >= startY - 4 && mouseY <= startY + height + 4) {
        this.isDragging = true;
        this.dragOffsetX = (int) mouseX - startX;
        this.dragOffsetY = (int) mouseY - startY;
        return true;
      }
    }
    return super.mouseClicked(click, isRightClick);
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    if (this.isDragging) {
      int newX = (int) mouseX - this.dragOffsetX;
      int newY = (int) mouseY - this.dragOffsetY;

      PinChatConfigMalilib.PINNED_X.setIntegerValue(newX);
      PinChatConfigMalilib.PINNED_Y.setIntegerValue(newY);
    } else if (this.isResizing) {
      double deltaX = mouseX - this.initialMouseX;
      double deltaY = mouseY - this.initialMouseY;
      double diagonal = (deltaX + deltaY) / 200.0;

      double newScale = this.initialScale + diagonal;
      newScale = Math.max(0.5, Math.min(3.0, newScale));

      PinChatConfigMalilib.PINNED_SCALE.setDoubleValue(newScale);
    }
    super.mouseMoved(mouseX, mouseY);
  }

  @Override
  public boolean mouseReleased(net.minecraft.client.gui.Click click) {
    if (click.buttonInfo().button() == 0) {
      this.isDragging = false;
      this.isResizing = false;
    }
    return super.mouseReleased(click);
  }

  @Override
  public void close() {
    PinChatConfigMalilib.saveConfig();
    if (this.client != null) {
      this.client.setScreen(this.parent);
    }
  }
}
