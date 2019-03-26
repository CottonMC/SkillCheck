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
				|| !(entity instanceof MobEntity)
				|| world.isClient) return ActionResult.PASS;
		MobEntity mob = (MobEntity) entity;
		//TODO: try to figure out a good way to require you to sneak around to pickpocket?
		Random rand = new Random();
		for (ItemStack stack : mob.getItemsArmor()) {
			if (stack.isEmpty()) continue;
			int roll = rand.nextInt(20) + 1;
			if (roll == 1) {
				System.out.println("Critical fail!");
				player.attack(mob);
				if (!player.isCreative()) mob.setTarget(player);
				mob.addPotionEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2000));
				return ActionResult.SUCCESS;
			}
			roll += ClassManager.getLevel(player, Skillworks.THIEF);
			System.out.println(roll);
			if (roll > 6) {
				ItemStack give = stack.copy();
				stack.subtractAmount(1);
				player.setStackInHand(hand, give);
				if (roll < 12) {
					player.attack(mob);
					if (!player.isCreative()) mob.setTarget(player);
				}
				return ActionResult.SUCCESS;
			}
		}
		player.attack(mob);
		if (!player.isCreative()) mob.setTarget(player);
		return ActionResult.SUCCESS;

	};
}
