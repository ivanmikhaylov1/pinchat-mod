package dev.sfafy.pinchat.integration;

import dev.sfafy.pinchat.gui.PinChatConfigScreen;
import net.minecraft.client.gui.screen.Screen;

/** Compatibility facade for older integrations; settings always use the built-in screen. */
public class IntegrationManager {
  public static void detectMods() {}
  public static boolean isMalilibLoaded() { return false; }
  public static boolean isClothConfigLoaded() { return false; }
  public static boolean isModMenuLoaded() { return false; }
  public static boolean isYaclLoaded() { return false; }

  public static Screen getConfigScreen(Screen parent) {
    return new PinChatConfigScreen(parent);
  }
}
