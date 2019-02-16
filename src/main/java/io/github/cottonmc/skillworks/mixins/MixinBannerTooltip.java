package io.github.cottonmc.skillworks.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BannerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BannerItem.class)
public abstract class MixinBannerTooltip {

	@Inject(method = "buildBannerTooltip",
			at = @At("TAIL"),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			remap = false)
	private static void appendBannerHoverText(ItemStack p_appendHoverTextFromTileEntityTag_0_, List<TextComponent> p_appendHoverTextFromTileEntityTag_1_, CallbackInfo ci,
											  CompoundTag var2, ListTag var3) {
		if (var3 != null) {
			if (var3.size() > 6) {
				p_appendHoverTextFromTileEntityTag_1_.add((new TranslatableTextComponent("tooltip.skillworks.banner."+(var3.size()-6), new Object[0])));
			}
		}
	}
}