package dev.sfafy.pinchat.integration;

import net.minecraft.client.gui.screen.Screen;

public class YaclIntegration {
  public static Screen createConfigScreen(Screen parent) {
    return dev.isxander.yacl3.api.YetAnotherConfigLib.createBuilder()
        .title(net.minecraft.text.Text.translatable("pinchat.config.title"))
        .category(dev.isxander.yacl3.api.ConfigCategory.createBuilder()
            .name(net.minecraft.text.Text.translatable("pinchat.config.category.general"))
            .option(dev.isxander.yacl3.api.Option.<Integer>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.maxPinnedMessages"))
                .description(dev.isxander.yacl3.api.OptionDescription
                    .of(net.minecraft.text.Text.translatable("pinchat.config.maxPinnedMessages.tooltip")))
                .binding(5, () -> dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder.create(opt)
                    .range(1, 100)
                    .step(1))
                .build())
            .option(dev.isxander.yacl3.api.Option.<Integer>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.maxLineWidth"))
                .description(dev.isxander.yacl3.api.OptionDescription
                    .of(net.minecraft.text.Text.translatable("pinchat.config.maxLineWidth.tooltip")))
                .binding(200, () -> dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder.create(opt)
                    .range(50, 1000)
                    .step(10))
                .build())
            .option(dev.isxander.yacl3.api.Option.<Double>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.chatSensitivity"))
                .description(dev.isxander.yacl3.api.OptionDescription
                    .of(net.minecraft.text.Text.translatable("pinchat.config.chatSensitivity.tooltip")))
                .binding(3.0, () -> dev.sfafy.pinchat.config.PinChatConfig.chatSensitivity,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.chatSensitivity = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder.create(opt)
                    .range(0.1, 10.0)
                    .step(0.1))
                .build())
            // Pinned HUD settings
            .option(dev.isxander.yacl3.api.Option.<Integer>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.pinnedX"))
                .binding(10, () -> dev.sfafy.pinchat.config.PinChatConfig.pinnedX,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedX = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder.create(opt))
                .build())
            .option(dev.isxander.yacl3.api.Option.<Integer>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.pinnedY"))
                .binding(10, () -> dev.sfafy.pinchat.config.PinChatConfig.pinnedY,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedY = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder.create(opt))
                .build())
            .option(dev.isxander.yacl3.api.Option.<Double>createBuilder()
                .name(net.minecraft.text.Text.translatable("pinchat.config.pinnedScale"))
                .binding(1.0, () -> dev.sfafy.pinchat.config.PinChatConfig.pinnedScale,
                    newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedScale = newValue)
                .controller(opt -> dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder.create(opt)
                    .range(0.5, 3.0)
                    .step(0.1))
                .build())
            .build())
        .save(dev.sfafy.pinchat.config.PinChatConfig::save)
        .build()
        .generateScreen(parent);
  }
}
