package io.github.cottonmc.skillworks.events;

import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Traits;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public class PlayerAttackEvent {

	public static AttackEntityCallback onPlayerAttack = (player, world, hand, entity, hitResult) -> {
		if (player.getStackInHand(hand).isEmpty()) {
			if (Traits.get(player, Skillworks.FISTICUFFS).getValue() > 0) {
				for (ItemStack stack : entity.getItemsArmor()) {
					if (stack.getItem() instanceof ArmorItem) return ActionResult.PASS;
				}
				entity.damage(DamageSource.player(player), Traits.get(player, Skillworks.FISTICUFFS).getValue());
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	};
}
