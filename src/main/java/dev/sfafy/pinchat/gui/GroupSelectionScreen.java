package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GroupSelectionScreen extends Screen {
  private final Screen parent;
  private final Text messageToPin;
  private final int targetX;
  private final int targetY;

  public GroupSelectionScreen(Screen parent, Text messageToPin, int x, int y) {
    super(Text.translatable("pinchat.gui.groupSelection.title"));
    this.parent = parent;
    this.messageToPin = messageToPin;
    this.targetX = x;
    this.targetY = y;
  }

  @Override
  protected void init() {
    super.init();
    int buttonWidth = 120;
    int buttonHeight = 20;
    int spacing = 5;

    int startY = this.targetY - ((PinnedMessages.groups.size() + 1) * (buttonHeight + spacing));
    if (startY < 0)
      startY = this.targetY + 10;

    int startX = this.targetX;
    if (startX + buttonWidth > this.width)
      startX = this.width - buttonWidth - 5;

    this.addDrawableChild(ButtonWidget.builder(Text.translatable("pinchat.gui.groupSelection.newGroup"), (button) -> {
      String name = "Group #" + (PinnedMessages.groups.size() + 1);
      MessageGroup newGroup = new MessageGroup(name, 100, 100, 1.0);
      PinnedMessages.groups.add(newGroup);
      PinnedMessages.toggle(this.messageToPin, newGroup);
      this.close();
    }).dimensions(startX, startY, buttonWidth, buttonHeight).build());

    for (int i = 0; i < PinnedMessages.groups.size(); i++) {
      MessageGroup group = PinnedMessages.groups.get(i);
      int y = startY + (i + 1) * (buttonHeight + spacing);

      this.addDrawableChild(ButtonWidget.builder(Text.of(group.name), (button) -> {
        PinnedMessages.toggle(this.messageToPin, group);
        this.close();
      }).dimensions(startX, y, buttonWidth, buttonHeight).build());
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean isRightClick) {
    if (!super.mouseClicked(click, isRightClick)) {
      this.close();
      return true;
    }

    return true;
  }

  @Override
  public void close() {
    if (this.client != null) {
      this.client.setScreen(this.parent);
    }
  }
}
