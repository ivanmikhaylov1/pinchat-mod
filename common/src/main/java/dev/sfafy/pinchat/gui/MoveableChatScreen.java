package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.PinChatCommon;
import dev.sfafy.pinchat.config.ConfigHandler;
import dev.sfafy.pinchat.mixin.ChatScreenAccessor;
import dev.sfafy.pinchat.mixin.PinChatKeyBindingAccessor;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MoveableChatScreen extends ChatScreen {
  private net.minecraft.client.input.Input originalInput;
  private double lastMouseX;
  private double lastMouseY;
  private boolean cursorLocked = false;
  private boolean wasCloseKeyPressed = false;
  private int timeOpened = 0;
  private boolean firstRender = true;

  public MoveableChatScreen(String originalChatText) {
    super(originalChatText, false);
    PinChatCommon.LOGGER.info("MoveableChatScreen: Constructor called with text: '{}'", originalChatText);
  }

  @Override
  protected void init() {
    PinChatCommon.LOGGER.info("MoveableChatScreen: init() called");
    super.init();

    net.minecraft.client.gui.widget.TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
    if (chatField != null) {
      chatField.setVisible(false);
      chatField.active = false;
      chatField.setFocused(false);
    }

    PinChatCommon.LOGGER.info("MoveableChatScreen: init() completed");
  }

  @Override
  public void tick() {
    super.tick();

    if (!cursorLocked && this.client != null) {
      long window = this.client.getWindow().getHandle();
      GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
      cursorLocked = true;
    }

    net.minecraft.client.gui.widget.TextFieldWidget chatField = ((ChatScreenAccessor) this).getChatField();
    if (chatField != null && (chatField.isVisible() || chatField.active || chatField.isFocused())) {
      chatField.setVisible(false);
      chatField.active = false;
      chatField.setFocused(false);
    }

    checkCloseKey();
  }

  private void checkCloseKey() {
    if (this.client == null)
      return;

    boolean isPressed = false;
    net.minecraft.client.util.Window window = this.client.getWindow();
    if (InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_U)) {
      isPressed = true;
    }

    if (isPressed && !wasCloseKeyPressed && timeOpened > 5) {
      this.close();
    }

    wasCloseKeyPressed = isPressed;
    timeOpened++;
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    if (this.client != null && this.client.player != null && cursorLocked) {
      double deltaX = mouseX - lastMouseX;
      double deltaY = mouseY - lastMouseY;

      if (lastMouseX != 0 || lastMouseY != 0) {
        double sensitivity = this.client.options.getMouseSensitivity().getValue();
        double d = sensitivity * 0.6 + 0.2;
        double e = d * d * d * 8.0;

        double configSensitivity = ConfigHandler.getConfig().chatSensitivity;
        double f = e * configSensitivity;

        this.client.player.changeLookDirection(deltaX * f, deltaY * f);
      }

      lastMouseX = mouseX;
      lastMouseY = mouseY;
    }
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    if (firstRender) {
      PinChatCommon.LOGGER.info("MoveableChatScreen: render() called for first time");
      firstRender = false;
    }

    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  public void removed() {
    PinChatCommon.LOGGER.info("MoveableChatScreen: removed() called");

    if (this.client != null) {
      long window = this.client.getWindow().getHandle();
      GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }

    super.removed();

    if (this.client != null && this.client.player != null) {
      if (this.originalInput != null) {
        PinChatCommon.LOGGER.info("MoveableChatScreen: Restoring original input");
        this.client.player.input = this.originalInput;
      }

      restoreKeyPressed(this.client.options.forwardKey);
      restoreKeyPressed(this.client.options.backKey);
      restoreKeyPressed(this.client.options.leftKey);
      restoreKeyPressed(this.client.options.rightKey);
      restoreKeyPressed(this.client.options.jumpKey);
      restoreKeyPressed(this.client.options.sneakKey);
      restoreKeyPressed(this.client.options.sprintKey);
    }
  }

  private void restoreKeyPressed(KeyBinding keyBinding) {
    if (this.client == null)
      return;

    InputUtil.Key key = ((PinChatKeyBindingAccessor) keyBinding).getBoundKey();

    boolean isPressed = InputUtil.isKeyPressed(this.client.getWindow(), key.getCode());

    KeyBinding.setKeyPressed(key, isPressed);
  }

  @Override
  public boolean shouldPause() {
    return false;
  }

  @Override
  public boolean shouldCloseOnEsc() {
    return true;
  }
}