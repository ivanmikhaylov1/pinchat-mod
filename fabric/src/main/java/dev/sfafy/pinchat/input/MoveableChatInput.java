package dev.sfafy.pinchat.input;

import dev.sfafy.pinchat.mixin.PinChatKeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class MoveableChatInput extends Input {
  private final MinecraftClient client;

  public MoveableChatInput(MinecraftClient client) {
    this.client = client;
    this.tick();
  }

  @Override
  public void tick() {
    if (this.client.player == null)
      return;

    net.minecraft.client.util.Window window = this.client.getWindow();

    boolean forward = isKeyPressed(this.client.options.forwardKey, window);
    boolean back = isKeyPressed(this.client.options.backKey, window);
    boolean left = isKeyPressed(this.client.options.leftKey, window);
    boolean right = isKeyPressed(this.client.options.rightKey, window);
    boolean jump = isKeyPressed(this.client.options.jumpKey, window);
    boolean sneak = isKeyPressed(this.client.options.sneakKey, window);
    boolean sprint = isKeyPressed(this.client.options.sprintKey, window);

    this.playerInput = new net.minecraft.util.PlayerInput(forward, back, left, right, jump, sneak, sprint);

    float movementForward = forward == back ? 0.0F : (forward ? 1.0F : -1.0F);
    float movementSideways = left == right ? 0.0F : (left ? 1.0F : -1.0F);

    this.movementVector = new net.minecraft.util.math.Vec2f(movementSideways, movementForward);
  }

  private boolean isKeyPressed(KeyBinding keyBinding, net.minecraft.client.util.Window window) {
    InputUtil.Key key = ((PinChatKeyBindingAccessor) keyBinding).getBoundKey();
    return InputUtil.isKeyPressed(window, key.getCode());
  }
}
