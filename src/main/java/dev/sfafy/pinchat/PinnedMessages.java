package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinnedMessages {

  public static final List<Text> pinnedList = new ArrayList<>();

  private static final Pattern COUNT_PATTERN = Pattern.compile(" \\(\\d+\\)$");

  private static String normalize(String text) {
    Matcher matcher = COUNT_PATTERN.matcher(text);
    if (matcher.find()) {
      return matcher.replaceAll("");
    }
    return text;
  }

  public static void toggle(Text message) {
    String newContent = message.getString();
    String normalizedNew = normalize(newContent);

    Text existingMatch = null;
    for (Text pinned : pinnedList) {
      if (normalize(pinned.getString()).equals(normalizedNew)) {
        existingMatch = pinned;
        break;
      }
    }

    if (existingMatch != null) {

      pinnedList.remove(existingMatch);
    } else {

      if (pinnedList.size() >= PinChatConfigMalilib.MAX_PINNED_MESSAGES.getIntegerValue()) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
          client.player.sendMessage(Text.of("§cPinChat: Maximum number of pinned messages reached ("
              + PinChatConfigMalilib.MAX_PINNED_MESSAGES.getIntegerValue() + ")"), false);
        }
        return;
      }
      pinnedList.add(message);
    }
  }
}
