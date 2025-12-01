package dev.sfafy.pinchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.sfafy.pinchat.PinChatCommon;
import dev.sfafy.pinchat.platform.Services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigHandler {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final String CONFIG_FILE_NAME = "pinchat.json";
  private static ConfigData configData = new ConfigData();

  public static void load() {
    Path configDir = Services.PLATFORM.getConfigDir();
    Path configFile = configDir.resolve(CONFIG_FILE_NAME);

    if (configFile.toFile().exists()) {
      try (FileReader reader = new FileReader(configFile.toFile())) {
        configData = GSON.fromJson(reader, ConfigData.class);
      } catch (IOException e) {
        PinChatCommon.LOGGER.error("Failed to load config", e);
      }
    } else {
      save();
    }
  }

  public static void save() {
    Path configDir = Services.PLATFORM.getConfigDir();
    Path configFile = configDir.resolve(CONFIG_FILE_NAME);

    try (FileWriter writer = new FileWriter(configFile.toFile())) {
      GSON.toJson(configData, writer);
    } catch (IOException e) {
      PinChatCommon.LOGGER.error("Failed to save config", e);
    }
  }

  public static ConfigData getConfig() {
    return configData;
  }

  public static class ConfigData {
    public int pinnedX = 10;
    public int pinnedY = 10;
    public double pinnedScale = 1.0;
    public int maxPinnedMessages = 10;
    public int maxLineWidth = 200;
    public double chatSensitivity = 1.0;
  }
}
