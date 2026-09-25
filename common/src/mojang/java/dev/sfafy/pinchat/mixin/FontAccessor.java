package dev.sfafy.pinchat.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.StringSplitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Font.class)
public interface FontAccessor {
  @Accessor("splitter")
  StringSplitter getSplitter();
}
