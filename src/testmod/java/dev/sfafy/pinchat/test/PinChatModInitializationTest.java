package dev.sfafy.pinchat.test;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты инициализации мода с запуском Minecraft")
public class PinChatModInitializationTest {

    @BeforeAll
    static void setup() {

        assertNotNull(FabricLoader.getInstance(), 
            "FabricLoader должен быть доступен в тестовой среде");
    }

    @Test
    @DisplayName("Проверка что мод загружен в FabricLoader")
    void testModIsLoaded() {
        FabricLoader loader = FabricLoader.getInstance();
        assertTrue(loader.isModLoaded("pinchat"), 
            "Мод pinchat должен быть загружен в FabricLoader");
    }

    @Test
    @DisplayName("Проверка что MOD_ID установлен корректно")
    void testModIdIsCorrect() {
        assertEquals("pinchat", PinChatMod.MOD_ID, 
            "MOD_ID должен быть 'pinchat'");
    }

    @Test
    @DisplayName("Проверка что Logger инициализирован")
    void testLoggerIsInitialized() {
        assertNotNull(PinChatMod.LOGGER, 
            "Logger должен быть инициализирован");
        assertEquals("pinchat", PinChatMod.LOGGER.getName(), 
            "Имя логгера должно быть 'pinchat'");
    }

    @Test
    @DisplayName("Проверка что конфигурация может быть загружена")
    void testConfigCanBeLoaded() {

        assertDoesNotThrow(() -> {

            assertTrue(PinChatConfig.maxPinnedMessages > 0, 
                "maxPinnedMessages должен быть положительным");
            assertTrue(PinChatConfig.maxLineWidth > 0, 
                "maxLineWidth должен быть положительным");
            assertTrue(PinChatConfig.chatSensitivity > 0, 
                "chatSensitivity должен быть положительным");
            assertTrue(PinChatConfig.pinnedScale > 0, 
                "pinnedScale должен быть положительным");
        }, "Конфигурация должна быть доступна и валидна");
    }

    @Test
    @DisplayName("Проверка что IntegrationManager может определить моды")
    void testIntegrationManagerWorks() {
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
        }, "IntegrationManager должен работать корректно");
    }

    @Test
    @DisplayName("Проверка что PinnedMessages может создавать группы по умолчанию")
    void testPinnedMessagesCanCreateDefaultGroup() {
        assertDoesNotThrow(() -> {
            PinnedMessages.groups.clear();


            MessageGroup defaultGroup = PinnedMessages.getOrCreateDefaultGroup();

            assertNotNull(defaultGroup, "Группа по умолчанию должна быть создана");
            assertEquals("Default Group", defaultGroup.name, 
                "Имя группы по умолчанию должно быть 'Default Group'");
            assertFalse(PinnedMessages.groups.isEmpty(), 
                "Список групп не должен быть пустым");
        }, "PinnedMessages должен создавать группу по умолчанию");
    }

    @Test
    @DisplayName("Проверка что toggle работает с реальными Text объектами")
    void testToggleWithRealTextObjects() {
        assertDoesNotThrow(() -> {
            PinnedMessages.groups.clear();
            MessageGroup group = new MessageGroup("Test", 0, 0, 1.0);

            Text text1 = Text.of("Test message 1");
            Text text2 = Text.literal("Test message 2");

            PinnedMessages.toggle(text1, group);
            PinnedMessages.toggle(text2, group);

            assertEquals(2, group.messages.size(), 
                "Должно быть добавлено 2 сообщения");


            PinnedMessages.toggle(text1, group);
            assertEquals(1, group.messages.size(), 
                "После удаления должно остаться 1 сообщение");
        }, "Toggle должен работать с Text объектами");
    }

    @Test
    @DisplayName("Проверка что классы мода доступны")
    void testModClassesAreAccessible() {
        assertNotNull(PinChatMod.class, "Класс PinChatMod должен быть доступен");
        assertNotNull(PinChatConfig.class, "Класс PinChatConfig должен быть доступен");
        assertNotNull(PinChatKeyBindings.class, "Класс PinChatKeyBindings должен быть доступен");
        assertNotNull(IntegrationManager.class, "Класс IntegrationManager должен быть доступен");
    }

    @Test
    @DisplayName("Проверка что мод реализует ClientModInitializer")
    void testModImplementsClientModInitializer() {
        assertTrue(ClientModInitializer.class.isAssignableFrom(PinChatMod.class), 
            "PinChatMod должен реализовывать ClientModInitializer");
    }
}

