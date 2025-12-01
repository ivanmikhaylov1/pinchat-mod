package dev.sfafy.pinchat.gui;

import dev.sfafy.pinchat.MessageGroup;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class GroupRenameScreen extends Screen {
	private final Screen parent;
	private final MessageGroup group;
	private TextFieldWidget nameField;

	public GroupRenameScreen(Screen parent, MessageGroup group) {
		super(Text.of("Rename Group"));
		this.parent = parent;
		this.group = group;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int centerY = this.height / 2;

		this.nameField = new TextFieldWidget(this.textRenderer, centerX - 100, centerY - 20, 200, 20,
				Text.of("Group Name"));
		this.nameField.setText(this.group.name);
		this.nameField.setMaxLength(32);
		this.addDrawableChild(this.nameField);

		this.addDrawableChild(ButtonWidget.builder(Text.of("Save"), button -> {
			this.group.name = this.nameField.getText();
			this.client.setScreen(this.parent);
		}).dimensions(centerX - 105, centerY + 10, 100, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), button -> {
			this.client.setScreen(this.parent);
		}).dimensions(centerX + 5, centerY + 10, 100, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, this.width, this.height, 0x80000000);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40,
				0xFFFFFF);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
