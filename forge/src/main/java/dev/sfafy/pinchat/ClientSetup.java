package dev.sfafy.pinchat;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {

    dev.sfafy.pinchat.config.PinChatConfig.load();
    PinChatMod.LOGGER.info("PinChat Client Setup");
  }
}
