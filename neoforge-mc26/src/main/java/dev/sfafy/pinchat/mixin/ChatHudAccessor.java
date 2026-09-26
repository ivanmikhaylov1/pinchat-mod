package dev.sfafy.pinchat.mixin;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatHudAccessor {
  @Accessor("trimmedMessages")
  List<GuiMessage.Line> getVisibleMessages();

  @Accessor("chatScrollbarPos")
  int getChatScrollbarPos();

  @Invoker("getWidth")
  int invokeGetWidth();
}
