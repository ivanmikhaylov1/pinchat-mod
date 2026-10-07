package dev.sfafy.pinchat.mixin;

import dev.sfafy.pinchat.gui.MoveableChatScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MoveableChatMouseMixin {
  @Shadow private double xpos;
  @Shadow private double ypos;
  @Shadow private boolean ignoreFirstMove;

  @Inject(method = "onMove", at = @At("HEAD"))
  private void pinchat$relativeMotion(long window, double x, double y, CallbackInfo ci) {
    Minecraft client = Minecraft.getInstance();
    if (!ignoreFirstMove && window == client.getWindow().handle()
        && client.screen instanceof MoveableChatScreen screen) {
      screen.relativeMouseMoved(x - xpos, y - ypos);
    }
  }
}
