package dev.sfafy.pinchat.mixin;

import dev.sfafy.pinchat.gui.MoveableChatScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MoveableChatMouseMixin {
  @Shadow private double x;
  @Shadow private double y;
  @Shadow private boolean hasResolutionChanged;

  @Inject(method = "onCursorPos", at = @At("HEAD"))
  private void pinchat$relativeMotion(long window, double cursorX, double cursorY, CallbackInfo ci) {
    MinecraftClient client = MinecraftClient.getInstance();
    if (!hasResolutionChanged && window == client.getWindow().getHandle()
        && client.currentScreen instanceof MoveableChatScreen screen) {
      screen.relativeMouseMoved(cursorX - x, cursorY - y);
    }
  }
}
