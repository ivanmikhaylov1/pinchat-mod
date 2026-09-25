package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.ConfigSerializer;
import dev.sfafy.pinchat.config.PinChatConfigData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettingsMigrationTest {
  @Test
  void oldConfigKeepsGroupsAndEnablesMoveableChatByDefault() {
    PinChatConfigData data = ConfigSerializer.fromJson("""
        {"groups":[{"name":"Saved","x":42,"y":70,"scale":1.0,"messages":["Hello"]}]}
        """);
    assertTrue(data.moveableChatEnabled);
    assertEquals("Hello", data.groups.get(0).messages.get(0));
    assertEquals(42, data.groups.get(0).x);
  }

  @Test
  void toggleAndNewGroupNameSurviveSerialization() {
    PinChatConfigData data = new PinChatConfigData();
    data.moveableChatEnabled = false;
    List<MessageGroup> groups = new ArrayList<>();
    groups.add(new MessageGroup("Group #1", 100, 100, 1.0));
    MessageGroup next = PinnedMessagesManager.createGroup(groups);
    assertEquals("Group #2", next.name);
    groups.add(next);
    data.groups.addAll(groups);
    PinChatConfigData restored = ConfigSerializer.fromJson(ConfigSerializer.toJson(data));
    assertFalse(restored.moveableChatEnabled);
    assertEquals("Group #2", restored.groups.get(1).name);
  }
}
