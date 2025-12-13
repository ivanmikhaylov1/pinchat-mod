package dev.sfafy.pinchat.integration;

import dev.sfafy.pinchat.gui.PinChatConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

public class IntegrationManager {
  private static boolean malilibLoaded = false;
  private static boolean clothConfigLoaded = false;
  private static boolean modMenuLoaded = false;
  private static boolean yaclLoaded = false;

  public static void detectMods() {
    malilibLoaded = FabricLoader.getInstance().isModLoaded("malilib");
    clothConfigLoaded = FabricLoader.getInstance().isModLoaded("cloth-config");
    modMenuLoaded = FabricLoader.getInstance().isModLoaded("modmenu");
    yaclLoaded = FabricLoader.getInstance().isModLoaded("yet-another-config-lib");
  }

  public static boolean isMalilibLoaded() {
    return malilibLoaded;
  }

  public static boolean isClothConfigLoaded() {
    return clothConfigLoaded;
  }

  public static boolean isModMenuLoaded() {
    return modMenuLoaded;
  }

  public static boolean isYaclLoaded() {
    return yaclLoaded;
  }

  public static Screen getConfigScreen(Screen parent) {
    if (isClothConfigLoaded()) {
      return ClothConfigIntegration.createConfigScreen(parent);
    } else if (isMalilibLoaded()) {
      return MalilibIntegration.createConfigScreen(parent);
    } else if (isYaclLoaded()) {
      return YaclIntegration.createConfigScreen(parent);
    } else {
      return new PinChatConfigScreen(parent);
    }
  }
}
