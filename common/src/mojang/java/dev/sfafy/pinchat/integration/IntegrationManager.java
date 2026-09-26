package dev.sfafy.pinchat.integration;

import dev.sfafy.pinchat.gui.PinChatConfigScreen;
import net.minecraft.client.gui.screens.Screen;

public class IntegrationManager {
  private static boolean malilibLoaded = false;
  private static boolean clothConfigLoaded = false;
  private static boolean modMenuLoaded = false;
  private static boolean yaclLoaded = false;

  public static void detectMods() {

  }

  public static boolean isMalilibLoaded() {
    return false;
  }

  public static boolean isClothConfigLoaded() {
    return false;
  }

  public static boolean isModMenuLoaded() {
    return false;
  }

  public static boolean isYaclLoaded() {
    return false;
  }

  public static Screen getConfigScreen(Screen parent) {
    return new PinChatConfigScreen(parent);
  }
}
