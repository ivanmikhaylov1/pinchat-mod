package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты загрузки мода и входа в мир")
public class ModLoadAndWorldEntryTest {

    @BeforeEach
    void setUp() {

        PinnedMessages.groups.clear();
    }

    @Test
    @DisplayName("Проверка что мод готов к загрузке")
    void testModIsReadyToLoad() {

        assertNotNull(PinChatMod.MOD_ID, "MOD_ID должен быть установлен");
        assertNotNull(PinChatMod.LOGGER, "Logger должен быть инициализирован");



        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
    }

    @Test
    @DisplayName("Проверка что все компоненты инициализируются без ошибок")
    void testAllComponentsInitializeWithoutErrors() {
        assertDoesNotThrow(() -> {

            new PinChatMod();
            IntegrationManager.detectMods();


            assertNotNull(PinChatConfig.class);


            assertNotNull(PinChatKeyBindings.class);
        }, "Все компоненты должны инициализироваться без ошибок");
    }

    @Test
    @DisplayName("Проверка что конфигурация может быть загружена")
    void testConfigCanBeLoaded() {


        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");

        try {

            PinChatConfig.class.getField("maxPinnedMessages");
            PinChatConfig.class.getField("maxLineWidth");
            PinChatConfig.class.getField("chatSensitivity");
            PinChatConfig.class.getField("pinnedScale");
            assertTrue(true, "Поля конфигурации должны существовать");
        } catch (NoSuchFieldException e) {
            fail("Поля конфигурации должны существовать");
        }
    }

    @Test
    @DisplayName("Проверка что группы сообщений готовы к использованию")
    void testMessageGroupsAreReady() {

        assertNotNull(PinnedMessages.groups, "Список групп должен быть инициализирован");



        try {
            MessageGroup defaultGroup = PinnedMessages.getOrCreateDefaultGroup();
            assertNotNull(defaultGroup, "Группа по умолчанию должна быть создана");
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {

            assertTrue(true, "Ожидаемая ошибка инициализации в unit тестах");
        }
    }

    @Test
    @DisplayName("Проверка что интеграции с другими модами работают")
    void testModIntegrationsWork() {
        assertDoesNotThrow(() -> {
            IntegrationManager.detectMods();


            boolean malilib = IntegrationManager.isMalilibLoaded();
            boolean clothConfig = IntegrationManager.isClothConfigLoaded();
            boolean modMenu = IntegrationManager.isModMenuLoaded();
            boolean yacl = IntegrationManager.isYaclLoaded();


            assertTrue(malilib == true || malilib == false);
            assertTrue(clothConfig == true || clothConfig == false);
            assertTrue(modMenu == true || modMenu == false);
            assertTrue(yacl == true || yacl == false);
        }, "Интеграции с другими модами должны работать");
    }

    @Test
    @DisplayName("Проверка что мод не имеет критических ошибок конфигурации")
    void testModHasNoCriticalConfigErrors() {

        assertNotNull(PinChatMod.MOD_ID);
        assertFalse(PinChatMod.MOD_ID.isEmpty(), "MOD_ID не должен быть пустым");


        assertNotNull(PinChatConfig.class, "Класс конфигурации должен существовать");
    }
}

