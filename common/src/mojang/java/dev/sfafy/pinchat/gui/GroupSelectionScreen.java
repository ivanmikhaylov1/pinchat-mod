package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class GroupSelectionScreen extends Screen {
  private final Screen parent;
  private final Component messageToPin;
  private final int targetX;
  private final int targetY;

  public GroupSelectionScreen(Screen parent, Component messageToPin, int x, int y) {
    super(Component.translatable("pinchat.gui.groupSelection.title"));
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

    this.addRenderableWidget(Button.builder(Component.translatable("pinchat.gui.groupSelection.newGroup"), (button) -> {
      String name = "Group #" + (PinnedMessages.groups.size() + 1);
      MessageGroup newGroup = new MessageGroup(name, 100, 100, 1.0);
      PinnedMessages.groups.add(newGroup);
      PinnedMessages.toggle(this.messageToPin, newGroup);
      this.onClose();
    }).bounds(startX, startY, buttonWidth, buttonHeight).build());

    for (int i = 0; i < PinnedMessages.groups.size(); i++) {
      MessageGroup group = PinnedMessages.groups.get(i);
      int y = startY + (i + 1) * (buttonHeight + spacing);

      this.addRenderableWidget(Button.builder(Component.literal(group.name), (button) -> {
        PinnedMessages.toggle(this.messageToPin, group);
        this.onClose();
      }).bounds(startX, y, buttonWidth, buttonHeight).build());
    }
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean released) {
    if (!super.mouseClicked(event, released)) {
      if (event.button() == 0) {
        double mouseX = event.x();
        double mouseY = event.y();

        int listX = this.width / 2 - 100;
        int listY = 50;
        int itemHeight = 20;

        for (int i = 0; i < PinnedMessages.groups.size(); i++) {
          int itemY = listY + i * itemHeight;
          if (mouseX >= listX && mouseX <= listX + 200 &&
              mouseY >= itemY && mouseY <= itemY + itemHeight) {

            MessageGroup group = PinnedMessages.groups.get(i);
            PinnedMessages.toggle(this.messageToPin, group);
            this.onClose();
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public void onClose() {
    if (this.minecraft != null) {
      this.minecraft.setScreen(this.parent);
    }
  }
}
