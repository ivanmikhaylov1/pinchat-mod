package dev.sfafy.pinchat.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для PinChatConfig
 * 
 * Примечание: Тесты конфигурации ограничены, так как PinChatConfig требует FabricLoader
 * для инициализации, который недоступен в unit тестах.
 * Полное тестирование требует интеграционных тестов с запуском Minecraft клиента.
 */
@DisplayName("Тесты PinChatConfig")
public class PinChatConfigTest {

    @Test
    @DisplayName("Проверка что класс PinChatConfig существует")
    void testPinChatConfigClassExists() {
        assertNotNull(PinChatConfig.class, "Класс PinChatConfig должен существовать");
    }

    @Test
    @DisplayName("Проверка что методы save и load существуют")
    void testSaveAndLoadMethodsExist() {
        // Проверяем что методы существуют через рефлексию
        try {
            PinChatConfig.class.getDeclaredMethod("save");
            PinChatConfig.class.getDeclaredMethod("load");
            assertTrue(true, "Методы save и load должны существовать");
        } catch (NoSuchMethodException e) {
            fail("Методы save и load должны существовать");
        }
    }

    @Test
    @DisplayName("Проверка что поля конфигурации существуют")
    void testConfigFieldsExist() {
        // Проверяем что поля существуют через рефлексию
        try {
            PinChatConfig.class.getField("maxPinnedMessages");
            PinChatConfig.class.getField("maxLineWidth");
            PinChatConfig.class.getField("chatSensitivity");
            PinChatConfig.class.getField("pinnedX");
            PinChatConfig.class.getField("pinnedY");
            PinChatConfig.class.getField("pinnedScale");
            assertTrue(true, "Все поля конфигурации должны существовать");
        } catch (NoSuchFieldException e) {
            fail("Поля конфигурации должны существовать: " + e.getMessage());
        }
    }
}

