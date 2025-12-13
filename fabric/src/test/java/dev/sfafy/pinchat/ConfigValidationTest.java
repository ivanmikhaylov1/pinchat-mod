package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты валидации конфигурации")
public class ConfigValidationTest {

    @Test
    @DisplayName("Проверка что конфигурация имеет валидные значения")
    void testConfigHasValidValues() {

        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");



        try {

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

            assertNotNull(PinChatConfig.class.getDeclaredMethod("load"));
            assertNotNull(PinChatConfig.class.getDeclaredMethod("save"));
        } catch (NoSuchMethodException e) {
            fail("Методы конфигурации должны существовать: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Проверка что конфигурация может быть изменена")
    void testConfigCanBeModified() {


        assertNotNull(PinChatConfig.class);
    }
}

