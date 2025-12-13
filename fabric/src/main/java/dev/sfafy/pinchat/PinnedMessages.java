package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinnedMessages {
  public static final List<MessageGroup> groups = new ArrayList<>();

  private static final Pattern COUNT_PATTERN = Pattern.compile(" \\(\\d+\\)$");

  private static String normalize(String text) {
    Matcher matcher = COUNT_PATTERN.matcher(text);
    if (matcher.find()) {
      return matcher.replaceAll("");
    }
    return text;
  }

  public static MessageGroup getOrCreateDefaultGroup() {
    if (groups.isEmpty()) {
      groups.add(
          new MessageGroup("Default Group", PinChatConfig.pinnedX, PinChatConfig.pinnedY, PinChatConfig.pinnedScale));
    }
    return groups.get(0);
  }

  public static void toggle(Text message, MessageGroup targetGroup) {
    if (targetGroup == null) {
      targetGroup = getOrCreateDefaultGroup();
    }

    String newContent = message.getString();
    String normalizedNew = normalize(newContent);

    String existingMatch = null;
    for (String pinned : targetGroup.messages) {
      if (normalize(pinned).equals(normalizedNew)) {
        existingMatch = pinned;
        break;
      }
    }

    if (existingMatch != null) {
      targetGroup.messages.remove(existingMatch);
    } else {
      if (targetGroup.messages.size() >= PinChatConfig.maxPinnedMessages) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
          client.player.sendMessage(
              Text.of("§cPinChat: Maximum number of pinned messages reached (" + PinChatConfig.maxPinnedMessages + ")"),
              false);
        }
        return;
      }
      targetGroup.messages.add(newContent);
    }

    PinChatConfig.save();
  }

  public static void toggle(Text message) {
    toggle(message, null);
  }
}
