package dev.sfafy.pinchat.integration;

import net.minecraft.client.gui.screen.Screen;

public class ClothConfigIntegration {
  public static Screen createConfigScreen(Screen parent) {
    me.shedaniel.clothconfig2.api.ConfigBuilder builder = me.shedaniel.clothconfig2.api.ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(net.minecraft.text.Text.translatable("pinchat.config.title"));

    builder.setSavingRunnable(dev.sfafy.pinchat.config.PinChatConfig::save);

    me.shedaniel.clothconfig2.api.ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    me.shedaniel.clothconfig2.api.ConfigCategory general = builder
        .getOrCreateCategory(net.minecraft.text.Text.translatable("pinchat.config.category.general"));

    general.addEntry(entryBuilder
        .startIntField(net.minecraft.text.Text.translatable("pinchat.config.maxPinnedMessages"),
            dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages)
        .setDefaultValue(5)
        .setTooltip(net.minecraft.text.Text.translatable("pinchat.config.maxPinnedMessages.tooltip"))
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.maxPinnedMessages = newValue)
        .build());

    general.addEntry(entryBuilder
        .startIntField(net.minecraft.text.Text.translatable("pinchat.config.maxLineWidth"),
            dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth)
        .setDefaultValue(200)
        .setTooltip(net.minecraft.text.Text.translatable("pinchat.config.maxLineWidth.tooltip"))
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.maxLineWidth = newValue)
        .build());

    general.addEntry(entryBuilder
        .startDoubleField(net.minecraft.text.Text.translatable("pinchat.config.chatSensitivity"),
            dev.sfafy.pinchat.config.PinChatConfig.chatSensitivity)
        .setDefaultValue(3.0)
        .setTooltip(net.minecraft.text.Text.translatable("pinchat.config.chatSensitivity.tooltip"))
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.chatSensitivity = newValue)
        .build());

    general.addEntry(entryBuilder
        .startIntField(net.minecraft.text.Text.translatable("pinchat.config.pinnedX"),
            dev.sfafy.pinchat.config.PinChatConfig.pinnedX)
        .setDefaultValue(10)
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedX = newValue)
        .build());

    general.addEntry(entryBuilder
        .startIntField(net.minecraft.text.Text.translatable("pinchat.config.pinnedY"),
            dev.sfafy.pinchat.config.PinChatConfig.pinnedY)
        .setDefaultValue(10)
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedY = newValue)
        .build());

    general.addEntry(entryBuilder
        .startDoubleField(net.minecraft.text.Text.translatable("pinchat.config.pinnedScale"),
            dev.sfafy.pinchat.config.PinChatConfig.pinnedScale)
        .setDefaultValue(1.0)
        .setSaveConsumer(newValue -> dev.sfafy.pinchat.config.PinChatConfig.pinnedScale = newValue)
        .build());

    return builder.build();
  }
}
