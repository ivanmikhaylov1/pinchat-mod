package dev.sfafy.pinchat.test;

import dev.sfafy.pinchat.PinChatMod;
import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.integration.IntegrationManager;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import dev.sfafy.pinchat.PinnedMessages;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PinChatLoadVerification implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("pinchat-testmod");
    private static boolean allChecksPassed = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("=== Начало проверки загрузки мода PinChat ===");

        int checksPassed = 0;
        int totalChecks = 0;


        totalChecks++;
        if (FabricLoader.getInstance().isModLoaded("pinchat")) {
            LOGGER.info("✓ Проверка 1: Мод pinchat загружен в FabricLoader");
            checksPassed++;
        } else {
            LOGGER.error("✗ Проверка 1: Мод pinchat НЕ загружен в FabricLoader");
        }


        totalChecks++;
        if (PinChatMod.MOD_ID != null && PinChatMod.MOD_ID.equals("pinchat")) {
            LOGGER.info("✓ Проверка 2: MOD_ID установлен корректно");
            checksPassed++;
        } else {
            LOGGER.error("✗ Проверка 2: MOD_ID установлен некорректно");
        }


        totalChecks++;
        if (PinChatMod.LOGGER != null) {
            LOGGER.info("✓ Проверка 3: Logger инициализирован");
            checksPassed++;
        } else {
            LOGGER.error("✗ Проверка 3: Logger НЕ инициализирован");
        }


        totalChecks++;
        try {
            if (PinChatConfig.maxPinnedMessages > 0 && 
                PinChatConfig.maxLineWidth > 0 &&
                PinChatConfig.chatSensitivity > 0 &&
                PinChatConfig.pinnedScale > 0) {
                LOGGER.info("✓ Проверка 4: Конфигурация загружена и валидна");
                checksPassed++;
            } else {
                LOGGER.error("✗ Проверка 4: Конфигурация имеет невалидные значения");
            }
        } catch (Exception e) {
            LOGGER.error("✗ Проверка 4: Ошибка при проверке конфигурации: {}", e.getMessage());
        }


        totalChecks++;
        try {
            IntegrationManager.detectMods();
            LOGGER.info("✓ Проверка 5: IntegrationManager работает");
            checksPassed++;
        } catch (Exception e) {
            LOGGER.error("✗ Проверка 5: Ошибка в IntegrationManager: {}", e.getMessage());
        }


        totalChecks++;
        if (PinnedMessages.groups != null) {
            LOGGER.info("✓ Проверка 6: PinnedMessages инициализирован");
            checksPassed++;
        } else {
            LOGGER.error("✗ Проверка 6: PinnedMessages НЕ инициализирован");
        }


        totalChecks++;
        try {
            if (PinChatKeyBindings.class != null && 
                IntegrationManager.class != null) {
                LOGGER.info("✓ Проверка 7: Все классы мода доступны");
                checksPassed++;
            } else {
                LOGGER.error("✗ Проверка 7: Некоторые классы недоступны");
            }
        } catch (Exception e) {
            LOGGER.error("✗ Проверка 7: Ошибка при проверке классов: {}", e.getMessage());
        }


        LOGGER.info("=== Результаты проверки: {}/{} проверок пройдено ===", checksPassed, totalChecks);

        if (checksPassed == totalChecks) {
            allChecksPassed = true;
            LOGGER.info("✓✓✓ ВСЕ ПРОВЕРКИ ПРОЙДЕНЫ УСПЕШНО! ✓✓✓");
            LOGGER.info("Мод PinChat готов к использованию и входу в мир!");
        } else {
            LOGGER.error("✗✗✗ НЕКОТОРЫЕ ПРОВЕРКИ НЕ ПРОЙДЕНЫ ✗✗✗");
            LOGGER.error("Мод PinChat может работать некорректно!");
        }
    }

    public static boolean areAllChecksPassed() {
        return allChecksPassed;
    }
}

