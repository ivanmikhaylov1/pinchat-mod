package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import net.minecraft.client.gui.Click;
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
    super(Text.translatable("pinchat.gui.positionEdit.title"));
    this.parent = parent;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    context.fill(0, 0, this.width, this.height, 0x80000000);

    context.drawCenteredTextWithShadow(this.textRenderer,
        Text.translatable("pinchat.gui.positionEdit.instructions"),
        this.width / 2, 20, 0xFFFFFF);

    int startX = PinChatConfigMalilib.PINNED_X.getIntegerValue();
    int startY = PinChatConfigMalilib.PINNED_Y.getIntegerValue();
    float scale = (float) PinChatConfigMalilib.PINNED_SCALE.getDoubleValue();
    int lineHeight = 12;

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
      Text dummy = Text.translatable("pinchat.gui.positionEdit.exampleMessage");
      width = this.textRenderer.getWidth(dummy);
      height = lineHeight;
    }

    var matrices = context.getMatrices();
    matrices.pushMatrix();
    matrices.translate(startX, startY);
    matrices.scale(scale, scale);

    if (PinnedMessages.pinnedList.isEmpty()) {
      Text dummy = Text.translatable("pinchat.gui.positionEdit.exampleMessage");
      context.fill(-2, -2, width + 2, lineHeight + 2, 0x80000000);
      context.drawText(this.textRenderer, dummy, 0, 0, 0xFFFFFFFF, true);
    } else {
      for (int i = 0; i < PinnedMessages.pinnedList.size(); i++) {
        Text msg = PinnedMessages.pinnedList.get(i);
        int y = i * lineHeight;
        int w = this.textRenderer.getWidth(msg);

        context.fill(-2, y - 2, w + 2, y + 8, 0x80000000);
        context.drawText(this.textRenderer, msg, 0, y, 0xFFFFFFFF, true);
      }
    }
    matrices.popMatrix();

    int scaledWidth = (int) (width * scale);
    int scaledHeight = (int) (height * scale);

    if (!PinnedMessages.pinnedList.isEmpty()) {
      for (int i = 0; i < PinnedMessages.pinnedList.size(); i++) {
        int rowY = i * lineHeight;
        double top = startY + (rowY - 2) * scale;
        double bottom = startY + (rowY + 8) * scale;

        if (mouseY >= top && mouseY <= bottom) {
          Text msg = PinnedMessages.pinnedList.get(i);
          int msgWidth = this.textRenderer.getWidth(msg);
          double localX = (mouseX - startX) / scale;

          if (localX >= -2 && localX <= msgWidth + 2) {
            int highlightLeft = (int) (startX - 2 * scale);
            int highlightRight = (int) (startX + (msgWidth + 2) * scale);
            int highlightTop = (int) top;
            int highlightBottom = (int) bottom;

            context.fill(highlightLeft, highlightTop, highlightRight, highlightBottom, 0x40FF0000);
            context.drawTooltip(this.textRenderer, Text.of("Right-click to unpin"), (int) mouseX, (int) mouseY);
            break;
          }
        }
      }
    }

    int handleX = startX + scaledWidth;
    int handleY = startY + scaledHeight;
    Text arrow = Text.of("↘");
    int arrowW = this.textRenderer.getWidth(arrow);
    int arrowH = this.textRenderer.fontHeight;
    context.drawTextWithShadow(this.textRenderer, arrow, handleX - arrowW, handleY - arrowH, 0xFFFFFFFF);
  }

  @Override
  public boolean mouseClicked(Click click, boolean isRightClick) {
    double mouseX = click.x();
    double mouseY = click.y();
    int startX = PinChatConfigMalilib.PINNED_X.getIntegerValue();
    int startY = PinChatConfigMalilib.PINNED_Y.getIntegerValue();
    double scale = PinChatConfigMalilib.PINNED_SCALE.getDoubleValue();
    int lineHeight = 12;

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
      Text dummy = Text.translatable("pinchat.gui.positionEdit.exampleMessage");
      width = this.textRenderer.getWidth(dummy);
      height = lineHeight;
    }

    int scaledWidth = (int) (width * scale);
    int scaledHeight = (int) (height * scale);

    if (click.buttonInfo().button() == 1 && mouseX >= startX - 4 && mouseX <= startX + scaledWidth + 4 &&
        mouseY >= startY - 4 && mouseY <= startY + scaledHeight + 4 && !PinnedMessages.pinnedList.isEmpty()) {
      double localX = (mouseX - startX) / scale;
      double localY = (mouseY - startY) / scale;

      for (int i = PinnedMessages.pinnedList.size() - 1; i >= 0; i--) {
        Text msg = PinnedMessages.pinnedList.get(i);
        int msgY = i * lineHeight;
        int msgWidth = this.textRenderer.getWidth(msg);

        if (localY >= msgY && localY < msgY + lineHeight &&
            localX >= -2 && localX <= msgWidth + 2) {

          PinnedMessages.pinnedList.remove(i);
          PinChatConfigMalilib.saveConfig();

          if (this.client != null) {
            this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
          }
          return true;
        }
      }
    }


    if (click.buttonInfo().button() == 0) {
      int handleX = startX + scaledWidth;
      int handleY = startY + scaledHeight;

      Text arrow = Text.of("↘");
      int arrowW = this.textRenderer.getWidth(arrow);
      int arrowH = this.textRenderer.fontHeight;

      if (mouseX >= handleX - arrowW - 4 && mouseX <= handleX + 4 &&
          mouseY >= handleY - arrowH - 4 && mouseY <= handleY + 4) {
        this.isResizing = true;
        this.initialMouseX = mouseX;
        this.initialMouseY = mouseY;
        this.initialScale = scale;
        return true;
      }

      if (mouseX >= startX - 4 && mouseX <= startX + scaledWidth + 4 &&
          mouseY >= startY - 4 && mouseY <= startY + scaledHeight + 4) {
        this.isDragging = true;
        this.dragOffsetX = (int) mouseX - startX;
        this.dragOffsetY = (int) mouseY - startY;
        return true;
      }
    }
    return super.mouseClicked(click, isRightClick);
  }

  @Override
  public boolean mouseDragged(Click click, double deltaX, double deltaY) {
    double mouseX = click.x();
    double mouseY = click.y();

    if (this.isDragging) {
      int newX = (int) mouseX - this.dragOffsetX;
      int newY = (int) mouseY - this.dragOffsetY;

      PinChatConfigMalilib.PINNED_X.setIntegerValue(newX);
      PinChatConfigMalilib.PINNED_Y.setIntegerValue(newY);
      return true;
    } else if (this.isResizing) {
      double diffX = mouseX - this.initialMouseX;
      double diffY = mouseY - this.initialMouseY;

      double diagonal = (diffX + diffY) / 200.0;

      double newScale = this.initialScale + diagonal;
      newScale = Math.max(0.5, Math.min(3.0, newScale));

      PinChatConfigMalilib.PINNED_SCALE.setDoubleValue(newScale);
      return true;
    }

    return super.mouseDragged(click, deltaX, deltaY);
  }

  @Override
  public boolean mouseReleased(Click click) {
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
