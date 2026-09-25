package dev.sfafy.pinchat;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;
import org.joml.Matrix3x2fStack;

public class PinnedHudRenderer {

  public static void register() {
    net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(
        Identifier.fromNamespaceAndPath("pinchat", "pinned_messages"),
        (graphics, tickCounter) -> onRenderGui(graphics));
  }

  public static void onRenderGui(GuiGraphicsExtractor context) {
    Minecraft client = Minecraft.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hideGui || PinnedMessages.groups.isEmpty())
      return;

    org.joml.Matrix3x2fStack matrices = context.pose();

    for (MessageGroup group : PinnedMessages.groups) {
      if (group.messages.isEmpty())
        continue;

      matrices.pushMatrix();
      matrices.translate((float) group.x, (float) group.y);
      matrices.scale((float) group.scale, (float) group.scale);

      int lineHeight = 12;

      String indicator = group.isCollapsed ? "▶" : "▼";
      Component headerText = Component.literal(indicator + " " + group.name);
      context.text(client.font, headerText, 0, -12, 0xFFAAAAAA, true);

      if (!group.isCollapsed) {
        for (int i = 0; i < group.messages.size(); i++) {
          String msgContent = group.messages.get(i);
          Component msg = Component.literal(msgContent);
          int y = i * lineHeight;

          int maxWidth = dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth;
          FormattedText trimmed = client.font.substrByWidth(msg, maxWidth);
          FormattedCharSequence renderedText = Language.getInstance().getVisualOrder(trimmed);
          int width = client.font.width(renderedText);

          context.fill(-2, y - 2, width + 2, y + 8, 0x80000000);

          context.text(client.font, renderedText, 0, y, 0xFFFFFFFF, true);
        }
      }

      matrices.popMatrix();
    }
  }
}
