package io.github.cottonmc.skillworks.events;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.traits.ClassManager;
import me.elucent.earlgray.api.Traits;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {

	public static AttackEntityCallback onPlayerAttack = (player, world, hand, entity, hitResult) -> {
		if (player.getStackInHand(hand).isEmpty()) {
			if (ClassManager.hasClass(player, Skillworks.BRAWLER)) {
				int level = Traits.get(player, Skillworks.BRAWLER).getLevel();
				for (ItemStack stack : entity.getItemsArmor()) {
					if (stack.getItem() instanceof ArmorItem) return ActionResult.PASS;
				}
				if (entity instanceof LivingEntity) {
					LivingEntity mob = (LivingEntity) entity;
					if (level >= 2 && !hasWeakness(mob)) {
						mob.addPotionEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200));
						player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
					}
				}
				entity.damage(DamageSource.player(player), level*2);
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
