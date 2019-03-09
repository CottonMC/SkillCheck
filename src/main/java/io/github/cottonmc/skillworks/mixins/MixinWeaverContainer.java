package io.github.cottonmc.skillworks.mixins;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.traits.ClassManager;
import net.minecraft.container.BlockContext;
import net.minecraft.container.LoomContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomContainer.class)
public abstract class MixinWeaverContainer {
	private PlayerEntity player;

	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/container/BlockContext;)V", at = @At("RETURN"))
	public void construct(int id, PlayerInventory inv, final BlockContext ctx, CallbackInfo ci) {
		player = inv.player;
	}

	@ModifyConstant(method = "onContentChanged", constant = @Constant(intValue = 6))
	private int changeBannerPatternLimit(int orig) {
		if (ClassManager.hasClass(player, Skillworks.WEAVER)) return 16;
		else return orig;
	}

}
