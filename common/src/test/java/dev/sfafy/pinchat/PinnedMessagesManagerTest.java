package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfigData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PinnedMessagesManagerTest {
  @ParameterizedTest
  @CsvSource({
      "'Test (5)', 'Test (10)'",
      "'Test (5)', 'Test'",
      "'Message (0)', 'Message (999)'",
      "'Hello', 'Hello'"
  })
  void equivalentMessagesUnpinInsteadOfDuplicating(String first, String second) {
    var data = new PinChatConfigData();
    var manager = new PinnedMessagesManager(data, null);
    assertTrue(manager.toggle(first));
    assertFalse(manager.toggle(second));
    assertTrue(data.groups.getFirst().messages.isEmpty());
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\n", "\t", "Координаты 🧭", "§aColored",
      "Test (not a number)", "Test (123) extra", "Test (-5)", "Test (5.5)", "Test ("})
  void preservesTextThatDoesNotEndInACounter(String message) {
    assertEquals(message, PinnedMessagesManager.normalize(message));
    var manager = new PinnedMessagesManager(new PinChatConfigData(), null);
    assertTrue(manager.toggle(message));
    assertEquals(List.of(message), manager.getOrCreateDefaultGroup().messages);
  }

  @Test
  void duplicateCounterTogglesExistingMessageWithoutChangingOtherGroups() {
    var data = new PinChatConfigData();
    var manager = new PinnedMessagesManager(data, null);
    var first = manager.getOrCreateDefaultGroup();
    var second = PinnedMessagesManager.createGroup(data.groups);
    data.groups.add(second);
    assertTrue(manager.toggle("Coordinates (2)", first));
    assertTrue(manager.toggle("Coordinates", second));
    assertFalse(manager.toggle("Coordinates (3)", first));
    assertTrue(first.messages.isEmpty());
    assertEquals(List.of("Coordinates"), second.messages);
  }

  @Test
  void limitIsPerGroupAndStillAllowsUnpinningWhenFull() {
    var data = new PinChatConfigData();
    data.maxPinnedMessages = 1;
    List<String> warnings = new ArrayList<>();
    var manager = new PinnedMessagesManager(data, (text, overlay) -> {
      assertFalse(overlay);
      warnings.add(text);
    });
    var first = manager.getOrCreateDefaultGroup();
    var second = PinnedMessagesManager.createGroup(data.groups);
    data.groups.add(second);
    assertTrue(manager.toggle("First", first));
    assertFalse(manager.toggle("Overflow", first));
    assertEquals(List.of("First"), first.messages);
    assertEquals(1, warnings.size());
    assertTrue(warnings.getFirst().contains("(1)"));
    assertTrue(manager.toggle("Second", second));
    assertFalse(manager.toggle("First", first));
    assertTrue(first.messages.isEmpty());
    assertEquals(1, warnings.size());
    assertTrue(manager.toggle("Replacement", first));
  }

  @Test
  void generatedGroupUsesFirstFreeNameAndBoundedOffset() {
    List<MessageGroup> groups = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      groups.add(new MessageGroup("Group #" + i, 0, 0, 1));
    }
    var next = PinnedMessagesManager.createGroup(groups);
    assertEquals("Group #13", next.name);
    assertEquals(292, next.x);
    assertEquals(292, next.y);
    groups.remove(1);
    assertEquals("Group #2", PinnedMessagesManager.createGroup(groups).name);
  }

  @Test
  void defaultGroupUsesConfiguredCoordinatesAndIsReused() {
    var data = new PinChatConfigData();
    data.pinnedX = 42;
    data.pinnedY = 73;
    data.pinnedScale = 1.75;
    var manager = new PinnedMessagesManager(data, null);
    var group = manager.getOrCreateDefaultGroup();
    assertEquals(42, group.x);
    assertEquals(73, group.y);
    assertEquals(1.75, group.scale);
    assertSame(group, manager.getOrCreateDefaultGroup());
    assertEquals(1, data.groups.size());
  }
}
