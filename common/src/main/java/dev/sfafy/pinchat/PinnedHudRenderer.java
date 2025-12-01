package dev.sfafy.pinchat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

public class PinnedHudRenderer {

  public void render(DrawContext context, float tickDelta) {
    MinecraftClient client = MinecraftClient.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hudHidden || PinnedMessages.groups.isEmpty())
      return;

    var matrices = context.getMatrices();

    for (MessageGroup group : PinnedMessages.groups) {
      if (group.messages.isEmpty())
        continue;

      matrices.pushMatrix();
      matrices.translate(group.x, group.y);
      matrices.scale((float) group.scale, (float) group.scale);

      int lineHeight = 12;

      String indicator = group.isCollapsed ? "▶" : "▼";
      Text headerText = Text.of(indicator + " " + group.name);
      context.drawText(client.textRenderer, headerText, 0, -12, 0xFFAAAAAA, true);

      if (!group.isCollapsed) {
        for (int i = 0; i < group.messages.size(); i++) {
          String msgContent = group.messages.get(i);
          Text msg = Text.of(msgContent);
          int y = i * lineHeight;

          int maxWidth = dev.sfafy.pinchat.config.ConfigHandler.getConfig().maxLineWidth;
          StringVisitable trimmed = client.textRenderer.trimToWidth(msg, maxWidth);
          OrderedText renderedText = Language.getInstance().reorder(trimmed);
          int width = client.textRenderer.getWidth(renderedText);

          context.fill(-2, y - 2, width + 2, y + 8, 0x80000000);

          context.drawText(client.textRenderer, renderedText, 0, y, 0xFFFFFFFF, true);
        }
      }

      matrices.popMatrix();
    }
  }
}
