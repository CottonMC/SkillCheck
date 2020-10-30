package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoomScreen.class)
public abstract class MixinLoomScreen {
	private PlayerEntity player;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void construct(LoomScreenHandler container, PlayerInventory inv, Text name, CallbackInfo ci) {
		player = inv.player;
	}

	@ModifyConstant(method = "onInventoryChanged", constant = @Constant(intValue = 6))
	private int changeBannerPatternLimit(int orig) {
		if (CharacterClasses.get(player).has(SkillCheck.ARTISAN)) return 16;
		else return orig;
	}
}
