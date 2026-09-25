package dev.sfafy.pinchat.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface PinChatKeyBindingAccessor {
  @Accessor("key")
  InputConstants.Key getBoundKey();
}
