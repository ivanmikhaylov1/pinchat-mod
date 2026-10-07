package dev.sfafy.pinchat.input;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

/** Poll the correct physical device; an unbound key must never reach GLFW. */
public final class PinChatInput {
  private PinChatInput() {}

  public static boolean isPressed(InputUtil.Key key, Window window) {
    if (key.getCode() < 0) return false;
    if (key.getCategory() == InputUtil.Type.MOUSE) {
      return GLFW.glfwGetMouseButton(window.getHandle(), key.getCode()) == GLFW.GLFW_PRESS;
    }
    return key.getCategory() == InputUtil.Type.KEYSYM && InputUtil.isKeyPressed(window, key.getCode());
  }
}
