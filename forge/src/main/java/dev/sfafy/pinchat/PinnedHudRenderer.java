package dev.sfafy.pinchat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.locale.Language;

@Mod.EventBusSubscriber(modid = PinChatMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PinnedHudRenderer {

  @SubscribeEvent
  public static void onRenderOverlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
    if (event.getType() != net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL)
      return;

    onRenderGui(event.getPoseStack());
  }

  public static void onRenderGui(PoseStack poseStack) {
    Minecraft client = Minecraft.getInstance();

    if (client == null || client.options == null)
      return;

    if (client.options.hideGui || PinnedMessages.groups.isEmpty())
      return;

    for (MessageGroup group : PinnedMessages.groups) {
      if (group.messages.isEmpty())
        continue;

      poseStack.pushPose();
      poseStack.translate((float) group.x, (float) group.y, 0f);
      poseStack.scale((float) group.scale, (float) group.scale, 1f);

      int lineHeight = 12;

      String indicator = group.isCollapsed ? "▶" : "▼";
      Component headerText = Component.literal(indicator + " " + group.name);
      client.font.drawShadow(poseStack, headerText, 0, -12, 0xFFAAAAAA);

      if (!group.isCollapsed) {
        for (int i = 0; i < group.messages.size(); i++) {
          String msgContent = group.messages.get(i);
          Component msg = Component.literal(msgContent);
          int y = i * lineHeight;

          int maxWidth = dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth;
          FormattedText trimmed = client.font.substrByWidth(msg, maxWidth);
          FormattedCharSequence renderedText = Language.getInstance().getVisualOrder(trimmed);
          int width = client.font.width(renderedText);

          com.mojang.blaze3d.systems.RenderSystem.enableBlend();
          net.minecraft.client.gui.GuiComponent.fill(poseStack, -2, y - 2, width + 2, y + 8, 0x80000000);

          client.font.drawShadow(poseStack, renderedText, 0, y, 0xFFFFFFFF);
        }
      }

      poseStack.popPose();
    }
  }
}
