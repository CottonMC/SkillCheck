package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.cottonrpg.data.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends AbstractTraderEntity {

	public MixinVillagerEntity(EntityType<? extends AbstractTraderEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "prepareRecipesFor", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void improveArtisanPrice(PlayerEntity player, CallbackInfo ci) {
		CharacterClasses classes = CharacterData.get(player).getClasses();
		if (classes.has(SkillCheck.ARTISAN_ID)) {
			int level = classes.get(SkillCheck.ARTISAN_ID).getLevel();
			for (TradeOffer offer : this.getOffers()) {
				//villagers only ever buy items for 1 emerald,
				if (offer.getSellItem().getItem() == Items.EMERALD) {
					int newLevel = level;
					if (level >= 5 && offer.getSellItem().getCount() == 1) {
						offer.getMutableSellItem().increment(1);
						newLevel -= 3;
					}
					double discount = 0.2D + 0.0625D * (double) newLevel;
					int discounted = (int) Math.floor(discount * (double) offer.getOriginalFirstBuyItem().getCount());
					offer.increaseSpecialPrice(-Math.max(discounted, 1));
				}
			}
		}
	}
}