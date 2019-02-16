package io.github.cottonmc.skillworks.mixins;

import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Traits;
import net.minecraft.class_3914;
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
public abstract class MixinWeaver {
	private PlayerEntity player;

	public MixinWeaver() {

	}

	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/class_3914;)V", at = @At("RETURN"))
	public void construct(int int_1, PlayerInventory playerInventory_1, final class_3914 class_3914_1, CallbackInfo ci) {
		player = playerInventory_1.player;
	}

	@ModifyConstant(method = "onContentChanged", constant = @Constant(intValue = 6))
	private int changeBannerPatternLimit(int orig) {
		if (Traits.get(player, Skillworks.WEAVER).getValue()) return 16;
		else return orig;
	}

}
