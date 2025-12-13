package dev.sfafy.pinchat.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
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
    MAX_PINNED_MESSAGES.setIntegerValue(PinChatConfig.maxPinnedMessages);
    MAX_LINE_WIDTH.setIntegerValue(PinChatConfig.maxLineWidth);
    CHAT_SENSITIVITY.setDoubleValue(PinChatConfig.chatSensitivity);
    PINNED_X.setIntegerValue(PinChatConfig.pinnedX);
    PINNED_Y.setIntegerValue(PinChatConfig.pinnedY);
    PINNED_SCALE.setDoubleValue(PinChatConfig.pinnedScale);
  }

  public static void saveConfig() {
    PinChatConfig.maxPinnedMessages = MAX_PINNED_MESSAGES.getIntegerValue();
    PinChatConfig.maxLineWidth = MAX_LINE_WIDTH.getIntegerValue();
    PinChatConfig.chatSensitivity = CHAT_SENSITIVITY.getDoubleValue();
    PinChatConfig.pinnedX = PINNED_X.getIntegerValue();
    PinChatConfig.pinnedY = PINNED_Y.getIntegerValue();
    PinChatConfig.pinnedScale = PINNED_SCALE.getDoubleValue();
    PinChatConfig.save();
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
