package dev.sfafy.pinchat.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(),
        "pinchat.json");
    if (configFile.exists()) {
      try (FileReader reader = new FileReader(configFile)) {
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
        if (json.has("maxPinnedMessages")) {
          MAX_PINNED_MESSAGES.setIntegerValue(json.get("maxPinnedMessages").getAsInt());
        }
        if (json.has("maxLineWidth")) {
          MAX_LINE_WIDTH.setIntegerValue(json.get("maxLineWidth").getAsInt());
        }
        if (json.has("chatSensitivity")) {
          CHAT_SENSITIVITY.setDoubleValue(json.get("chatSensitivity").getAsDouble());
        }

        PinnedMessages.groups.clear();
        if (json.has("groups")) {
          JsonArray groupsArray = json.getAsJsonArray("groups");
          for (JsonElement element : groupsArray) {
            JsonObject groupObj = element.getAsJsonObject();
            String name = groupObj.get("name").getAsString();
            int x = groupObj.get("x").getAsInt();
            int y = groupObj.get("y").getAsInt();
            double scale = groupObj.get("scale").getAsDouble();
            boolean isCollapsed = false;
            if (groupObj.has("isCollapsed")) {
              isCollapsed = groupObj.get("isCollapsed").getAsBoolean();
            }

            MessageGroup group = new MessageGroup(name, x, y, scale);
            group.isCollapsed = isCollapsed;

            if (groupObj.has("messages")) {
              JsonArray messagesArray = groupObj.getAsJsonArray("messages");
              for (JsonElement msgElement : messagesArray) {
                group.messages.add(msgElement.getAsString());
              }
            }

            PinnedMessages.groups.add(group);
          }
        } else {
          if (json.has("pinnedX") && json.has("pinnedY") && json.has("pinnedScale")) {

            int x = json.get("pinnedX").getAsInt();
            int y = json.get("pinnedY").getAsInt();
            double scale = json.get("pinnedScale").getAsDouble();
            PinnedMessages.groups
                .add(new MessageGroup("Default Group", x, y, scale));
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void saveConfig() {
    File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(),
        "pinchat.json");
    try (FileWriter writer = new FileWriter(configFile)) {
      JsonObject json = new JsonObject();
      json.addProperty("maxPinnedMessages", MAX_PINNED_MESSAGES.getIntegerValue());
      json.addProperty("maxLineWidth", MAX_LINE_WIDTH.getIntegerValue());
      json.addProperty("chatSensitivity", CHAT_SENSITIVITY.getDoubleValue());

      JsonArray groupsArray = new JsonArray();
      for (MessageGroup group : PinnedMessages.groups) {
        JsonObject groupObj = new JsonObject();
        groupObj.addProperty("name", group.name);
        groupObj.addProperty("x", group.x);
        groupObj.addProperty("y", group.y);
        groupObj.addProperty("scale", group.scale);
        groupObj.addProperty("isCollapsed", group.isCollapsed);

        JsonArray messagesArray = new JsonArray();
        for (String msg : group.messages) {
          messagesArray.add(msg);
        }

        groupObj.add("messages", messagesArray);

        groupsArray.add(groupObj);
      }

      json.add("groups", groupsArray);

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
