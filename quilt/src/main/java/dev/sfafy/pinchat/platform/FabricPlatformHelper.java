package dev.sfafy.pinchat.platform;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
  @Override
  public Path getConfigDir() {
    return FabricLoader.getInstance().getConfigDir();
  }
}
