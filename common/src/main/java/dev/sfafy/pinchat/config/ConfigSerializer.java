package dev.sfafy.pinchat.config;

import com.google.gson.*;
import dev.sfafy.pinchat.MessageGroup;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigSerializer {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static String toJson(PinChatConfigData config) {
    JsonObject json = new JsonObject();
    json.addProperty("maxPinnedMessages", config.maxPinnedMessages);
    json.addProperty("maxLineWidth", config.maxLineWidth);
    json.addProperty("chatSensitivity", config.chatSensitivity);
    json.addProperty("pinnedX", config.pinnedX);
    json.addProperty("pinnedY", config.pinnedY);
    json.addProperty("pinnedScale", config.pinnedScale);

    JsonArray groupsArray = new JsonArray();
    for (MessageGroup group : config.groups) {
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

    return GSON.toJson(json);
  }

  public static PinChatConfigData fromJson(String jsonString) {
    PinChatConfigData config = new PinChatConfigData();

    try {
      JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

      if (json.has("maxPinnedMessages"))
        config.maxPinnedMessages = json.get("maxPinnedMessages").getAsInt();
      if (json.has("maxLineWidth"))
        config.maxLineWidth = json.get("maxLineWidth").getAsInt();
      if (json.has("chatSensitivity"))
        config.chatSensitivity = json.get("chatSensitivity").getAsDouble();
      if (json.has("pinnedX"))
        config.pinnedX = json.get("pinnedX").getAsInt();
      if (json.has("pinnedY"))
        config.pinnedY = json.get("pinnedY").getAsInt();
      if (json.has("pinnedScale"))
        config.pinnedScale = json.get("pinnedScale").getAsDouble();

      config.groups.clear();
      if (json.has("groups")) {
        JsonArray groupsArray = json.getAsJsonArray("groups");
        for (JsonElement element : groupsArray) {
          JsonObject groupObj = element.getAsJsonObject();
          String name = groupObj.get("name").getAsString();
          int x = groupObj.get("x").getAsInt();
          int y = groupObj.get("y").getAsInt();
          double scale = groupObj.get("scale").getAsDouble();
          boolean isCollapsed = groupObj.has("isCollapsed") && groupObj.get("isCollapsed").getAsBoolean();

          MessageGroup group = new MessageGroup(name, x, y, scale);
          group.isCollapsed = isCollapsed;

          if (groupObj.has("messages")) {
            JsonArray messagesArray = groupObj.getAsJsonArray("messages");
            for (JsonElement msgElement : messagesArray) {
              group.messages.add(msgElement.getAsString());
            }
          }

          config.groups.add(group);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return config;
  }

  public static void saveToFile(PinChatConfigData config, File file) {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      writer.write(toJson(config));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static PinChatConfigData loadFromFile(File file) {
    if (!file.exists()) {
      return new PinChatConfigData();
    }

    try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
      StringBuilder sb = new StringBuilder();
      char[] buffer = new char[1024];
      int read;
      while ((read = reader.read(buffer)) != -1) {
        sb.append(buffer, 0, read);
      }
      return fromJson(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return new PinChatConfigData();
    }
  }
}
