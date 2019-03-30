package io.github.cottonmc.skillworks.events;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.traits.ClassManager;
import io.github.cottonmc.skillworks.util.Dice;
import io.github.cottonmc.skillworks.util.DiceResult;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {

	public static AttackEntityCallback onPlayerAttack = (player, world, hand, entity, hitResult) -> {
		if (player.getStackInHand(hand).isEmpty()) {
			if (ClassManager.hasClass(player, Skillworks.BRAWLER)) {
				for (ItemStack stack : entity.getItemsArmor()) {
					if (stack.getItem() instanceof ArmorItem) return ActionResult.PASS;
				}
				if (entity instanceof LivingEntity) {
					LivingEntity mob = (LivingEntity) entity;
					if (ClassManager.hasLevel(player, Skillworks.BRAWLER, 2) && !hasWeakness(mob)) {
						DiceResult roll = Dice.roll("1d20+"+ClassManager.getLevel(player, Skillworks.BRAWLER));
						if (Skillworks.config.showDiceRolls) {
							if (roll.isCritFail()) player.addChatMessage(new TranslatableTextComponent("msg.skillworks.roll.fail", roll.getFormattedNaturals()), false);
							else player.addChatMessage(new TranslatableTextComponent("msg.skillworks.roll.result", roll.getTotal(), roll.getFormattedNaturals()), false);
						}
						if (roll.isCritFail()) {
							player.addPotionEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200));
							player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
						} else if (roll.getTotal() >= Skillworks.config.weakenEnemyRoll) {
							mob.addPotionEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200));
							player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
						}
					}
				}
				entity.damage(DamageSource.player(player), ClassManager.getLevel(player, Skillworks.BRAWLER)*2);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	};

	private static boolean hasWeakness(LivingEntity entity) {
		if (entity.getPotionEffects().isEmpty()) return false;
		for (StatusEffectInstance status : entity.getPotionEffects()) {
			if (status.getEffectType() == StatusEffects.WEAKNESS) return true;
		}
		return false;
	}
}
