package dev.sfafy.pinchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты MessageGroup")
public class MessageGroupTest {

    private MessageGroup group;

    @BeforeEach
    void setUp() {
        group = new MessageGroup("Test Group", 10, 20, 1.5);
    }

    @Nested
    @DisplayName("Тесты создания группы")
    class CreationTests {

        @Test
        @DisplayName("Проверка создания группы с валидными параметрами")
        void testMessageGroupCreation() {
            assertNotNull(group, "Группа должна быть создана");
            assertEquals("Test Group", group.name, "Имя группы должно быть установлено");
            assertEquals(10, group.x, "Координата X должна быть установлена");
            assertEquals(20, group.y, "Координата Y должна быть установлена");
            assertEquals(1.5, group.scale, 0.001, "Масштаб должен быть установлен");
        }

        @ParameterizedTest
        @CsvSource({
                "Group1, 0, 0, 1.0",
                "Group2, 100, 200, 2.0",
                "Group3, -10, -20, 0.5",
                "'', 0, 0, 1.0",
                "Very Long Group Name With Spaces, 999, 999, 10.0"
        })
        @DisplayName("Проверка создания группы с различными параметрами")
        void testMessageGroupWithVariousParameters(String name, int x, int y, double scale) {
            MessageGroup testGroup = new MessageGroup(name, x, y, scale);
            assertEquals(name, testGroup.name);
            assertEquals(x, testGroup.x);
            assertEquals(y, testGroup.y);
            assertEquals(scale, testGroup.scale, 0.001);
        }

        @Test
        @DisplayName("Проверка создания группы с пустым именем")
        void testMessageGroupWithEmptyName() {
            MessageGroup emptyNameGroup = new MessageGroup("", 0, 0, 1.0);
            assertNotNull(emptyNameGroup);
            assertEquals("", emptyNameGroup.name);
        }

        @Test
        @DisplayName("Проверка создания группы с отрицательными координатами")
        void testMessageGroupWithNegativeCoordinates() {
            MessageGroup negativeGroup = new MessageGroup("Negative", -100, -200, 1.0);
            assertEquals(-100, negativeGroup.x);
            assertEquals(-200, negativeGroup.y);
        }

        @Test
        @DisplayName("Проверка создания группы с нулевым масштабом")
        void testMessageGroupWithZeroScale() {
            MessageGroup zeroScaleGroup = new MessageGroup("Zero Scale", 0, 0, 0.0);
            assertEquals(0.0, zeroScaleGroup.scale, 0.001);
        }

        @Test
        @DisplayName("Проверка создания группы с очень маленьким масштабом")
        void testMessageGroupWithVerySmallScale() {
            MessageGroup smallScaleGroup = new MessageGroup("Small Scale", 0, 0, 0.001);
            assertEquals(0.001, smallScaleGroup.scale, 0.0001);
        }
    }

    @Nested
    @DisplayName("Тесты списка сообщений")
    class MessagesListTests {

        @Test
        @DisplayName("Проверка что список сообщений инициализирован")
        void testMessagesListIsInitialized() {
            assertNotNull(group.messages, "Список сообщений должен быть инициализирован");
            assertTrue(group.messages.isEmpty(), "Список сообщений должен быть пустым при создании");
        }

        @Test
        @DisplayName("Проверка добавления сообщений")
        void testAddMessages() {
            group.messages.add("Test message 1");
            group.messages.add("Test message 2");

            assertEquals(2, group.messages.size(), "Должно быть 2 сообщения");
            assertEquals("Test message 1", group.messages.get(0));
            assertEquals("Test message 2", group.messages.get(1));
        }

        @Test
        @DisplayName("Проверка удаления сообщений")
        void testRemoveMessages() {
            group.messages.add("Message to remove");
            group.messages.add("Message to keep");

            group.messages.remove("Message to remove");

            assertEquals(1, group.messages.size());
            assertEquals("Message to keep", group.messages.get(0));
        }

        @Test
        @DisplayName("Проверка удаления сообщения по индексу")
        void testRemoveMessageByIndex() {
            group.messages.add("First");
            group.messages.add("Second");
            group.messages.add("Third");

            group.messages.remove(1);

            assertEquals(2, group.messages.size());
            assertEquals("First", group.messages.get(0));
            assertEquals("Third", group.messages.get(1));
        }

        @Test
        @DisplayName("Проверка очистки списка сообщений")
        void testClearMessages() {
            group.messages.add("Message 1");
            group.messages.add("Message 2");
            group.messages.add("Message 3");

            group.messages.clear();

            assertTrue(group.messages.isEmpty());
        }

        @Test
        @DisplayName("Проверка добавления большого количества сообщений")
        void testAddManyMessages() {
            for (int i = 0; i < 100; i++) {
                group.messages.add("Message " + i);
            }
            assertEquals(100, group.messages.size());
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "   ", "\n", "\t", "Normal message", "Сообщение на русском",
                "🎯 Emoji message" })
        @DisplayName("Проверка добавления различных типов сообщений")
        void testAddVariousMessageTypes(String message) {
            group.messages.add(message);
            assertTrue(group.messages.contains(message));
        }

        @Test
        @DisplayName("Проверка проверки наличия сообщения")
        void testContainsMessage() {
            group.messages.add("Searchable message");

            assertTrue(group.messages.contains("Searchable message"));
            assertFalse(group.messages.contains("Non-existent message"));
        }
    }

    @Nested
    @DisplayName("Тесты состояния isCollapsed")
    class CollapsedStateTests {

        @Test
        @DisplayName("Проверка значения isCollapsed по умолчанию")
        void testIsCollapsedDefault() {
            assertFalse(group.isCollapsed, "isCollapsed должен быть false по умолчанию");
        }

        @Test
        @DisplayName("Проверка установки isCollapsed в true")
        void testSetIsCollapsedTrue() {
            group.isCollapsed = true;
            assertTrue(group.isCollapsed);
        }

        @Test
        @DisplayName("Проверка переключения isCollapsed")
        void testToggleIsCollapsed() {
            assertFalse(group.isCollapsed);

            group.isCollapsed = true;
            assertTrue(group.isCollapsed);

            group.isCollapsed = false;
            assertFalse(group.isCollapsed);
        }
    }

    @Nested
    @DisplayName("Тесты изменения свойств группы")
    class PropertyModificationTests {

        @Test
        @DisplayName("Проверка изменения имени группы")
        void testModifyGroupName() {
            group.name = "New Name";
            assertEquals("New Name", group.name);
        }

        @Test
        @DisplayName("Проверка изменения координат")
        void testModifyCoordinates() {
            group.x = 100;
            group.y = 200;

            assertEquals(100, group.x);
            assertEquals(200, group.y);
        }

        @Test
        @DisplayName("Проверка изменения масштаба")
        void testModifyScale() {
            group.scale = 2.5;
            assertEquals(2.5, group.scale, 0.001);
        }

        @Test
        @DisplayName("Проверка множественных изменений")
        void testMultipleModifications() {
            group.name = "Modified Group";
            group.x = 50;
            group.y = 75;
            group.scale = 1.25;
            group.isCollapsed = true;
            group.messages.add("New message");

            assertEquals("Modified Group", group.name);
            assertEquals(50, group.x);
            assertEquals(75, group.y);
            assertEquals(1.25, group.scale, 0.001);
            assertTrue(group.isCollapsed);
            assertEquals(1, group.messages.size());
        }
    }
}
