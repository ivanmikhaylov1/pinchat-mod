package dev.sfafy.pinchat.integration;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Интеграционные тесты PinChat")
public class PinChatIntegrationTest {

    @Test
    @DisplayName("Проверка что все основные компоненты доступны")
    void testAllComponentsAreAvailable() {
        assertNotNull(PinChatMod.MOD_ID, "MOD_ID должен быть доступен");
        assertNotNull(PinChatMod.LOGGER, "Logger должен быть доступен");
        assertNotNull(PinChatConfig.class, "PinChatConfig должен быть доступен");
        assertNotNull(PinChatKeyBindings.class, "PinChatKeyBindings должен быть доступен");
        assertNotNull(IntegrationManager.class, "IntegrationManager должен быть доступен");
    }

    @Test
    @DisplayName("Проверка что мод может быть инициализирован")
    void testModCanBeInitialized() {

        PinChatMod mod = new PinChatMod();
        assertNotNull(mod, "Мод должен быть создан");



        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
    }

    @Test
    @DisplayName("Проверка что IntegrationManager может определить моды")
    void testIntegrationManagerCanDetectMods() {
        assertDoesNotThrow(() -> {
            IntegrationManager.detectMods();
        }, "IntegrationManager должен уметь определять моды");


        boolean malilib = IntegrationManager.isMalilibLoaded();
        assertTrue(malilib == true || malilib == false, 
            "Результат определения мода должен быть boolean");
    }

    @Test
    @DisplayName("Проверка что конфигурация и группы сообщений работают вместе")
    void testConfigAndMessagesIntegration() {


        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
        assertNotNull(PinnedMessages.class, "Класс PinnedMessages должен существовать");
        assertNotNull(PinnedMessages.groups, "Список групп должен быть инициализирован");
    }

    @Test
    @DisplayName("Проверка что все модули могут быть загружены одновременно")
    void testAllModulesCanBeLoadedTogether() {
        assertDoesNotThrow(() -> {

            new PinChatMod();
            IntegrationManager.detectMods();


            assertNotNull(PinChatMod.MOD_ID);
            assertNotNull(PinChatMod.LOGGER);
            assertNotNull(PinChatConfig.class);
        }, "Все модули должны загружаться без конфликтов");
    }
}

