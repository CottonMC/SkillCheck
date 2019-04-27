package io.github.cottonmc.skillworks.events;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.api.traits.ClassManager;
import io.github.cottonmc.skillworks.api.dice.Dice;
import io.github.cottonmc.skillworks.api.dice.DiceResult;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;

public class PlayerStealEvent {

	public static UseEntityCallback onPlayerInteract = (player, world, hand, entity, HitResult) -> {
		if (player.isSpectator()
				|| !player.getStackInHand(hand).isEmpty()
				|| !ClassManager.hasClass(player, Skillworks.THIEF)
				|| !(entity instanceof MobEntity)
				|| world.isClient
				|| !player.isSneaking()) return ActionResult.PASS;
		MobEntity mob = (MobEntity) entity;
		//TODO: try to figure out a good way to require you to sneak around to pickpocket?
		for (ItemStack stack : mob.getArmorItems()) {
			if (stack.isEmpty()) continue;
			DiceResult roll = Dice.roll("1d20+"+ClassManager.getLevel(player, Skillworks.THIEF));
			if (Skillworks.config.showDiceRolls) {
				if (roll.isCritFail()) player.addChatMessage(new TranslatableTextComponent("msg.skillworks.roll.fail", roll.getFormattedNaturals()), false);
				else player.addChatMessage(new TranslatableTextComponent("msg.skillworks.roll.result", roll.getTotal(), roll.getFormattedNaturals()), false);
			}
			if (roll.isCritFail()) {
				mob.addPotionEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2000));
				mob.tryAttack(player);
				if (!player.isCreative()) mob.setTarget(player);
				player.swingHand(hand);
				return ActionResult.SUCCESS;
			}
			if (roll.getTotal() >= Skillworks.config.stealArmorRoll) {
				ItemStack give = stack.copy();
				stack.subtractAmount(1);
				player.setStackInHand(hand, give);
				if (roll.getTotal() < Skillworks.config.stealArmorRoll+4) {
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
