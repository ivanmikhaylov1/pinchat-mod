package dev.sfafy.pinchat;

import dev.sfafy.pinchat.command.PinChatCommand;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PinChatMod.MOD_ID)
public class PinChatMod {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  public PinChatMod(FMLJavaModLoadingContext context) {

  }

  public void onInitializeClient(final FMLClientSetupEvent event) {
    dev.sfafy.pinchat.config.PinChatConfig.load();

    LOGGER.info("PinChatMod initialized!");
  }
}
