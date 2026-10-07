package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.PinChatMod;

import dev.sfafy.pinchat.mixin.ChatScreenAccessor;
import dev.sfafy.pinchat.mixin.PinChatKeyBindingAccessor;
import dev.sfafy.pinchat.input.PinChatInput;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;

public class MoveableChatScreen extends ChatScreen {
  private net.minecraft.client.player.ClientInput originalInput;
  private boolean cursorLocked = false;
  private boolean wasCloseKeyPressed = false;
  private int timeOpened = 0;
  private boolean firstRender = true;

  public MoveableChatScreen(String originalChatText) {
    super(originalChatText, false);
    PinChatMod.LOGGER.info("MoveableChatScreen: Constructor called with text: '{}'", originalChatText);
  }

  @Override
  protected void init() {
    PinChatMod.LOGGER.info("MoveableChatScreen: init() called");
    super.init();
    if (this.minecraft != null && this.minecraft.player != null) {
      PinChatMod.LOGGER.info("MoveableChatScreen: Setting up custom input");
      if (this.originalInput == null) {
        this.originalInput = this.minecraft.player.input;
      }
      this.minecraft.player.input = new dev.sfafy.pinchat.input.MoveableChatInput(this.minecraft);
    } else {
      PinChatMod.LOGGER.warn("MoveableChatScreen: minecraft or player is null!");
    }

    EditBox chatField = ((ChatScreenAccessor) this).getChatField();
    if (chatField != null) {
      chatField.setVisible(false);
      chatField.active = false;
      chatField.setFocused(false);
    }

    PinChatMod.LOGGER.info("MoveableChatScreen: init() completed");
  }

  @Override
  public void tick() {
    super.tick();

    if (!cursorLocked && this.minecraft != null) {
      long window = this.minecraft.getWindow().handle();
      GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
      cursorLocked = true;
    }

    EditBox chatField = ((ChatScreenAccessor) this).getChatField();
    if (chatField != null && (chatField.isVisible() || chatField.active || chatField.isFocused())) {
      chatField.setVisible(false);
      chatField.active = false;
      chatField.setFocused(false);
    }

    checkCloseKey();
  }

  private void checkCloseKey() {
    if (this.minecraft == null)
      return;

    boolean isPressed = false;

    KeyMapping openMoveableChatKey = dev.sfafy.pinchat.keybindings.PinChatKeyBindings.openMoveableChatKey;
    if (openMoveableChatKey != null) {
      InputConstants.Key boundKey = ((PinChatKeyBindingAccessor) (Object) openMoveableChatKey).getBoundKey();
      if (PinChatInput.isPressed(boundKey, this.minecraft.getWindow())) {
        isPressed = true;
      }
    }

    if (isPressed && !wasCloseKeyPressed && timeOpened > 5) {
      this.onClose();
    }

    wasCloseKeyPressed = isPressed;
    timeOpened++;
  }

  @Override
  public void mouseMoved(double x, double y) {
    // Camera deltas come from the native mouse callback before GUI coordinate conversion.
  }

  public void relativeMouseMoved(double dx, double dy) {
    if (this.minecraft == null || this.minecraft.player == null || !cursorLocked
        || !this.minecraft.isWindowActive()) return;
    double sensitivity = this.minecraft.options.sensitivity().get() * 0.6 + 0.2;
    double scale = sensitivity * sensitivity * sensitivity * 8.0;
    this.minecraft.player.turn(dx * scale * (this.minecraft.options.invertMouseX().get() ? -1 : 1),
        dy * scale * (this.minecraft.options.invertMouseY().get() ? -1 : 1));
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    if (firstRender) {
      PinChatMod.LOGGER.info("MoveableChatScreen: render() called for first time");
      firstRender = false;
    }

    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void removed() {
    PinChatMod.LOGGER.info("MoveableChatScreen: removed() called");

    if (this.minecraft != null) {
      long window = this.minecraft.getWindow().handle();
      GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    cursorLocked = false;
    super.removed();

    if (this.minecraft != null && this.minecraft.player != null) {
      if (this.originalInput != null) {
        PinChatMod.LOGGER.info("MoveableChatScreen: Restoring original input");
        this.minecraft.player.input = this.originalInput;
        this.originalInput = null;
      }

      restoreKeyPressed(this.minecraft.options.keyUp);
      restoreKeyPressed(this.minecraft.options.keyDown);
      restoreKeyPressed(this.minecraft.options.keyLeft);
      restoreKeyPressed(this.minecraft.options.keyRight);
      restoreKeyPressed(this.minecraft.options.keyJump);
      restoreKeyPressed(this.minecraft.options.keyShift);
      restoreKeyPressed(this.minecraft.options.keySprint);
    }
  }

  private void restoreKeyPressed(KeyMapping keyMapping) {
    if (this.minecraft == null)
      return;

    InputConstants.Key key = ((PinChatKeyBindingAccessor) (Object) keyMapping).getBoundKey();

    com.mojang.blaze3d.platform.Window window = this.minecraft.getWindow();
    boolean isPressed = PinChatInput.isPressed(key, window);

    keyMapping.setDown(isPressed);
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
