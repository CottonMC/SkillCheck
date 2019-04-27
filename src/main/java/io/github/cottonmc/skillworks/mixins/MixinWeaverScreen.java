package io.github.cottonmc.skillworks.mixins;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.api.traits.ClassManager;
import net.minecraft.client.gui.container.LoomScreen;
import net.minecraft.container.LoomContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomScreen.class)
public abstract class MixinWeaverScreen {
	private PlayerEntity player;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void construct(LoomContainer container, PlayerInventory inv, TextComponent name, CallbackInfo ci) {
		player = inv.player;
	}

	@ModifyConstant(method = "onInventoryChanged", constant = @Constant(intValue = 6))
	private int changeBannerPatternLimit(int orig) {
		if (ClassManager.hasClass(player, Skillworks.ARTISAN)) return 16;
		else return orig;
	}
}
