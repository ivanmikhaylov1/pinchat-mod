package dev.sfafy.pinchat.config;

import dev.sfafy.pinchat.MessageGroup;

import java.util.ArrayList;
import java.util.List;

public class PinChatConfigData {

  public static final int DEFAULT_MAX_PINNED_MESSAGES = 5;
  public static final int DEFAULT_MAX_LINE_WIDTH = 200;
  public static final double DEFAULT_CHAT_SENSITIVITY = 1.0;
  public static final int DEFAULT_PINNED_X = 10;
  public static final int DEFAULT_PINNED_Y = 10;
  public static final double DEFAULT_PINNED_SCALE = 1.0;

  public int maxPinnedMessages = DEFAULT_MAX_PINNED_MESSAGES;
  public int maxLineWidth = DEFAULT_MAX_LINE_WIDTH;
  public double chatSensitivity = DEFAULT_CHAT_SENSITIVITY;
  public int pinnedX = DEFAULT_PINNED_X;
  public int pinnedY = DEFAULT_PINNED_Y;
  public double pinnedScale = DEFAULT_PINNED_SCALE;
  public List<MessageGroup> groups = new ArrayList<>();

  public PinChatConfigData() {
  }

  public void reset() {
    maxPinnedMessages = DEFAULT_MAX_PINNED_MESSAGES;
    maxLineWidth = DEFAULT_MAX_LINE_WIDTH;
    chatSensitivity = DEFAULT_CHAT_SENSITIVITY;
    pinnedX = DEFAULT_PINNED_X;
    pinnedY = DEFAULT_PINNED_Y;
    pinnedScale = DEFAULT_PINNED_SCALE;
    groups.clear();
  }

  public MessageGroup getOrCreateDefaultGroup() {
    if (groups.isEmpty()) {
      groups.add(new MessageGroup("Default Group", pinnedX, pinnedY, pinnedScale));
    }
    return groups.get(0);
  }

  public PinChatConfigData copy() {
    PinChatConfigData copy = new PinChatConfigData();
    copy.maxPinnedMessages = this.maxPinnedMessages;
    copy.maxLineWidth = this.maxLineWidth;
    copy.chatSensitivity = this.chatSensitivity;
    copy.pinnedX = this.pinnedX;
    copy.pinnedY = this.pinnedY;
    copy.pinnedScale = this.pinnedScale;
    for (MessageGroup group : this.groups) {
      copy.groups.add(group.copy());
    }
    return copy;
  }
}
