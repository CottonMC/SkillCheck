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

public class TraitModItem extends Item {
	private float floatToSet;
	private boolean booleanToSet;

	public TraitModItem() {
		super(new Item.Settings().itemGroup(ItemGroup.MISC));
	}

	public TraitModItem(float value) {
		this();
		this.floatToSet = value;
	}

	public TraitModItem(boolean value) {
		this();
		this.floatToSet = -1;
		this.booleanToSet = value;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (floatToSet != -1) {
			Traits.get(player, Skillworks.FISTICUFFS).setValue(floatToSet);
			player.addChatMessage(new StringTextComponent("Trait value set to " + floatToSet), true);
		} else {
			Traits.get(player, Skillworks.WEAVER).setValue(booleanToSet);
			player.addChatMessage(new StringTextComponent("Trait value set to " + booleanToSet), true);
		}
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}
}
