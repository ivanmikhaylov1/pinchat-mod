package dev.sfafy.pinchat.mixin;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

  @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
  private void onMouseClicked(Click click, boolean isRightClick, CallbackInfoReturnable<Boolean> cir) {
    if (click.buttonInfo().button() != 1)
      return;

    double mouseX = click.x();
    double mouseY = click.y();

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
          PinnedMessages.toggle(messageContent);

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