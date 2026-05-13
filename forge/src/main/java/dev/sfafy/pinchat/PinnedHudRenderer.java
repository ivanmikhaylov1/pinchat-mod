package dev.sfafy.pinchat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PinnedHudRenderer {

  @SubscribeEvent
  public static void registerOverlays(net.minecraftforge.client.event.RegisterGuiOverlaysEvent event) {
    event.registerAboveAll(new ResourceLocation(PinChatMod.MOD_ID, "pinned_messages"),
        (gui, graphics, partialTick, screenWidth, screenHeight) -> {
          onRenderGui(graphics);
        });
  }

  public static void onRenderGui(GuiGraphics context) {
    Minecraft client = Minecraft.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hideGui || PinnedMessages.groups.isEmpty())
      return;

    PoseStack matrices = context.pose();

    for (MessageGroup group : PinnedMessages.groups) {
      if (group.messages.isEmpty())
        continue;

      matrices.pushPose();
      matrices.translate((float) group.x, (float) group.y, 0f);
      matrices.scale((float) group.scale, (float) group.scale, 1f);

      int lineHeight = 12;

      String indicator = group.isCollapsed ? "▶" : "▼";
      Component headerText = Component.literal(indicator + " " + group.name);
      context.drawString(client.font, headerText, 0, -12, 0xFFAAAAAA, true);

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

          context.drawString(client.font, renderedText, 0, y, 0xFFFFFFFF, true);
        }
      }

      matrices.popPose();
    }
  }
}
