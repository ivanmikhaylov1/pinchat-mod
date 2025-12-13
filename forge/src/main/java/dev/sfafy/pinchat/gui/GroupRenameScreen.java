package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.MessageGroup;
import dev.sfafy.pinchat.config.PinChatConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class GroupRenameScreen extends Screen {
	private final Screen parent;
	private final MessageGroup group;
	private EditBox nameField;

	public GroupRenameScreen(Screen parent, MessageGroup group) {
		super(Component.translatable("pinchat.gui.groupRename.title"));
		this.parent = parent;
		this.group = group;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;

		this.nameField = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20,
				Component.translatable("pinchat.gui.groupRename.field"));
		this.nameField.setValue(this.group.name);
		this.nameField.setMaxLength(32);
		this.addRenderableWidget(this.nameField);

		this.addRenderableWidget(Button.builder(Component.translatable("pinchat.gui.groupRename.save"), button -> {
			this.group.name = this.nameField.getValue();
			PinChatConfig.save();
			this.minecraft.setScreen(this.parent);
		}).bounds(centerX - 105, centerY + 10, 100, 20).build());

		this.addRenderableWidget(Button.builder(Component.translatable("pinchat.gui.groupRename.cancel"), button -> {
			this.minecraft.setScreen(this.parent);
		}).bounds(centerX + 5, centerY + 10, 100, 20).build());
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, this.width, this.height, 0x80000000);
		context.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40,
				0xFFFFFF);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
