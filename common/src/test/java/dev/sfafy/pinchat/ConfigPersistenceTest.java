package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.ConfigSerializer;
import dev.sfafy.pinchat.config.PinChatConfigData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigPersistenceTest {
  @TempDir Path directory;

  @Test
  void diskRoundTripKeepsUnicodeGroupsAndEveryDisplaySetting() throws Exception {
    var data = new PinChatConfigData();
    data.moveableChatEnabled = false;
    data.maxPinnedMessages = 12;
    data.maxLineWidth = 320;
    data.chatSensitivity = 0.75;
    data.pinnedX = 30;
    data.pinnedY = 40;
    data.pinnedScale = 1.25;
    var group = new MessageGroup("Координаты 🧭", 123, 87, 2.25);
    group.isCollapsed = true;
    group.messages.addAll(List.of("База: -42, 64, 180", "§aWaypoint\nSecond line"));
    data.groups.add(group);
    data.groups.add(new MessageGroup("Empty", 250, 180, 0.5));
    Path file = directory.resolve("pinchat.json");
    ConfigSerializer.saveToFile(data, file.toFile());
    assertTrue(Files.readString(file).contains("Координаты"));
    var restored = ConfigSerializer.loadFromFile(file.toFile());
    assertEquals(ConfigSerializer.toJson(data), ConfigSerializer.toJson(restored));
    assertNotSame(group, restored.groups.getFirst());
  }

  @Test
  void missingFileReturnsDefaultsWithoutCreatingAFile() {
    Path file = directory.resolve("missing.json");
    var loaded = ConfigSerializer.loadFromFile(file.toFile());
    assertEquals(ConfigSerializer.toJson(new PinChatConfigData()), ConfigSerializer.toJson(loaded));
    assertFalse(Files.exists(file));
  }

  @Test
  void configCopyDoesNotShareMutableGroupsOrMessages() {
    var data = new PinChatConfigData();
    var group = data.getOrCreateDefaultGroup();
    group.messages.add("Original");
    group.isCollapsed = true;
    var copy = data.copy();
    copy.groups.getFirst().messages.clear();
    copy.groups.getFirst().x = 999;
    copy.groups.clear();
    assertEquals(List.of("Original"), group.messages);
    assertEquals(PinChatConfigData.DEFAULT_PINNED_X, group.x);
    assertTrue(group.isCollapsed);
    assertEquals(1, data.groups.size());
  }
}
