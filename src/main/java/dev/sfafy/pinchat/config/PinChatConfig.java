package dev.sfafy.pinchat.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PinChatConfig {
  private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pinchat.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static int maxPinnedMessages = 5;
  public static int maxLineWidth = 200;
  public static double chatSensitivity = 3.0;
  public static int pinnedX = 10;
  public static int pinnedY = 10;
  public static double pinnedScale = 1.0;

  public static void load() {
    if (!CONFIG_FILE.exists()) {
      save();
      return;
    }

    try (FileReader reader = new FileReader(CONFIG_FILE)) {
      JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

      if (json.has("maxPinnedMessages"))
        maxPinnedMessages = json.get("maxPinnedMessages").getAsInt();
      if (json.has("maxLineWidth"))
        maxLineWidth = json.get("maxLineWidth").getAsInt();
      if (json.has("chatSensitivity"))
        chatSensitivity = json.get("chatSensitivity").getAsDouble();
      if (json.has("pinnedX"))
        pinnedX = json.get("pinnedX").getAsInt();
      if (json.has("pinnedY"))
        pinnedY = json.get("pinnedY").getAsInt();
      if (json.has("pinnedScale"))
        pinnedScale = json.get("pinnedScale").getAsDouble();

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
        if (PinnedMessages.groups.isEmpty()) {
          PinnedMessages.groups.add(new MessageGroup("Default Group", pinnedX, pinnedY, pinnedScale));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void save() {
    try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
      JsonObject json = new JsonObject();
      json.addProperty("maxPinnedMessages", maxPinnedMessages);
      json.addProperty("maxLineWidth", maxLineWidth);
      json.addProperty("chatSensitivity", chatSensitivity);
      json.addProperty("pinnedX", pinnedX);
      json.addProperty("pinnedY", pinnedY);
      json.addProperty("pinnedScale", pinnedScale);

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

      GSON.toJson(json, writer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
