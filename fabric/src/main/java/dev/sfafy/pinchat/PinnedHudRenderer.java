package dev.sfafy.pinchat;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

public class PinnedHudRenderer implements HudRenderCallback {

  @Override
  public void onHudRender(MatrixStack matrixStack, float tickDelta) {
    MinecraftClient client = MinecraftClient.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hudHidden || PinnedMessages.groups.isEmpty())
      return;

    for (MessageGroup group : PinnedMessages.groups) {
      if (group.messages.isEmpty())
        continue;

      matrixStack.push();
      matrixStack.translate(group.x, group.y, 0f);
      matrixStack.scale((float) group.scale, (float) group.scale, 1f);

      int lineHeight = 12;

      String indicator = group.isCollapsed ? "▶" : "▼";
      Text headerText = Text.of(indicator + " " + group.name);
      client.textRenderer.drawWithShadow(matrixStack, headerText, 0, -12, 0xFFAAAAAA);

      if (!group.isCollapsed) {
        for (int i = 0; i < group.messages.size(); i++) {
          String msgContent = group.messages.get(i);
          Text msg = Text.of(msgContent);
          int y = i * lineHeight;

          int maxWidth = dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth;
          StringVisitable trimmed = client.textRenderer.trimToWidth(msg, maxWidth);
          OrderedText renderedText = Language.getInstance().reorder(trimmed);
          int width = client.textRenderer.getWidth(renderedText);

          DrawableHelper.fill(matrixStack, -2, y - 2, width + 2, y + 8, 0x80000000);

          client.textRenderer.drawWithShadow(matrixStack, renderedText, 0, y, 0xFFFFFFFF);
        }
      }

      matrixStack.pop();
    }
  }
}
