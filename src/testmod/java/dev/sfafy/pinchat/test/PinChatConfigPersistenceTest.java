package dev.sfafy.pinchat.test;

import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import net.fabricmc.loader.api.FabricLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Тесты сохранения и загрузки конфигурации")
public class PinChatConfigPersistenceTest {

    @BeforeEach
    void setUp() {

        assertNotNull(FabricLoader.getInstance(), 
            "FabricLoader должен быть доступен");
    }

    @Test
    @DisplayName("Проверка что конфигурация может быть сохранена и загружена")
    void testConfigSaveAndLoad() {
        assertDoesNotThrow(() -> {

            int originalMax = PinChatConfig.maxPinnedMessages;
            int originalX = PinChatConfig.pinnedX;
            int originalY = PinChatConfig.pinnedY;
            double originalScale = PinChatConfig.pinnedScale;


            PinChatConfig.maxPinnedMessages = 10;
            PinChatConfig.pinnedX = 50;
            PinChatConfig.pinnedY = 100;
            PinChatConfig.pinnedScale = 1.5;


            PinChatConfig.save();


            PinChatConfig.maxPinnedMessages = 5;
            PinChatConfig.pinnedX = 10;
            PinChatConfig.pinnedY = 10;
            PinChatConfig.pinnedScale = 1.0;


            PinChatConfig.load();


            assertEquals(10, PinChatConfig.maxPinnedMessages, 
                "maxPinnedMessages должен быть восстановлен");
            assertEquals(50, PinChatConfig.pinnedX, 
                "pinnedX должен быть восстановлен");
            assertEquals(100, PinChatConfig.pinnedY, 
                "pinnedY должен быть восстановлен");
            assertEquals(1.5, PinChatConfig.pinnedScale, 0.001, 
                "pinnedScale должен быть восстановлен");


            PinChatConfig.maxPinnedMessages = originalMax;
            PinChatConfig.pinnedX = originalX;
            PinChatConfig.pinnedY = originalY;
            PinChatConfig.pinnedScale = originalScale;
            PinChatConfig.save();
        }, "Конфигурация должна сохраняться и загружаться без ошибок");
    }

    @Test
    @DisplayName("Проверка что группы сообщений сохраняются и загружаются")
    void testGroupsSaveAndLoad() {
        assertDoesNotThrow(() -> {

            PinnedMessages.groups.clear();


            MessageGroup group1 = new MessageGroup("Test Group 1", 10, 20, 1.0);
            group1.messages.add("Message 1");
            group1.messages.add("Message 2");

            MessageGroup group2 = new MessageGroup("Test Group 2", 100, 200, 1.5);
            group2.messages.add("Message 3");
            group2.isCollapsed = true;

            PinnedMessages.groups.add(group1);
            PinnedMessages.groups.add(group2);


            PinChatConfig.save();

            int groupsCountBefore = PinnedMessages.groups.size();


            PinnedMessages.groups.clear();
            PinChatConfig.load();


            assertFalse(PinnedMessages.groups.isEmpty(), 
                "Группы должны быть загружены");
            assertEquals(groupsCountBefore, PinnedMessages.groups.size(), 
                "Количество групп должно совпадать");


            MessageGroup loadedGroup1 = PinnedMessages.groups.stream()
                .filter(g -> g.name.equals("Test Group 1"))
                .findFirst()
                .orElse(null);

            assertNotNull(loadedGroup1, "Первая группа должна быть загружена");
            assertEquals(10, loadedGroup1.x);
            assertEquals(20, loadedGroup1.y);
            assertEquals(1.0, loadedGroup1.scale, 0.001);
            assertEquals(2, loadedGroup1.messages.size());


            MessageGroup loadedGroup2 = PinnedMessages.groups.stream()
                .filter(g -> g.name.equals("Test Group 2"))
                .findFirst()
                .orElse(null);

            assertNotNull(loadedGroup2, "Вторая группа должна быть загружена");
            assertTrue(loadedGroup2.isCollapsed, 
                "Состояние isCollapsed должно быть сохранено");
        }, "Группы должны сохраняться и загружаться корректно");
    }

    @Test
    @DisplayName("Проверка валидации значений конфигурации при загрузке")
    void testConfigValidationOnLoad() {
        assertDoesNotThrow(() -> {

            PinChatConfig.load();


            assertTrue(PinChatConfig.maxPinnedMessages > 0, 
                "maxPinnedMessages должен быть положительным");
            assertTrue(PinChatConfig.maxLineWidth > 0, 
                "maxLineWidth должен быть положительным");
            assertTrue(PinChatConfig.chatSensitivity > 0, 
                "chatSensitivity должен быть положительным");
            assertTrue(PinChatConfig.pinnedScale > 0, 
                "pinnedScale должен быть положительным");
        }, "Конфигурация должна иметь валидные значения после загрузки");
    }
}

