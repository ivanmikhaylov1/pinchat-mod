package dev.sfafy.pinchat.mixin;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

import dev.sfafy.pinchat.config.PinChatConfig;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.sfafy.pinchat.MessageGroup;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
  @Unique
  private boolean isDragging = false;
  @Unique
  private boolean isResizing = false;
  @Unique
  private MessageGroup draggingGroup = null;
  @Unique
  private MessageGroup resizingGroup = null;
  @Unique
  private double lastMouseX = 0;
  @Unique
  private double lastMouseY = 0;
  @Unique
  private double initialScale = 1.0;
  @Unique
  private double initialMouseX = 0;
  @Unique
  private double initialMouseY = 0;

  @Inject(method = "render", at = @At("TAIL"))
  private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo cir) {
    if (this.isDragging || this.isResizing) {
      if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) != GLFW.GLFW_PRESS) {
        this.isDragging = false;
        this.isResizing = false;
        this.draggingGroup = null;
        this.resizingGroup = null;
        PinChatConfig.save();
      } else {
        if (this.isDragging && this.draggingGroup != null) {
          double deltaX = mouseX - this.lastMouseX;
          double deltaY = mouseY - this.lastMouseY;
          this.draggingGroup.x += (int) deltaX;
          this.draggingGroup.y += (int) deltaY;
        } else if (this.isResizing && this.resizingGroup != null) {
          double diffX = mouseX - this.initialMouseX;
          double diffY = mouseY - this.initialMouseY;
          double diagonal = (diffX + diffY) / 200.0;
          double newScale = this.initialScale + diagonal;
          newScale = Math.max(0.5, Math.min(3.0, newScale));
          this.resizingGroup.scale = newScale;
        }
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
      }
    }

    if (!PinnedMessages.groups.isEmpty()) {
      for (MessageGroup group : PinnedMessages.groups) {
        if (group.messages.isEmpty())
          continue;

        int startX = group.x;
        int startY = group.y;
        double scale = group.scale;
        int lineHeight = 12;

        int width = 0;
        for (String msgContent : group.messages) {
          Text msg = Text.of(msgContent);
          int w = MinecraftClient.getInstance().textRenderer.getWidth(msg);
          if (w > width)
            width = w;
        }
        int height = group.isCollapsed ? 0 : group.messages.size() * lineHeight;

        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);

        int handleX = startX + scaledWidth;
        int handleY = startY + scaledHeight;
        Text arrow = Text.of("↘");
        int arrowW = MinecraftClient.getInstance().textRenderer.getWidth(arrow);
        int arrowH = MinecraftClient.getInstance().textRenderer.fontHeight;

        if (mouseX >= startX - 10 && mouseX <= startX + scaledWidth + 20 &&
            mouseY >= startY - 20 && mouseY <= startY + scaledHeight + 20) {
          context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, arrow, handleX - arrowW,
              handleY - arrowH, 0xFFFFFFFF);

          context.fill(startX - 2, startY - 14, startX + scaledWidth + 2, startY + scaledHeight + 2, 0x20FFFFFF);
          String indicator = group.isCollapsed ? "▶" : "▼";
          int nameWidth = MinecraftClient.getInstance().textRenderer.getWidth(Text.of(indicator + " " + group.name));

          double btnLocalY = -12;
          double renameBtnLocalX = nameWidth + 8;

          int globalRenameX = (int) (startX + renameBtnLocalX * scale);
          int globalBtnY = (int) (startY + btnLocalY * scale);

          context.drawText(MinecraftClient.getInstance().textRenderer, Text.of("[R]"), globalRenameX, globalBtnY,
              0xFFFFFF00,
              true);

          int deleteBtnLocalX = (int) (renameBtnLocalX
              + MinecraftClient.getInstance().textRenderer.getWidth(Text.of("[R]")) + 4);
          int globalDeleteX = (int) (startX + deleteBtnLocalX * scale);

          context.drawText(MinecraftClient.getInstance().textRenderer, Text.of("[X]"), globalDeleteX, globalBtnY,
              0xFFFF5555,
              true);
        }
      }
    }
  }

  @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
  private void onMouseClicked(Click click, boolean isRightClick, CallbackInfoReturnable<Boolean> cir) {
    int button = click.buttonInfo().button();
    if (button != 0 && button != 1)
      return;

    double mouseX = click.x();
    double mouseY = click.y();

    int lineHeight = 12;

    if (!PinnedMessages.groups.isEmpty()) {
      for (int i = PinnedMessages.groups.size() - 1; i >= 0; i--) {
        MessageGroup group = PinnedMessages.groups.get(i);
        if (group.messages.isEmpty())
          continue;

        int startX = group.x;
        int startY = group.y;
        double scale = group.scale;

        int width = 0;
        for (String msgContent : group.messages) {
          Text msg = Text.of(msgContent);
          int w = MinecraftClient.getInstance().textRenderer.getWidth(msg);
          if (w > width)
            width = w;
        }
        int height = group.isCollapsed ? 0 : group.messages.size() * lineHeight;
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);

        if (button == 0) {
          int handleX = startX + scaledWidth;
          int handleY = startY + scaledHeight;
          Text arrow = Text.of("↘");
          int arrowW = MinecraftClient.getInstance().textRenderer.getWidth(arrow);
          int arrowH = MinecraftClient.getInstance().textRenderer.fontHeight;

          if (mouseX >= handleX - arrowW - 4 && mouseX <= handleX + 4 &&
              mouseY >= handleY - arrowH - 4 && mouseY <= handleY + 4) {
            this.isResizing = true;
            this.resizingGroup = group;
            this.initialMouseX = mouseX;
            this.initialMouseY = mouseY;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.initialScale = scale;
            cir.setReturnValue(true);
            return;
          }
        }

        if (button == 0) {
          String indicator = group.isCollapsed ? "▶" : "▼";
          int nameWidth = MinecraftClient.getInstance().textRenderer.getWidth(Text.of(indicator + " " + group.name));

          double headerLocalX = 0;
          double headerLocalY = -12;
          double headerWidth = nameWidth;
          double headerHeight = 12;

          double globalX = startX + headerLocalX * scale;
          double globalY = startY + headerLocalY * scale;
          double globalWidth = headerWidth * scale;
          double globalHeight = headerHeight * scale;

          int renameBtnX = nameWidth + 8;
          int renameBtnW = MinecraftClient.getInstance().textRenderer.getWidth(Text.of("[R]"));
          double globalRenameX = startX + renameBtnX * scale;
          double globalRenameW = renameBtnW * scale;

          if (mouseX >= globalRenameX && mouseX <= globalRenameX + globalRenameW &&
              mouseY >= globalY && mouseY <= globalY + globalHeight) {
            MinecraftClient.getInstance().getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            MinecraftClient.getInstance().setScreen(
                new dev.sfafy.pinchat.gui.GroupRenameScreen(MinecraftClient.getInstance().currentScreen, group));
            cir.setReturnValue(true);
            return;
          }

          int deleteBtnX = renameBtnX + renameBtnW + 4;
          int deleteBtnW = MinecraftClient.getInstance().textRenderer.getWidth(Text.of("[X]"));
          double globalDeleteX = startX + deleteBtnX * scale;
          double globalDeleteW = deleteBtnW * scale;

          if (mouseX >= globalDeleteX && mouseX <= globalDeleteX + globalDeleteW &&
              mouseY >= globalY && mouseY <= globalY + globalHeight) {
            PinnedMessages.groups.remove(i);
            PinChatConfig.save();
            MinecraftClient.getInstance().getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            cir.setReturnValue(true);
            return;
          }

          if (mouseX >= globalX && mouseX <= globalX + globalWidth &&
              mouseY >= globalY && mouseY <= globalY + globalHeight) {
            group.isCollapsed = !group.isCollapsed;
            PinChatConfig.save();
            MinecraftClient.getInstance().getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance
                .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            cir.setReturnValue(true);
            return;
          }
        }

        if (!group.isCollapsed) {
          if (button == 1) {
            for (int j = 0; j < group.messages.size(); j++) {
              int rowY = j * lineHeight;
              double top = startY + (rowY - 2) * scale;
              double bottom = startY + (rowY + 8) * scale;

              if (mouseY >= top && mouseY <= bottom) {
                String msgContent = group.messages.get(j);
                Text msg = Text.of(msgContent);
                int maxWidth = dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth;
                net.minecraft.text.StringVisitable trimmed = MinecraftClient.getInstance().textRenderer.trimToWidth(msg,
                    maxWidth);
                net.minecraft.text.OrderedText renderedText = net.minecraft.util.Language.getInstance()
                    .reorder(trimmed);
                int msgWidth = MinecraftClient.getInstance().textRenderer.getWidth(renderedText);
                double localX = (mouseX - startX) / scale;

                if (localX >= -2 && localX <= msgWidth + 2) {
                  group.messages.remove(j);
                  dev.sfafy.pinchat.config.PinChatConfig.save();
                  MinecraftClient.getInstance().getSoundManager()
                      .play(net.minecraft.client.sound.PositionedSoundInstance
                          .master(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK, 1.0F));
                  cir.setReturnValue(true);
                  return;
                }
              }
            }
          }

          if (button == 0) {
            if (mouseX >= startX && mouseX <= startX + scaledWidth &&
                mouseY >= startY && mouseY <= startY + scaledHeight) {
              this.isDragging = true;
              this.draggingGroup = group;
              this.lastMouseX = mouseX;
              this.lastMouseY = mouseY;
              cir.setReturnValue(true);
              return;
            }
          }
        }
      }
    }

    if (button != 1)
      return;

    MinecraftClient client = MinecraftClient.getInstance();
    ChatHud chatHud = client.inGameHud.getChatHud();

    List<ChatHudLine.Visible> visibleMessages = ((ChatHudAccessor) chatHud).getVisibleMessages();

    if (visibleMessages == null || visibleMessages.isEmpty()) {
      return;
    }

    double chatScale = client.options.getChatScale().getValue();
    int windowHeight = client.getWindow().getScaledHeight();

    double chatX = (mouseX - 4.0) / chatScale;

    double chatBottomY = windowHeight - 40.0;
    double yFromBottom = (chatBottomY - mouseY) / chatScale;
    int lineIndex = (int) (yFromBottom / 9.0);

    int chatWidth = chatHud.getWidth();

    if (chatX >= 0 && chatX <= chatWidth / chatScale && lineIndex >= 0 && lineIndex < visibleMessages.size()) {
      ChatHudLine.Visible visibleLine = visibleMessages.get(lineIndex);

      int creationTick = visibleLine.addedTime();

      List<ChatHudLine> messages = ((ChatHudAccessor) chatHud).getMessages();
      Text messageContent = null;

      for (ChatHudLine message : messages) {
        if (message.creationTick() == creationTick) {
          messageContent = message.content();
          break;
        }
      }

      if (messageContent != null) {
        MinecraftClient.getInstance().setScreen(new dev.sfafy.pinchat.gui.GroupSelectionScreen(client.currentScreen,
            messageContent, (int) mouseX, (int) mouseY));

        if (client.player != null) {
          client.player.playSound(
              net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(),
              0.5f,
              1.0f);
        }

        cir.setReturnValue(true);
      } else {
        PinChatMod.LOGGER.warn("Could not find matching ChatHudLine for visible line with tick {}", creationTick);
      }
    }
  }
}
