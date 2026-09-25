package dev.sfafy.pinchat;

import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PinChatMod.MOD_ID)
public class PinChatMod {
  public static final Logger LOGGER = LoggerFactory.getLogger("pinchat");
  public static final String MOD_ID = "pinchat";

  public PinChatMod(IEventBus modBus) {
    if (FMLEnvironment.getDist() == Dist.CLIENT) {
      modBus.addListener(ClientSetup::onClientSetup);
      modBus.addListener(PinChatKeyBindings::registerKeyMappings);
      modBus.addListener(PinnedHudRenderer::registerOverlays);
      NeoForge.EVENT_BUS.addListener(NeoClientEvents::onKeyInput);
    }
  }
}
