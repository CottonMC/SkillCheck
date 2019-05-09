package io.github.cottonmc.skillcheck.mixins;

import net.minecraft.ChatFormat;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
			locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private static void appendBannerHoverText(ItemStack stack, List<Component> tooltip, CallbackInfo ci,
											  CompoundTag var2, ListTag var3) {
		if (var3 != null) {
			if (var3.size() > 6) {
				tooltip.add((new TranslatableComponent("tooltip.skillcheck.banner", (var3.size()-6))).applyFormat(ChatFormat.GRAY, ChatFormat.ITALIC));
			}
		}
	}
}