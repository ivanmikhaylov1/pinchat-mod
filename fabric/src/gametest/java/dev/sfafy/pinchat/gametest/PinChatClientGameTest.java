package dev.sfafy.pinchat.gametest;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.PinnedMessages;
import dev.sfafy.pinchat.config.PinChatConfig;
import dev.sfafy.pinchat.gui.GroupRenameScreen;
import dev.sfafy.pinchat.gui.MoveableChatScreen;
import dev.sfafy.pinchat.gui.PinChatConfigScreen;
import dev.sfafy.pinchat.keybindings.PinChatKeyBindings;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/** Runs through real screens and mixins in an isolated singleplayer world. */
public final class PinChatClientGameTest implements FabricClientGameTest {
  private static final String MESSAGE = "Base: X=120 Y=64 Z=-80";

  @Override
  public void runTest(ClientGameTestContext context) {
    // Keep software rendering and chunk generation within the CI runner budget.
    context.runOnClient(client -> {
      client.options.getViewDistance().setValue(2);
      client.options.getSimulationDistance().setValue(5);
      client.options.getMaxFps().setValue(30);
    });
    try (var world = context.worldBuilder().create()) {
      try {
        runScenario(context);
        context.runOnClient(client -> dev.sfafy.pinchat.PinChatMod.LOGGER.info(
            "PinChat in-world interactions passed"));
      } catch (Throwable failure) {
        // Capture the failing screen before the world is closed.
        context.takeScreenshot("pinchat-failure");
        throw failure;
      }
    }
  }

  private void runScenario(ClientGameTestContext context) {
    context.waitFor(client -> client.player != null && client.world != null);
    context.runOnClient(client -> {
      PinnedMessages.groups.clear();
      PinChatConfig.pinnedX = 100;
      PinChatConfig.pinnedY = 100;
      PinChatConfig.pinnedScale = 1.0;
      PinChatConfig.maxPinnedMessages = 5;
      PinChatConfig.moveableChatEnabled = true;
      check(PinChatKeyBindings.openMoveableChatKey != null, "U binding was not registered");
      client.inGameHud.getChatHud().clear(false);
      client.inGameHud.getChatHud().addMessage(Text.literal(MESSAGE));
    });
    context.setScreen(() -> new ChatScreen("", false));
    context.waitTick();

    // A real right-click must reach ChatScreenMixin, not a direct model call.
    clickChatLine(context);
    context.runOnClient(client -> check(
        PinnedMessages.groups.size() == 1 && defaultGroup().messages.equals(List.of(MESSAGE)),
        "Right-click did not pin the complete chat message"));
    clickChatLine(context);
    context.runOnClient(client -> check(defaultGroup().messages.isEmpty(), "Second click did not unpin"));
    clickChatLine(context);

    context.getInput().holdShift();
    try {
      clickChatLine(context);
    } finally {
      context.getInput().releaseShift();
    }
    context.runOnClient(client -> {
      check(PinnedMessages.groups.size() == 2, "Shift-click did not create a separate group");
      check(PinnedMessages.groups.get(1).messages.equals(List.of(MESSAGE)), "New group lost its message");
      check(defaultGroup().messages.equals(List.of(MESSAGE)), "New group modified the default group");
      // Keep groups apart so subsequent clicks have a single target.
      PinnedMessages.groups.get(1).x = 250;
      PinnedMessages.groups.get(1).y = 150;
    });

    click(context, 104, 94, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    context.runOnClient(client -> check(defaultGroup().isCollapsed, "Header click did not collapse"));
    click(context, 104, 94, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    context.runOnClient(client -> check(!defaultGroup().isCollapsed, "Header click did not expand"));

    double renameX = context.computeOnClient(client -> 100.0 +
        client.textRenderer.getWidth(Text.literal("▼ " + defaultGroup().name)) + 10);
    click(context, renameX, 94, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    context.waitForScreen(GroupRenameScreen.class);
    double centerX = context.computeOnClient(client -> client.getWindow().getScaledWidth() / 2.0);
    double centerY = context.computeOnClient(client -> client.getWindow().getScaledHeight() / 2.0);
    click(context, centerX, centerY - 10, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    // This Fabric API version sends key events without modifier flags.
    context.getInput().pressKey(GLFW.GLFW_KEY_END);
    int oldNameLength = context.computeOnClient(client -> defaultGroup().name.length());
    for (int i = 0; i < oldNameLength; i++) {
      context.getInput().pressKey(GLFW.GLFW_KEY_BACKSPACE);
    }
    context.getInput().typeChars("Coordinates");
    context.clickScreenButton("pinchat.gui.groupRename.save");
    context.waitForScreen(ChatScreen.class);
    context.runOnClient(client -> check(defaultGroup().name.equals("Coordinates"), "Rename was not saved: " + defaultGroup().name));
    context.takeScreenshot("pinchat-groups");

    // Reload from disk after discarding runtime state; serialization alone is insufficient.
    context.runOnClient(client -> {
      defaultGroup().x = 135;
      defaultGroup().y = 85;
      defaultGroup().scale = 1.5;
      defaultGroup().isCollapsed = true;
      PinChatConfig.save();
      PinnedMessages.groups.clear();
      PinChatConfig.load();
      var restored = defaultGroup();
      check(PinnedMessages.groups.size() == 2, "Config reload lost a group");
      check(restored.name.equals("Coordinates") && restored.messages.equals(List.of(MESSAGE)),
          "Config reload lost name or content");
      check(restored.x == 135 && restored.y == 85 && restored.scale == 1.5 && restored.isCollapsed,
          "Config reload lost position, scale or collapsed state");
      restored.isCollapsed = false;
    });
    click(context, 138, 88, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    context.runOnClient(client -> check(defaultGroup().messages.isEmpty(), "Pinned row right-click did not remove it"));

    // Limits are checked through the chat screen with distinct creation ticks.
    context.runOnClient(client -> PinChatConfig.maxPinnedMessages = 1);
    addAndClick(context, "First");
    addAndClick(context, "Second");
    context.runOnClient(client -> check(defaultGroup().messages.equals(List.of("First")),
        "Per-group limit accepted a second message or removed the first"));

    context.setScreen(() -> null);
    context.waitTicks(40); // Allow the player to settle on the flat world's ground.
    var originalInput = context.computeOnClient(client -> client.player.input);
    context.getInput().pressKey(PinChatKeyBindings.openMoveableChatKey);
    context.waitForScreen(MoveableChatScreen.class);
    context.runOnClient(client -> check(client.player.input != originalInput, "Moveable chat did not replace input"));
    var start = context.computeOnClient(client -> client.player.getEntityPos());
    context.getInput().holdKeyFor(options -> options.forwardKey, 20);
    context.waitTick();
    context.runOnClient(client -> {
      var end = client.player.getEntityPos();
      double dx = end.x - start.x;
      double dz = end.z - start.z;
      check(dx * dx + dz * dz > 0.25, "Player did not move while chat was open");
      check(client.currentScreen instanceof MoveableChatScreen, "Movement unexpectedly closed chat");
    });
    context.takeScreenshot("pinchat-moveable-chat");
    context.getInput().pressKey(GLFW.GLFW_KEY_ESCAPE);
    context.waitForScreen(null);
    context.runOnClient(client -> check(client.player.input == originalInput, "Closing chat did not restore input"));

    context.getInput().pressKey(PinChatKeyBindings.openConfigKey);
    context.waitForScreen(PinChatConfigScreen.class);
    click(context, centerX, centerY, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    context.runOnClient(client -> {
      check(!PinChatConfig.moveableChatEnabled, "Settings toggle did not disable moveable chat");
      PinChatConfig.moveableChatEnabled = true;
      PinChatConfig.load();
      check(!PinChatConfig.moveableChatEnabled, "Settings toggle was not persisted");
    });
    context.clickScreenButton("gui.done");
    context.waitForScreen(null);
    context.getInput().pressKey(PinChatKeyBindings.openMoveableChatKey);
    context.waitTicks(5);
    context.runOnClient(client -> check(client.currentScreen == null, "U opened chat while disabled"));
  }

  private static MessageGroup defaultGroup() {
    return PinnedMessages.groups.getFirst();
  }

  private static void addAndClick(ClientGameTestContext context, String message) {
    context.waitTick();
    context.runOnClient(client -> client.inGameHud.getChatHud().addMessage(Text.literal(message)));
    clickChatLine(context);
  }

  private static void clickChatLine(ClientGameTestContext context) {
    double y = context.computeOnClient(client -> client.getWindow().getScaledHeight() - 44.0);
    click(context, 20, y, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
  }

  private static void click(ClientGameTestContext context, double x, double y, int button) {
    double factor = context.computeOnClient(client -> client.getWindow().getScaleFactor());
    context.getInput().setCursorPos(x * factor, y * factor);
    context.getInput().pressMouse(button);
  }

  private static void check(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
