package dev.sfafy.pinchat.test;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тестовый мод для запуска интеграционных тестов
 * 
 * Этот мод запускается вместе с основным модом PinChat
 * и выполняет автоматическую проверку загрузки мода.
 * 
 * Использование: ./gradlew runTestClient
 * 
 * После запуска проверьте логи на наличие сообщений о проверке загрузки.
 */
public class PinChatTestMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pinchat-testmod");

    @Override
    public void onInitializeClient() {
        LOGGER.info("========================================");
        LOGGER.info("PinChat Test Mod инициализирован!");
        LOGGER.info("Начинается автоматическая проверка загрузки мода PinChat...");
        LOGGER.info("========================================");
        
        // Запускаем проверку загрузки
        new PinChatLoadVerification().onInitializeClient();
    }
}

