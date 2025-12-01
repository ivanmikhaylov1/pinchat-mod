package dev.sfafy.pinchat.platform;

import java.nio.file.Path;
import java.util.ServiceLoader;

public class PlatformHelper {
  private static IPlatformHelper instance;

  public static Path getConfigDir() {
    if (instance == null) {
      instance = ServiceLoader.load(IPlatformHelper.class)
          .findFirst()
          .orElseThrow(() -> new RuntimeException("No platform helper found!"));
    }
    return instance.getConfigDir();
  }
}
