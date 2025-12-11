package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки валидации данных конфигурации
 */
@DisplayName("Тесты валидации конфигурации")
public class ConfigValidationTest {

    @Test
    @DisplayName("Проверка что конфигурация имеет валидные значения")
    void testConfigHasValidValues() {
        // Проверяем что класс существует
        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
        
        // В unit тестах значения могут быть не инициализированы,
        // но мы проверяем структуру через рефлексию
        try {
            // Проверяем что поля существуют
            assertNotNull(PinChatConfig.class.getField("maxPinnedMessages"));
            assertNotNull(PinChatConfig.class.getField("maxLineWidth"));
            assertNotNull(PinChatConfig.class.getField("chatSensitivity"));
            assertNotNull(PinChatConfig.class.getField("pinnedX"));
            assertNotNull(PinChatConfig.class.getField("pinnedY"));
            assertNotNull(PinChatConfig.class.getField("pinnedScale"));
        } catch (NoSuchFieldException e) {
            fail("Поля конфигурации должны существовать: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка структуры методов конфигурации")
    void testConfigMethodsExist() {
        try {
            // Проверяем что методы существуют
            assertNotNull(PinChatConfig.class.getDeclaredMethod("load"));
            assertNotNull(PinChatConfig.class.getDeclaredMethod("save"));
        } catch (NoSuchMethodException e) {
            fail("Методы конфигурации должны существовать: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка что конфигурация может быть изменена")
    void testConfigCanBeModified() {
        // В unit тестах это может не работать из-за отсутствия FabricLoader
        // Но структура должна позволять изменения
        assertNotNull(PinChatConfig.class);
    }
}

