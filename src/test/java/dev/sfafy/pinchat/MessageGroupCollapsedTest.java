package dev.sfafy.pinchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты состояния isCollapsed MessageGroup")
public class MessageGroupCollapsedTest {

    private MessageGroup group;

    @BeforeEach
    void setUp() {
        group = new MessageGroup("Test Group", 0, 0, 1.0);
        group.messages.add("Message 1");
        group.messages.add("Message 2");
        group.messages.add("Message 3");
    }

    @Test
    @DisplayName("Проверка значения isCollapsed по умолчанию")
    void testIsCollapsedDefault() {
        assertFalse(group.isCollapsed, 
            "isCollapsed должен быть false по умолчанию");
    }

    @Test
    @DisplayName("Проверка установки isCollapsed в true")
    void testSetIsCollapsedTrue() {
        group.isCollapsed = true;
        assertTrue(group.isCollapsed, 
            "isCollapsed должен быть установлен в true");


        assertFalse(group.messages.isEmpty(), 
            "Сообщения не должны быть удалены при сворачивании");
        assertEquals(3, group.messages.size());
    }

    @Test
    @DisplayName("Проверка переключения isCollapsed")
    void testToggleIsCollapsed() {
        assertFalse(group.isCollapsed);

        group.isCollapsed = true;
        assertTrue(group.isCollapsed);

        group.isCollapsed = false;
        assertFalse(group.isCollapsed);


        assertEquals(3, group.messages.size());
    }

    @Test
    @DisplayName("Проверка что сворачивание не влияет на сообщения")
    void testCollapsedDoesNotAffectMessages() {
        int originalSize = group.messages.size();

        group.isCollapsed = true;
        assertEquals(originalSize, group.messages.size(), 
            "Количество сообщений не должно измениться");

        group.isCollapsed = false;
        assertEquals(originalSize, group.messages.size(), 
            "Количество сообщений не должно измениться");
    }

    @Test
    @DisplayName("Проверка работы с пустой группой")
    void testCollapsedWithEmptyGroup() {
        MessageGroup emptyGroup = new MessageGroup("Empty", 0, 0, 1.0);
        assertTrue(emptyGroup.messages.isEmpty());
        assertFalse(emptyGroup.isCollapsed);

        emptyGroup.isCollapsed = true;
        assertTrue(emptyGroup.isCollapsed);
        assertTrue(emptyGroup.messages.isEmpty());
    }

    @Test
    @DisplayName("Проверка что можно добавлять сообщения в свернутую группу")
    void testAddMessagesToCollapsedGroup() {
        group.isCollapsed = true;
        assertTrue(group.isCollapsed);

        group.messages.add("Message 4");
        assertEquals(4, group.messages.size());
        assertTrue(group.isCollapsed, 
            "Группа должна остаться свернутой");
    }
}

