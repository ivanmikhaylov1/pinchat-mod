package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.ConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PinChatCommon {
  public static final String MOD_ID = "pinchat";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static void init() {
    ConfigHandler.load();
    LOGGER.info("PinChat Common initialized!");
  }
}
