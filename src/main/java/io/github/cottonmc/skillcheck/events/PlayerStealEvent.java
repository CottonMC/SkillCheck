package io.github.cottonmc.skillcheck.events;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.LegacyClassManager;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class PlayerStealEvent {

	public static UseEntityCallback onPlayerInteract = (player, world, hand, entity, HitResult) -> {
		if (player.isSpectator()
				|| !player.getStackInHand(hand).isEmpty()
				|| !LegacyClassManager.hasClass(player, SkillCheck.OLD_THIEF)
				|| !(entity instanceof MobEntity)
				|| world.isClient
				|| !player.isSneaking()) return ActionResult.PASS;
		MobEntity mob = (MobEntity) entity;
		//TODO: try to figure out a good way to require you to sneak around to pickpocket?
		for (ItemStack stack : mob.getArmorItems()) {
			if (stack.isEmpty()) continue;
			RollResult roll = Dice.roll("1d20+"+ LegacyClassManager.getLevel(player, SkillCheck.OLD_THIEF));
			if (SkillCheck.config.showDiceRolls) {
				if (roll.isCritFail()) player.addChatMessage(new TranslatableText("msg.skillcheck.roll.fail", roll.getFormattedNaturals()), false);
				else player.addChatMessage(new TranslatableText("msg.skillcheck.roll.result", roll.getTotal(), roll.getFormattedNaturals()), false);
			}
			if (roll.isCritFail()) {
				mob.addPotionEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2000));
				mob.tryAttack(player);
				if (!player.isCreative()) mob.setTarget(player);
				player.swingHand(hand);
				return ActionResult.SUCCESS;
			}
			if (roll.getTotal() >= SkillCheck.config.stealArmorRoll) {
				ItemStack give = stack.copy();
				stack.decrement(1);
				player.setStackInHand(hand, give);
				if (roll.getTotal() < SkillCheck.config.silentStealArmorRoll) {
					player.attack(mob);
					if (!player.isCreative()) mob.setTarget(player);
				}
				player.swingHand(hand);
				return ActionResult.SUCCESS;
			}
		}
		player.attack(mob);
		if (!player.isCreative()) mob.setTarget(player);
		player.swingHand(hand);
		return ActionResult.SUCCESS;

	};
}
