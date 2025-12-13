package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.config.PinChatConfigData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PinnedMessages {
  public static final List<MessageGroup> groups = new ArrayList<>();

  private static final PinnedMessagesManager manager = createManager();

  private static PinnedMessagesManager createManager() {
    PinChatConfigData data = new PinChatConfigData();

    data.groups = groups;

    return new PinnedMessagesManager(data, (msg, overlay) -> {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null) {
        client.player.sendMessage(Text.of(msg), overlay);
      }
    });
  }

  public static MessageGroup getOrCreateDefaultGroup() {
    if (groups.isEmpty()) {
      groups.add(
          new MessageGroup("Default Group", PinChatConfig.pinnedX, PinChatConfig.pinnedY, PinChatConfig.pinnedScale));
    }
    return groups.get(0);
  }

  public static void toggle(Text message, MessageGroup targetGroup) {

    PinChatConfigData data = manager.getConfig();
    data.maxPinnedMessages = PinChatConfig.maxPinnedMessages;
    data.groups = groups;

    manager.toggle(message.getString(), targetGroup);
    PinChatConfig.save();
  }

  public static void toggle(Text message) {
    toggle(message, null);
  }
}
