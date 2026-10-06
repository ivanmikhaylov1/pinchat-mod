package dev.sfafy.pinchat.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;

/** Poll the correct physical device; an unbound key must never reach GLFW. */
public final class PinChatInput {
  private PinChatInput() {}

  public static boolean isPressed(InputConstants.Key key, Window window) {
    if (key.getValue() < 0) return false;
    if (key.getType() == InputConstants.Type.MOUSE) {
      return GLFW.glfwGetMouseButton(window.handle(), key.getValue()) == GLFW.GLFW_PRESS;
    }
    return key.getType() == InputConstants.Type.KEYSYM && InputConstants.isKeyDown(window, key.getValue());
  }
}
