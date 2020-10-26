package io.github.cottonmc.skillcheck.events;

import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
import io.github.cottonmc.skillcheck.util.ClassUtils;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class PlayerAttackEvent implements AttackEntityCallback {

	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
		CharacterClasses classes = CharacterClasses.get(player);
		if (player.getStackInHand(hand).isEmpty()) {
			if (classes.has(SkillCheck.BRAWLER)) {
				for (ItemStack stack : entity.getArmorItems()) {
					if (stack.getItem() instanceof ArmorItem) return ActionResult.PASS;
				}
				if (entity instanceof LivingEntity) {
					LivingEntity mob = (LivingEntity) entity;
					if (ClassUtils.hasLevel(classes, SkillCheck.BRAWLER, 2) && !hasWeakness(mob)) {
						RollResult roll = Dice.roll("1d20+" + classes.get(SkillCheck.BRAWLER).getLevel());
						if (SkillCheck.config.showDiceRolls) {
							if (roll.isCritFail())
								player.sendMessage(new TranslatableText("msg.skillcheck.roll.fail", roll.getFormattedNaturals()), false);
							else
								player.sendMessage(new TranslatableText("msg.skillcheck.roll.result", roll.getTotal(), roll.getFormattedNaturals()), false);
						}
						if (roll.isCritFail()) {
							player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200));
							player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
						} else if (roll.getTotal() >= SkillCheck.config.weakenEnemyRoll) {
							mob.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200));
							player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
						}
					}
				}
				entity.damage(DamageSource.player(player), classes.get(SkillCheck.BRAWLER).getLevel() * 2);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}

	private static boolean hasWeakness(LivingEntity entity) {
		if (entity.getStatusEffects().isEmpty()) return false;
		for (StatusEffectInstance status : entity.getStatusEffects()) {
			if (status.getEffectType() == StatusEffects.WEAKNESS) return true;
		}
		return false;
	}
}
