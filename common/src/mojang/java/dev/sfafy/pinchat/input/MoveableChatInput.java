package dev.sfafy.pinchat.input;

import dev.sfafy.pinchat.mixin.PinChatKeyBindingAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

public class MoveableChatInput extends KeyboardInput {
  private final Minecraft client;

  public MoveableChatInput(Minecraft client) {
    super(client.options);
    this.client = client;
  }

  @Override
  public void tick() {
    if (this.client.player == null)
      return;

    com.mojang.blaze3d.platform.Window window = this.client.getWindow();

    boolean isForward = isKeyPressed(this.client.options.keyUp, window);
    boolean isBackward = isKeyPressed(this.client.options.keyDown, window);
    boolean isLeft = isKeyPressed(this.client.options.keyLeft, window);
    boolean isRight = isKeyPressed(this.client.options.keyRight, window);
    boolean isJump = isKeyPressed(this.client.options.keyJump, window);
    boolean isShift = isKeyPressed(this.client.options.keyShift, window);
    boolean isSprint = isKeyPressed(this.client.options.keySprint, window);

    this.keyPresses = new Input(
        isForward,
        isBackward,
        isLeft,
        isRight,
        isJump,
        isShift,
        isSprint);

    float forwardImpulse = isForward == isBackward ? 0.0F : (isForward ? 1.0F : -1.0F);
    float strafeImpulse = isLeft == isRight ? 0.0F : (isLeft ? 1.0F : -1.0F);

    this.moveVector = new net.minecraft.world.phys.Vec2(strafeImpulse, forwardImpulse);
  }

  private boolean isKeyPressed(KeyMapping keyMapping, com.mojang.blaze3d.platform.Window window) {
    InputConstants.Key key = ((PinChatKeyBindingAccessor) (Object) keyMapping).getBoundKey();
    return InputConstants.isKeyDown(window, key.getValue());
  }
}
