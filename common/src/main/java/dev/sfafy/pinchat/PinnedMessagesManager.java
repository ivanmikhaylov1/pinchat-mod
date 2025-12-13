package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfigData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinnedMessagesManager {
  private static final Pattern COUNT_PATTERN = Pattern.compile(" \\(\\d+\\)$");

  private final PinChatConfigData config;
  private final MessageCallback messageCallback;

  @FunctionalInterface
  public interface MessageCallback {
    void displayMessage(String message, boolean overlay);
  }

  public PinnedMessagesManager(PinChatConfigData config, MessageCallback messageCallback) {
    this.config = config;
    this.messageCallback = messageCallback;
  }

  public static String normalize(String text) {
    Matcher matcher = COUNT_PATTERN.matcher(text);
    if (matcher.find()) {
      return matcher.replaceAll("");
    }
    return text;
  }

  public MessageGroup getOrCreateDefaultGroup() {
    return config.getOrCreateDefaultGroup();
  }

  public boolean toggle(String messageContent, MessageGroup targetGroup) {
    if (targetGroup == null) {
      targetGroup = getOrCreateDefaultGroup();
    }

    String normalizedNew = normalize(messageContent);

    String existingMatch = null;
    for (String pinned : targetGroup.messages) {
      if (normalize(pinned).equals(normalizedNew)) {
        existingMatch = pinned;
        break;
      }
    }

    if (existingMatch != null) {

      targetGroup.messages.remove(existingMatch);
      return false;
    } else {

      if (targetGroup.messages.size() >= config.maxPinnedMessages) {
        if (messageCallback != null) {
          messageCallback.displayMessage(
              "§cPinChat: Maximum number of pinned messages reached (" + config.maxPinnedMessages + ")",
              false);
        }
        return false;
      }

      targetGroup.messages.add(messageContent);
      return true;
    }
  }

  public boolean toggle(String messageContent) {
    return toggle(messageContent, null);
  }

  public PinChatConfigData getConfig() {
    return config;
  }
}
