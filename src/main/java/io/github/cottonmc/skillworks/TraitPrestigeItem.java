package io.github.cottonmc.skillworks;

import me.elucent.earlgray.api.Traits;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TraitPrestigeItem extends Item {
	public TraitPrestigeItem() {
		super(new Item.Settings().itemGroup(Skillworks.SKILLWORKS_GROUP));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		Traits.get(player, Skillworks.FISTICUFFS).setValue(0);
		Traits.remove(player, Skillworks.GYMNIST);
		Traits.remove(player, Skillworks.WEAVER);
		player.addChatMessage(new StringTextComponent("All traits reset"), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}
}
