package dev.sfafy.pinchat.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;

import java.io.File;
import java.util.List;

public class PinChatConfigMalilib implements IConfigHandler {
  public static final ConfigInteger MAX_PINNED_MESSAGES = new ConfigInteger("maxPinnedMessages", 5, 1, 100,
      "pinchat.config.maxPinnedMessages.comment");
  public static final ConfigInteger MAX_LINE_WIDTH = new ConfigInteger("maxLineWidth", 200, 50, 1000,
      "pinchat.config.maxLineWidth.comment");
  public static final ConfigDouble CHAT_SENSITIVITY = new ConfigDouble("chatSensitivity", 3.0, 0.1, 10.0,
      "pinchat.config.chatSensitivity.comment");
  public static final ConfigInteger PINNED_X = new ConfigInteger("pinnedX", 10, 0, 10000,
      "pinchat.config.pinnedX.comment");
  public static final ConfigInteger PINNED_Y = new ConfigInteger("pinnedY", 10, 0, 10000,
      "pinchat.config.pinnedY.comment");
  public static final ConfigDouble PINNED_SCALE = new ConfigDouble("pinnedScale", 1.0, 0.5, 3.0,
      "pinchat.config.pinnedScale.comment");

  public static final List<IConfigBase> OPTIONS = ImmutableList.of(
      MAX_PINNED_MESSAGES,
      MAX_LINE_WIDTH,
      MAX_LINE_WIDTH,
      CHAT_SENSITIVITY,
      PINNED_X,
      PINNED_Y,
      PINNED_SCALE);

  public static void loadConfig() {
    File configFile = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile(),
        "pinchat.json");
    if (configFile.exists()) {
      try (java.io.FileReader reader = new java.io.FileReader(configFile)) {
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
        if (json.has("maxPinnedMessages")) {
          MAX_PINNED_MESSAGES.setIntegerValue(json.get("maxPinnedMessages").getAsInt());
        }
        if (json.has("maxLineWidth")) {
          MAX_LINE_WIDTH.setIntegerValue(json.get("maxLineWidth").getAsInt());
        }
        if (json.has("chatSensitivity")) {
          CHAT_SENSITIVITY.setDoubleValue(json.get("chatSensitivity").getAsDouble());
        }
        if (json.has("pinnedX")) {
          PINNED_X.setIntegerValue(json.get("pinnedX").getAsInt());
        }
        if (json.has("pinnedY")) {
          PINNED_Y.setIntegerValue(json.get("pinnedY").getAsInt());
        }
        if (json.has("pinnedScale")) {
          PINNED_SCALE.setDoubleValue(json.get("pinnedScale").getAsDouble());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void saveConfig() {
    File configFile = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile(),
        "pinchat.json");
    try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
      com.google.gson.JsonObject json = new com.google.gson.JsonObject();
      json.addProperty("maxPinnedMessages", MAX_PINNED_MESSAGES.getIntegerValue());
      json.addProperty("maxLineWidth", MAX_LINE_WIDTH.getIntegerValue());
      json.addProperty("chatSensitivity", CHAT_SENSITIVITY.getDoubleValue());
      json.addProperty("pinnedX", PINNED_X.getIntegerValue());
      json.addProperty("pinnedY", PINNED_Y.getIntegerValue());
      json.addProperty("pinnedScale", PINNED_SCALE.getDoubleValue());

      com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
      gson.toJson(json, writer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void load() {
    loadConfig();
  }

  @Override
  public void save() {
    saveConfig();
  }
}
