package dev.sfafy.pinchat.config;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class PinChatConfig {
  private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "pinchat.json");

  public static int maxPinnedMessages = PinChatConfigData.DEFAULT_MAX_PINNED_MESSAGES;
  public static int maxLineWidth = PinChatConfigData.DEFAULT_MAX_LINE_WIDTH;
  public static double chatSensitivity = PinChatConfigData.DEFAULT_CHAT_SENSITIVITY;
  public static int pinnedX = PinChatConfigData.DEFAULT_PINNED_X;
  public static int pinnedY = PinChatConfigData.DEFAULT_PINNED_Y;
  public static double pinnedScale = PinChatConfigData.DEFAULT_PINNED_SCALE;

  public static void load() {
    PinChatConfigData data = ConfigSerializer.loadFromFile(CONFIG_FILE);

    maxPinnedMessages = data.maxPinnedMessages;
    maxLineWidth = data.maxLineWidth;
    chatSensitivity = data.chatSensitivity;
    if (chatSensitivity == 3.0) {
      chatSensitivity = 1.0;
    }
    pinnedX = data.pinnedX;
    pinnedY = data.pinnedY;
    pinnedScale = data.pinnedScale;

    PinnedMessages.groups.clear();
    if (!data.groups.isEmpty()) {
      PinnedMessages.groups.addAll(data.groups);
    } else {
      PinnedMessages.groups.add(new MessageGroup("Default Group", pinnedX, pinnedY, pinnedScale));
    }
  }

  public static void save() {
    PinChatConfigData data = new PinChatConfigData();
    data.maxPinnedMessages = maxPinnedMessages;
    data.maxLineWidth = maxLineWidth;
    data.chatSensitivity = chatSensitivity;
    data.pinnedX = pinnedX;
    data.pinnedY = pinnedY;
    data.pinnedScale = pinnedScale;
    data.groups.addAll(PinnedMessages.groups);

    ConfigSerializer.saveToFile(data, CONFIG_FILE);
  }
}
