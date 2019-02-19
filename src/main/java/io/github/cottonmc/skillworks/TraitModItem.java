package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.traits.FloatTrait;
import me.elucent.earlgray.api.TraitRegistry;
import me.elucent.earlgray.api.Traits;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TraitModItem extends Item {
	boolean setFloatValue;
	private float floatToSet;
	private Identifier traitToSet;

	public TraitModItem(Identifier trait) {
		super(new Item.Settings().itemGroup(Skillworks.SKILLWORKS_GROUP));
		this.traitToSet = trait;
	}

	public TraitModItem(Identifier trait, float value) {
		this(trait);
		this.setFloatValue = true;
		this.floatToSet = value;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (setFloatValue) {
			((FloatTrait)Traits.get(player, TraitRegistry.getEntry(traitToSet))).setValue(floatToSet);
			player.addChatMessage(new StringTextComponent("Trait value set to " + floatToSet), true);
		} else {
			Traits.add(player, TraitRegistry.getEntry(traitToSet).generate());
			player.addChatMessage(new StringTextComponent("Class " + traitToSet.getPath() + " added"), true);
		}
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}
}
