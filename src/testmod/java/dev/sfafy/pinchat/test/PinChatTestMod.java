package dev.sfafy.pinchat.test;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PinChatTestMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pinchat-testmod");

    @Override
    public void onInitializeClient() {
        LOGGER.info("========================================");
        LOGGER.info("PinChat Test Mod инициализирован!");
        LOGGER.info("Начинается автоматическая проверка загрузки мода PinChat...");
        LOGGER.info("========================================");


        new PinChatLoadVerification().onInitializeClient();
    }
}

