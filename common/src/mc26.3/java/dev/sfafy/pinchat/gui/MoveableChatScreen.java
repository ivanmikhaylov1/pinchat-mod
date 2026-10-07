package dev.sfafy.pinchat.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.input.MoveableChatInput;
import dev.sfafy.pinchat.input.PinChatInput;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import dev.sfafy.pinchat.mixin.ChatScreenAccessor;
import dev.sfafy.pinchat.mixin.PinChatKeyBindingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.ClientInput;

/** SDL reports camera motion as relative deltas, independently of the cursor position. */
public class MoveableChatScreen extends ChatScreen {
  private ClientInput originalInput;
  private boolean cursorLocked;
  private boolean wasCloseKeyPressed;
  private int timeOpened;

  public MoveableChatScreen(String originalChatText) {
    super(originalChatText, false);
  }

  @Override
  protected void init() {
    super.init();
    if (minecraft.player != null) {
      if (originalInput == null) originalInput = minecraft.player.input;
      minecraft.player.input = new MoveableChatInput(minecraft);
    }
    hideChatField();
    PinChatMod.LOGGER.info("MoveableChatScreen: init() completed");
  }

  private void hideChatField() {
    EditBox field = ((ChatScreenAccessor) this).getChatField();
    if (field != null) {
      field.setVisible(false);
      field.active = false;
      field.setFocused(false);
    }
  }

  @Override
  public void tick() {
    super.tick();
    if (!cursorLocked) {
      InputConstants.grabMouse(minecraft.getWindow(), minecraft.mouseHandler.xpos(), minecraft.mouseHandler.ypos());
      cursorLocked = true;
    }
    hideChatField();
    KeyMapping mapping = PinChatKeyBindings.openMoveableChatKey;
    boolean pressed = mapping != null && PinChatInput.isPressed(
        ((PinChatKeyBindingAccessor) (Object) mapping).getBoundKey(), minecraft.getWindow());
    if (pressed && !wasCloseKeyPressed && timeOpened > 5) onClose();
    wasCloseKeyPressed = pressed;
    timeOpened++;
  }

  public void relativeMouseMoved(double dx, double dy) {
    if (!cursorLocked || minecraft.player == null || !minecraft.isWindowActive()) return;
    double sensitivity = minecraft.options.sensitivity().get() * 0.6 + 0.2;
    double scale = sensitivity * sensitivity * sensitivity * 8.0;
    minecraft.player.turn(dx * scale * (minecraft.options.invertMouseX().get() ? -1 : 1),
        dy * scale * (minecraft.options.invertMouseY().get() ? -1 : 1));
  }

  @Override
  public void mouseMoved(double x, double y) {
    // Absolute cursor coordinates stay fixed during SDL relative mode.
  }

  @Override
  public void removed() {
    InputConstants.releaseMouse(minecraft.getWindow(), minecraft.mouseHandler.xpos(), minecraft.mouseHandler.ypos());
    cursorLocked = false;
    super.removed();
    if (minecraft.player != null && originalInput != null) {
      PinChatMod.LOGGER.info("MoveableChatScreen: Restoring original input");
      minecraft.player.input = originalInput;
      originalInput = null;
      for (KeyMapping mapping : new KeyMapping[] {minecraft.options.keyUp, minecraft.options.keyDown,
          minecraft.options.keyLeft, minecraft.options.keyRight, minecraft.options.keyJump,
          minecraft.options.keyShift, minecraft.options.keySprint}) {
        mapping.setDown(PinChatInput.isPressed(
            ((PinChatKeyBindingAccessor) (Object) mapping).getBoundKey(), minecraft.getWindow()));
      }
    }
  }

  @Override
  public boolean isPauseScreen() { return false; }
}
