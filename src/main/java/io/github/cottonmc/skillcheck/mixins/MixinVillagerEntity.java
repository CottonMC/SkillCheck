package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
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
		if (classes.has(SkillCheck.ARTISAN)) {
			int level = classes.get(SkillCheck.ARTISAN).getLevel();
			for (TradeOffer offer : this.getOffers()) {
				//normal villagers only ever buy items for 1 emerald.
				if (offer.getSellItem().getItem() == Items.EMERALD) {
					int newLevel = level;
					if (level >= 5  && offer.getSellItem().getCount() == 1) {
						offer.getMutableSellItem().increment(1);
						newLevel -= 3;
					}
					//prevent non-level-5 players from getting the 2-emerald deal
					if (level < 5 && offer.getSellItem().getCount() == 2) {
						offer.getMutableSellItem().decrement(1);
					}
					double discount = 0.2D + 0.0625D * (double) newLevel;
					int discounted = (int) Math.floor(discount * (double) offer.getOriginalFirstBuyItem().getCount());
					offer.increaseSpecialPrice(-Math.max(discounted, 1));
				}
			}
		} else {
			for (TradeOffer offer : this.getOffers()) {
				//prevent non-level-5 players from getting the 2-emerald deal
				if (offer.getSellItem().getItem() == Items.EMERALD && offer.getSellItem().getCount() == 2) {
					offer.getMutableSellItem().decrement(1);
				}
			}
		}
	}
}
