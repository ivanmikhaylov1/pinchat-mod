package dev.sfafy.pinchat;

import dev.sfafy.pinchat.config.PinChatConfigMalilib;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class PinnedHudRenderer implements HudRenderCallback {

  @Override

  public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
    MinecraftClient client = MinecraftClient.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hudHidden || PinnedMessages.pinnedList.isEmpty())
      return;

    int startX = PinChatConfigMalilib.PINNED_X.getIntegerValue();
    int startY = PinChatConfigMalilib.PINNED_Y.getIntegerValue();

    int lineHeight = 12;

    var matrices = context.getMatrices();
    matrices.pushMatrix();
    matrices.translate((float) startX, (float) startY);
    matrices.scale(scale, scale);

    for (int i = 0; i < PinnedMessages.pinnedList.size(); i++) {
      Text msg = PinnedMessages.pinnedList.get(i);
      int y = i * lineHeight;

      int maxWidth = PinChatConfigMalilib.MAX_LINE_WIDTH.getIntegerValue();
      net.minecraft.text.StringVisitable trimmed = client.textRenderer.trimToWidth(msg, maxWidth);
      net.minecraft.text.OrderedText renderedText = net.minecraft.util.Language.getInstance().reorder(trimmed);
      int width = client.textRenderer.getWidth(renderedText);

      context.fill(-2, y - 2, width + 2, y + 8, 0x80000000);

      context.drawText(client.textRenderer, renderedText, 0, y, 0xFFFFFFFF, true);
    }

    matrices.popMatrix();
  }
}