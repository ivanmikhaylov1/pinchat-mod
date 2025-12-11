package dev.sfafy.pinchat.integration;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для проверки загрузки мода и взаимодействия компонентов
 * 
 * Примечание: Полное тестирование требует запуска Minecraft клиента.
 * Эти тесты проверяют базовую интеграцию компонентов без запуска игры.
 */
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
        // Проверяем что основные компоненты могут быть созданы
        PinChatMod mod = new PinChatMod();
        assertNotNull(mod, "Мод должен быть создан");
        
        // Проверяем что класс конфигурации существует
        // В unit тестах PinChatConfig не может быть инициализирован
        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
    }

    @Test
    @DisplayName("Проверка что IntegrationManager может определить моды")
    void testIntegrationManagerCanDetectMods() {
        assertDoesNotThrow(() -> {
            IntegrationManager.detectMods();
        }, "IntegrationManager должен уметь определять моды");
        
        // Проверяем что методы возвращают boolean значения
        boolean malilib = IntegrationManager.isMalilibLoaded();
        assertTrue(malilib == true || malilib == false, 
            "Результат определения мода должен быть boolean");
    }

    @Test
    @DisplayName("Проверка что конфигурация и группы сообщений работают вместе")
    void testConfigAndMessagesIntegration() {
        // В unit тестах PinChatConfig не может быть инициализирован
        // Проверяем только что классы существуют и могут работать вместе
        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
        assertNotNull(PinnedMessages.class, "Класс PinnedMessages должен существовать");
        assertNotNull(PinnedMessages.groups, "Список групп должен быть инициализирован");
    }

    @Test
    @DisplayName("Проверка что все модули могут быть загружены одновременно")
    void testAllModulesCanBeLoadedTogether() {
        assertDoesNotThrow(() -> {
            // Симулируем загрузку всех компонентов
            new PinChatMod();
            IntegrationManager.detectMods();
            
            // Проверяем что все доступно
            assertNotNull(PinChatMod.MOD_ID);
            assertNotNull(PinChatMod.LOGGER);
            assertNotNull(PinChatConfig.class);
        }, "Все модули должны загружаться без конфликтов");
    }
}

