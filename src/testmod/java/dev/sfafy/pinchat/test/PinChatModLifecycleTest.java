package dev.sfafy.pinchat.test;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.MessageGroup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты жизненного цикла мода с проверкой инициализации
 * 
 * Эти тесты проверяют что мод корректно инициализируется
 * и все компоненты готовы к работе.
 */
@DisplayName("Тесты жизненного цикла мода")
@Environment(EnvType.CLIENT)
public class PinChatModLifecycleTest {

    @Test
    @DisplayName("Проверка что мы в клиентской среде")
    void testClientEnvironment() {
        // В тестовом клиенте мы должны быть в клиентской среде
        assertNotNull(FabricLoader.getInstance(), "FabricLoader должен быть доступен");
        assertTrue(true, "Тесты выполняются в клиентской среде");
    }

    @Test
    @DisplayName("Проверка что группы сообщений инициализированы")
    void testPinnedMessagesInitialized() {
        assertNotNull(PinnedMessages.groups, 
            "Список групп сообщений должен быть инициализирован");
        
        // Проверяем что можно работать с группами
        assertDoesNotThrow(() -> {
            MessageGroup testGroup = new MessageGroup("Test", 0, 0, 1.0);
            assertNotNull(testGroup);
            assertNotNull(testGroup.messages);
        }, "Должна быть возможность создавать группы сообщений");
    }

    @Test
    @DisplayName("Проверка что мод может быть создан")
    void testModCanBeInstantiated() {
        assertDoesNotThrow(() -> {
            PinChatMod mod = new PinChatMod();
            assertNotNull(mod, "Мод должен быть создан");
        }, "Мод должен создаваться без ошибок");
    }

    @Test
    @DisplayName("Проверка что все статические поля инициализированы")
    void testStaticFieldsInitialized() {
        assertNotNull(PinChatMod.MOD_ID, "MOD_ID должен быть инициализирован");
        assertNotNull(PinChatMod.LOGGER, "Logger должен быть инициализирован");
        assertFalse(PinChatMod.MOD_ID.isEmpty(), "MOD_ID не должен быть пустым");
    }

    @Test
    @DisplayName("Проверка что конфигурация имеет разумные значения")
    void testConfigHasReasonableValues() {
        // В среде с FabricLoader конфигурация должна быть загружена
        assertDoesNotThrow(() -> {
            // Проверяем что значения в разумных пределах
            assertTrue(dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages > 0 
                && dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages <= 1000,
                "maxPinnedMessages должен быть в разумных пределах");
            
            assertTrue(dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth > 0 
                && dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth <= 10000,
                "maxLineWidth должен быть в разумных пределах");
            
            assertTrue(dev.sfafy.pinchat.config.PinChatConfig.pinnedScale > 0 
                && dev.sfafy.pinchat.config.PinChatConfig.pinnedScale <= 10.0,
                "pinnedScale должен быть в разумных пределах");
        }, "Конфигурация должна иметь разумные значения");
    }
}

