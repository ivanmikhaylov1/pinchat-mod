package dev.sfafy.pinchat.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.lwjgl.sdl.SDLMouse;

/** SDL uses physical keyboard scancodes and one-based mouse button masks. */
public final class PinChatInput {
  private PinChatInput() {}

  public static boolean isPressed(InputConstants.Key key, Window window) {
    if (key.equals(InputConstants.UNKNOWN)) return false;
    if (key.getType() == InputConstants.Type.MOUSE) {
      return isMousePressed(key.getValue());
    }
    return key.getType() == InputConstants.Type.KEYBOARD
        && key.getValue() > 0 && InputConstants.isKeyDown(key.getValue());
  }

  public static boolean isMousePressed(int button) {
    return button > 0 && button <= 32
        && (SDLMouse.SDL_GetMouseState((java.nio.FloatBuffer) null, (java.nio.FloatBuffer) null) & (1 << (button - 1))) != 0;
  }

  /** Keep the shared chat interaction logic independent of platform button numbering. */
  public static int chatButton(int button) {
    if (button == InputConstants.MOUSE_BUTTON_LEFT) return 0;
    if (button == InputConstants.MOUSE_BUTTON_RIGHT) return 1;
    return -1;
  }
}
