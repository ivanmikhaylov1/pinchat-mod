package dev.sfafy.pinchat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты загрузки мода PinChat")
public class PinChatModLoadTest {

    @Test
    @DisplayName("Проверка что MOD_ID установлен корректно")
    void testModIdIsSet() {
        assertNotNull(PinChatMod.MOD_ID, "MOD_ID не должен быть null");
        assertEquals("pinchat", PinChatMod.MOD_ID, "MOD_ID должен быть 'pinchat'");
    }

    @Test
    @DisplayName("Проверка что Logger инициализирован")
    void testLoggerIsInitialized() {
        assertNotNull(PinChatMod.LOGGER, "Logger не должен быть null");
        assertEquals("pinchat", PinChatMod.LOGGER.getName(), "Имя логгера должно быть 'pinchat'");
    }

    @Test
    @DisplayName("Проверка что мод может быть создан")
    void testModCanBeInstantiated() {
        PinChatMod mod = new PinChatMod();
        assertNotNull(mod, "Экземпляр мода должен быть создан");
    }
}

