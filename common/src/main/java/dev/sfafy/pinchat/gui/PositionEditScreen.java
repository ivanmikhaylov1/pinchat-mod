package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PositionEditScreen extends Screen {
  private static final long DOUBLE_CLICK_INTERVAL = 500;
  private final Screen parent;
  private boolean isDragging = false;
  private boolean isResizing = false;
  private MessageGroup draggingGroup = null;
  private MessageGroup resizingGroup = null;
  private int dragOffsetX = 0;
  private int dragOffsetY = 0;
  private double initialMouseX = 0;
  private double initialMouseY = 0;
  private double initialScale = 1.0;
  private long lastRightClickTime = 0;

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

    PinnedMessages.getOrCreateDefaultGroup();

    int btnWidth = 80;
    int btnHeight = 20;
    int btnX = this.width - btnWidth - 10;
    int btnY = 10;

    boolean isHovered = mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight;
    int color = isHovered ? 0xFFFFFFFF : 0xFFCCCCCC;
    int bgColor = isHovered ? 0xFF444444 : 0xFF222222;

    context.fill(btnX, btnY, btnX + btnWidth, btnY + btnHeight, 0xFF000000);
    context.fill(btnX + 1, btnY + 1, btnX + btnWidth - 1, btnY + btnHeight - 1, bgColor);
    context.drawCenteredTextWithShadow(this.textRenderer, Text.of("New Group"), btnX + btnWidth / 2, btnY + 6, color);

    for (MessageGroup group : PinnedMessages.groups) {
      renderGroup(context, group, mouseX, mouseY);
    }
  }

  private void renderGroup(DrawContext context, MessageGroup group, int mouseX, int mouseY) {
    int startX = group.x;
    int startY = group.y;
    float scale = (float) group.scale;
    int lineHeight = 12;

    int width = 100;
    int height = 20;

    if (!group.messages.isEmpty()) {
      int maxWidth = 0;
      for (String msgContent : group.messages) {
        Text msg = Text.of(msgContent);
        int w = this.textRenderer.getWidth(msg);
        if (w > maxWidth)
          maxWidth = w;
      }
      width = maxWidth;
      height = group.messages.size() * lineHeight;
    } else {
      Text dummy = Text.translatable("pinchat.gui.positionEdit.exampleMessage");
      width = this.textRenderer.getWidth(dummy);
      height = lineHeight;
    }

    var matrices = context.getMatrices();
    matrices.pushMatrix();
    matrices.translate(startX, startY);
    matrices.scale(scale, scale);

    if (group.messages.isEmpty()) {
      Text dummy = Text.translatable("pinchat.gui.positionEdit.exampleMessage");
      context.fill(-2, -2, width + 2, lineHeight + 2, 0x80000000);
      context.drawText(this.textRenderer, dummy, 0, 0, 0xFFFFFFFF, true);

      String indicator = group.isCollapsed ? "▶" : "▼";
      Text headerText = Text.of(indicator + " " + group.name);
      context.drawText(this.textRenderer, headerText, 0, -12, 0xFFAAAAAA, true);

      if (PinnedMessages.groups.size() > 1) {
        context.drawText(this.textRenderer, Text.of("[x]"), this.textRenderer.getWidth(headerText) + 4, -12,
            0xFFFF5555, true);
      }
    } else {
      String indicator = group.isCollapsed ? "▶" : "▼";
      Text headerText = Text.of(indicator + " " + group.name);
      context.drawText(this.textRenderer, headerText, 0, -12, 0xFFAAAAAA, true);

      if (PinnedMessages.groups.size() > 1) {
        context.drawText(this.textRenderer, Text.of("[x]"), this.textRenderer.getWidth(headerText) + 4, -12,
            0xFFFF5555, true);
      }

      if (!group.isCollapsed) {
        for (int i = 0; i < group.messages.size(); i++) {
          String msgContent = group.messages.get(i);
          Text msg = Text.of(msgContent);
          int y = i * lineHeight;
          int w = this.textRenderer.getWidth(msg);

          context.fill(-2, y - 2, w + 2, y + 8, 0x80000000);
          context.drawText(this.textRenderer, msg, 0, y, 0xFFFFFFFF, true);
        }
      }
    }
    matrices.popMatrix();

    int scaledWidth = (int) (width * scale);
    int scaledHeight = (int) (height * scale);

    if (!group.messages.isEmpty() && !group.isCollapsed) {
      for (int i = 0; i < group.messages.size(); i++) {
        int rowY = i * lineHeight;
        double top = startY + (rowY - 2) * scale;
        double bottom = startY + (rowY + 8) * scale;

        if (mouseY >= top && mouseY <= bottom) {
          String msgContent = group.messages.get(i);
          Text msg = Text.of(msgContent);
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

    if (click.buttonInfo().button() == 1) {
      boolean clickedOnGroup = false;

      for (int gIdx = PinnedMessages.groups.size() - 1; gIdx >= 0; gIdx--) {
        MessageGroup group = PinnedMessages.groups.get(gIdx);
        if (handleRightClickOnGroup(group, mouseX, mouseY)) {
          return true;
        }
        if (checkHeaderRightClick(group, mouseX, mouseY)) {
          return true;
        }
        if (isMouseOverGroup(group, mouseX, mouseY)) {
          clickedOnGroup = true;
        }
      }

      if (!clickedOnGroup) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRightClickTime < DOUBLE_CLICK_INTERVAL) {
          createNewGroup((int) mouseX, (int) mouseY);
          lastRightClickTime = 0;
          return true;
        }
        lastRightClickTime = currentTime;
      }
      return false;
    }

    if (click.buttonInfo().button() == 0) {
      int btnWidth = 80;
      int btnHeight = 20;
      int btnX = this.width - btnWidth - 10;
      int btnY = 10;

      if (mouseX >= btnX && mouseX <= btnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight) {
        createNewGroup(this.width / 2, this.height / 2);
        return true;
      }

      for (int gIdx = PinnedMessages.groups.size() - 1; gIdx >= 0; gIdx--) {
        MessageGroup group = PinnedMessages.groups.get(gIdx);
        if (checkDeleteClick(group, mouseX, mouseY)) {
          return true;
        }
      }

      for (int gIdx = PinnedMessages.groups.size() - 1; gIdx >= 0; gIdx--) {
        MessageGroup group = PinnedMessages.groups.get(gIdx);
        if (checkResizeClick(group, mouseX, mouseY)) {
          return true;
        }
      }

      for (int gIdx = PinnedMessages.groups.size() - 1; gIdx >= 0; gIdx--) {
        MessageGroup group = PinnedMessages.groups.get(gIdx);
        if (checkHeaderClick(group, mouseX, mouseY)) {
          return true;
        }
      }

      for (int gIdx = PinnedMessages.groups.size() - 1; gIdx >= 0; gIdx--) {
        MessageGroup group = PinnedMessages.groups.get(gIdx);
        if (checkDragClick(group, mouseX, mouseY)) {
          return true;
        }
      }
    }

    return super.mouseClicked(click, isRightClick);
  }

  private void createNewGroup(int x, int y) {
    String name = "Group #" + (PinnedMessages.groups.size() + 1);
    PinnedMessages.groups.add(new MessageGroup(name, x, y, 1.0));
    if (this.client != null) {
      this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
          .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
  }

  private boolean handleRightClickOnGroup(MessageGroup group, double mouseX, double mouseY) {
    if (group.messages.isEmpty() || group.isCollapsed)
      return false;

    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;
    int lineHeight = 12;

    double localX = (mouseX - startX) / scale;
    double localY = (mouseY - startY) / scale;

    for (int i = group.messages.size() - 1; i >= 0; i--) {
      String msgContent = group.messages.get(i);
      Text msg = Text.of(msgContent);
      int msgY = i * lineHeight;
      int msgWidth = this.textRenderer.getWidth(msg);

      if (localY >= msgY && localY < msgY + lineHeight &&
          localX >= -2 && localX <= msgWidth + 2) {

        group.messages.remove(i);

        if (this.client != null) {
          this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
              .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return true;
      }
    }

    return false;
  }

  private boolean isMouseOverGroup(MessageGroup group, double mouseX, double mouseY) {
    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;
    int lineHeight = 12;

    int width = 100;
    int height = 20;

    if (!group.messages.isEmpty()) {
      int maxWidth = 0;
      for (String msg : group.messages) {
        int w = this.textRenderer.getWidth(Text.of(msg));
        if (w > maxWidth)
          maxWidth = w;
      }

      width = maxWidth;
      height = group.isCollapsed ? 0 : group.messages.size() * lineHeight;
    } else {
      width = this.textRenderer.getWidth(Text.translatable("pinchat.gui.positionEdit.exampleMessage"));
      height = lineHeight;
    }

    int scaledWidth = (int) (width * scale);
    int scaledHeight = (int) (height * scale);

    return mouseX >= startX - 4 && mouseX <= startX + scaledWidth + 4 &&
        mouseY >= startY - 4 && mouseY <= startY + scaledHeight + 4;
  }

  private boolean checkDeleteClick(MessageGroup group, double mouseX, double mouseY) {
    if (PinnedMessages.groups.size() <= 1)
      return false;

    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;

    String indicator = group.isCollapsed ? "▶" : "▼";
    int nameWidth = this.textRenderer.getWidth(Text.of(indicator + " " + group.name));
    double btnLocalX = nameWidth + 4;
    double btnLocalY = -12;
    double btnWidth = this.textRenderer.getWidth(Text.of("[x]"));
    double btnHeight = 9;

    double btnGlobalX = startX + btnLocalX * scale;
    double btnGlobalY = startY + btnLocalY * scale;
    double btnGlobalWidth = btnWidth * scale;
    double btnGlobalHeight = btnHeight * scale;

    if (mouseX >= btnGlobalX && mouseX <= btnGlobalX + btnGlobalWidth &&
        mouseY >= btnGlobalY && mouseY <= btnGlobalY + btnGlobalHeight) {

      PinnedMessages.groups.remove(group);

      if (this.client != null) {
        this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
            .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      return true;
    }

    return false;
  }

  private boolean checkResizeClick(MessageGroup group, double mouseX, double mouseY) {
    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;
    int lineHeight = 12;

    int width = 100;
    int height = 20;

    if (!group.messages.isEmpty()) {
      int maxWidth = 0;
      for (String msg : group.messages) {
        int w = this.textRenderer.getWidth(Text.of(msg));
        if (w > maxWidth)
          maxWidth = w;
      }

      width = maxWidth;
      height = group.isCollapsed ? 0 : group.messages.size() * lineHeight;
    } else {
      width = this.textRenderer.getWidth(Text.translatable("pinchat.gui.positionEdit.exampleMessage"));
      height = lineHeight;
    }

    int scaledWidth = (int) (width * scale);
    int scaledHeight = (int) (height * scale);

    int handleX = startX + scaledWidth;
    int handleY = startY + scaledHeight;

    Text arrow = Text.of("↘");
    int arrowW = this.textRenderer.getWidth(arrow);
    int arrowH = this.textRenderer.fontHeight;

    if (mouseX >= handleX - arrowW - 4 && mouseX <= handleX + 4 &&
        mouseY >= handleY - arrowH - 4 && mouseY <= handleY + 4) {
      this.isResizing = true;
      this.resizingGroup = group;
      this.initialMouseX = mouseX;
      this.initialMouseY = mouseY;
      this.initialScale = scale;
      return true;
    }

    return false;
  }

  private boolean checkHeaderClick(MessageGroup group, double mouseX, double mouseY) {
    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;

    String indicator = group.isCollapsed ? "▶" : "▼";
    int nameWidth = this.textRenderer.getWidth(Text.of(indicator + " " + group.name));

    double headerLocalX = 0;
    double headerLocalY = -12;
    double headerWidth = nameWidth;
    double headerHeight = 12;

    double globalX = startX + headerLocalX * scale;
    double globalY = startY + headerLocalY * scale;
    double globalWidth = headerWidth * scale;
    double globalHeight = headerHeight * scale;

    if (mouseX >= globalX && mouseX <= globalX + globalWidth &&
        mouseY >= globalY && mouseY <= globalY + globalHeight) {

      group.isCollapsed = !group.isCollapsed;

      if (this.client != null) {
        this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
            .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
      return true;
    }

    return false;
  }

  private boolean checkHeaderRightClick(MessageGroup group, double mouseX, double mouseY) {
    int startX = group.x;
    int startY = group.y;
    double scale = group.scale;

    String indicator = group.isCollapsed ? "▶" : "▼";
    int nameWidth = this.textRenderer.getWidth(Text.of(indicator + " " + group.name));

    double headerLocalX = 0;
    double headerLocalY = -12;
    double headerWidth = nameWidth;
    double headerHeight = 12;

    double globalX = startX + headerLocalX * scale;
    double globalY = startY + headerLocalY * scale;
    double globalWidth = headerWidth * scale;
    double globalHeight = headerHeight * scale;

    if (mouseX >= globalX && mouseX <= globalX + globalWidth &&
        mouseY >= globalY && mouseY <= globalY + globalHeight) {

      if (this.client != null) {
        this.client.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
            .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.client.setScreen(new GroupRenameScreen(this, group));
      }
      return true;
    }

    return false;
  }

  private boolean checkDragClick(MessageGroup group, double mouseX, double mouseY) {
    if (isMouseOverGroup(group, mouseX, mouseY)) {
      this.isDragging = true;
      this.draggingGroup = group;
      this.dragOffsetX = (int) mouseX - group.x;
      this.dragOffsetY = (int) mouseY - group.y;
      return true;
    }

    return false;
  }

  @Override
  public boolean mouseDragged(Click click, double deltaX, double deltaY) {
    double mouseX = click.x();
    double mouseY = click.y();

    if (this.isDragging && this.draggingGroup != null) {
      int newX = (int) mouseX - this.dragOffsetX;
      int newY = (int) mouseY - this.dragOffsetY;

      this.draggingGroup.x = newX;
      this.draggingGroup.y = newY;

      return true;
    } else if (this.isResizing && this.resizingGroup != null) {
      double diffX = mouseX - this.initialMouseX;
      double diffY = mouseY - this.initialMouseY;

      double diagonal = (diffX + diffY) / 200.0;

      double newScale = this.initialScale + diagonal;
      newScale = Math.max(0.5, Math.min(3.0, newScale));

      this.resizingGroup.scale = newScale;
      return true;
    }

    return super.mouseDragged(click, deltaX, deltaY);
  }

  @Override
  public boolean mouseReleased(Click click) {
    if (click.buttonInfo().button() == 0) {
      this.isDragging = false;
      this.draggingGroup = null;
      this.isResizing = false;
      this.resizingGroup = null;
    }

    return super.mouseReleased(click);
  }

  @Override
  public void close() {
    if (this.client != null) {
      this.client.setScreen(this.parent);
    }
  }
}
