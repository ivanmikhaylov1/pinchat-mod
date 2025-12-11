package dev.sfafy.pinchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты производительности для PinnedMessages
 */
@DisplayName("Тесты производительности PinnedMessages")
public class PinnedMessagesPerformanceTest {

    private MessageGroup testGroup;

    @BeforeEach
    void setUp() {
        PinnedMessages.groups.clear();
        testGroup = new MessageGroup("Performance Test", 0, 0, 1.0);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Проверка производительности с большим количеством сообщений")
    void testPerformanceWithManyMessages() {
        // Добавляем много сообщений
        for (int i = 0; i < 1000; i++) {
            testGroup.messages.add("Message " + i);
        }
        
        // Проверяем что операции быстрые
        assertTrue(testGroup.messages.contains("Message 500"), 
            "Должна быть возможность найти сообщение");
        assertEquals(1000, testGroup.messages.size(), 
            "Должно быть 1000 сообщений");
        
        // Проверяем производительность поиска
        long startTime = System.nanoTime();
        boolean found = testGroup.messages.contains("Message 999");
        long endTime = System.nanoTime();
        
        assertTrue(found);
        long durationMs = (endTime - startTime) / 1_000_000;
        assertTrue(durationMs < 100, 
            "Поиск должен быть быстрым (< 100ms), было: " + durationMs + "ms");
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("Проверка производительности добавления сообщений")
    void testPerformanceAddingMessages() {
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 100; i++) {
            testGroup.messages.add("Message " + i);
        }
        
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        
        assertEquals(100, testGroup.messages.size());
        assertTrue(durationMs < 50, 
            "Добавление должно быть быстрым (< 50ms), было: " + durationMs + "ms");
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("Проверка производительности удаления сообщений")
    void testPerformanceRemovingMessages() {
        // Добавляем сообщения
        for (int i = 0; i < 100; i++) {
            testGroup.messages.add("Message " + i);
        }
        
        long startTime = System.nanoTime();
        
        // Удаляем половину
        for (int i = 0; i < 50; i++) {
            testGroup.messages.remove("Message " + i);
        }
        
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        
        assertEquals(50, testGroup.messages.size());
        assertTrue(durationMs < 50, 
            "Удаление должно быть быстрым (< 50ms), было: " + durationMs + "ms");
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Проверка производительности очистки списка")
    void testPerformanceClearing() {
        // Добавляем сообщения
        for (int i = 0; i < 500; i++) {
            testGroup.messages.add("Message " + i);
        }
        
        long startTime = System.nanoTime();
        testGroup.messages.clear();
        long endTime = System.nanoTime();
        
        assertTrue(testGroup.messages.isEmpty());
        long durationMs = (endTime - startTime) / 1_000_000;
        assertTrue(durationMs < 10, 
            "Очистка должна быть очень быстрой (< 10ms), было: " + durationMs + "ms");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Проверка производительности с множественными группами")
    void testPerformanceWithMultipleGroups() {
        // Создаем несколько групп
        for (int g = 0; g < 10; g++) {
            MessageGroup group = new MessageGroup("Group " + g, 0, 0, 1.0);
            PinnedMessages.groups.add(group);
            
            // Добавляем сообщения в каждую группу
            for (int i = 0; i < 100; i++) {
                group.messages.add("Group " + g + " Message " + i);
            }
        }
        
        assertEquals(10, PinnedMessages.groups.size());
        
        // Проверяем производительность поиска
        long startTime = System.nanoTime();
        boolean found = PinnedMessages.groups.stream()
            .anyMatch(g -> g.messages.contains("Group 5 Message 50"));
        long endTime = System.nanoTime();
        
        assertTrue(found);
        long durationMs = (endTime - startTime) / 1_000_000;
        assertTrue(durationMs < 100, 
            "Поиск должен быть быстрым (< 100ms), было: " + durationMs + "ms");
    }
}

