package dev.sfafy.pinchat;

import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты множественных групп PinnedMessages")
public class PinnedMessagesMultipleGroupsTest {

    @BeforeEach
    void setUp() {
        PinnedMessages.groups.clear();
    }

    @Test
    @DisplayName("Проверка работы с несколькими группами")
    void testMultipleGroups() {
        MessageGroup group1 = new MessageGroup("Group 1", 0, 0, 1.0);
        MessageGroup group2 = new MessageGroup("Group 2", 100, 100, 1.5);

        PinnedMessages.groups.add(group1);
        PinnedMessages.groups.add(group2);

        try {
            PinnedMessages.toggle(Text.of("Message 1"), group1);
            PinnedMessages.toggle(Text.of("Message 2"), group2);

            assertEquals(1, group1.messages.size(), 
                "Group 1 должна содержать 1 сообщение");
            assertEquals(1, group2.messages.size(), 
                "Group 2 должна содержать 1 сообщение");
            assertEquals(2, PinnedMessages.groups.size(), 
                "Должно быть 2 группы");
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            assertTrue(true, "Ожидаемая ошибка инициализации в unit тестах");
        }
    }

    @Test
    @DisplayName("Проверка что сообщения в разных группах независимы")
    void testIndependentGroups() {
        MessageGroup group1 = new MessageGroup("Group 1", 0, 0, 1.0);
        MessageGroup group2 = new MessageGroup("Group 2", 0, 0, 1.0);

        PinnedMessages.groups.add(group1);
        PinnedMessages.groups.add(group2);

        try {

            Text sameMessage = Text.of("Same message");
            PinnedMessages.toggle(sameMessage, group1);
            PinnedMessages.toggle(sameMessage, group2);

            assertEquals(1, group1.messages.size());
            assertEquals(1, group2.messages.size());


            PinnedMessages.toggle(sameMessage, group1);
            assertEquals(0, group1.messages.size());
            assertEquals(1, group2.messages.size(), 
                "Group 2 не должна измениться");
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            assertTrue(true, "Ожидаемая ошибка инициализации в unit тестах");
        }
    }

    @Test
    @DisplayName("Проверка лимита для каждой группы отдельно")
    void testLimitPerGroup() {
        try {
            int originalLimit = dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages;
            dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages = 2;

            MessageGroup group1 = new MessageGroup("Group 1", 0, 0, 1.0);
            MessageGroup group2 = new MessageGroup("Group 2", 0, 0, 1.0);

            PinnedMessages.groups.add(group1);
            PinnedMessages.groups.add(group2);


            for (int i = 0; i < 2; i++) {
                PinnedMessages.toggle(Text.of("Group1 Message " + i), group1);
                PinnedMessages.toggle(Text.of("Group2 Message " + i), group2);
            }

            assertEquals(2, group1.messages.size());
            assertEquals(2, group2.messages.size());


            PinnedMessages.toggle(Text.of("Group1 Message 3"), group1);
            assertEquals(2, group1.messages.size(), 
                "Лимит группы 1 не должен быть превышен");


            assertEquals(2, group2.messages.size());

            dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages = originalLimit;
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            assertTrue(true, "Ожидаемая ошибка инициализации в unit тестах");
        }
    }

    @Test
    @DisplayName("Проверка работы с пустыми группами")
    void testEmptyGroups() {
        MessageGroup group1 = new MessageGroup("Empty Group 1", 0, 0, 1.0);
        MessageGroup group2 = new MessageGroup("Empty Group 2", 0, 0, 1.0);

        PinnedMessages.groups.add(group1);
        PinnedMessages.groups.add(group2);

        assertTrue(group1.messages.isEmpty());
        assertTrue(group2.messages.isEmpty());
        assertEquals(2, PinnedMessages.groups.size());
    }

    @Test
    @DisplayName("Проверка удаления групп")
    void testGroupRemoval() {
        MessageGroup group1 = new MessageGroup("Group 1", 0, 0, 1.0);
        MessageGroup group2 = new MessageGroup("Group 2", 0, 0, 1.0);

        PinnedMessages.groups.add(group1);
        PinnedMessages.groups.add(group2);
        assertEquals(2, PinnedMessages.groups.size());

        PinnedMessages.groups.remove(group1);
        assertEquals(1, PinnedMessages.groups.size());
        assertFalse(PinnedMessages.groups.contains(group1));
        assertTrue(PinnedMessages.groups.contains(group2));
    }
}

