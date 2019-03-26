package io.github.cottonmc.skillworks.events;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.traits.ClassManager;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import java.util.Random;

public class PlayerStealEvent {

	public static UseEntityCallback onPlayerInteract = (player, world, hand, entity, HitResult) -> {
		if (player.isSpectator()
				|| !player.getStackInHand(hand).isEmpty()
				|| !ClassManager.hasClass(player, Skillworks.THIEF)
				|| !(entity instanceof MobEntity)) return ActionResult.PASS;
		MobEntity mob = (MobEntity) entity;
		if (mob.getTarget() == null || !mob.getTarget().equals(player)) {
			Random rand = new Random();
			for (ItemStack stack : mob.getItemsArmor()) {
				if (stack.isEmpty()) continue;
				int roll = rand.nextInt(20) + 1;
				if (roll == 1) {
					player.attack(mob);
					mob.setTarget(player);
					mob.addPotionEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1000));
					return ActionResult.SUCCESS;
				}
				roll += ClassManager.getLevel(player, Skillworks.THIEF);
				if (roll > 6) {
					ItemStack give = stack.copy();
					stack.subtractAmount(1);
					player.setStackInHand(hand, give);
					if (roll < 12) {
						player.attack(mob);
						mob.setTarget(player);
					}
					return ActionResult.SUCCESS;
				}
			}
			player.attack(mob);
			mob.setTarget(player);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	};
}
