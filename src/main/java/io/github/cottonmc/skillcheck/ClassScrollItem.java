package io.github.cottonmc.skillcheck;

import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ClassScrollItem extends Item {
	static final String FLAVOR_TEXT = "FlavorText";

	Identifier trait;

	public ClassScrollItem(Identifier trait) {
		super(new Item.Settings().group(SkillCheck.SKILLCHECK_GROUP).maxCount(1));
		this.trait = trait;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ClassManager.levelUp(player, trait);
		ItemStack held = player.getStackInHand(hand);
		if (!player.abilities.creativeMode) held.decrement(1);
		player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		player.addChatMessage(new TranslatableText("msg.skillcheck.levelup", getTraitName()), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, held);
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltips, TooltipContext ctx) {
		int flavor;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.containsKey(FLAVOR_TEXT, NbtType.INT)) {
			flavor = tag.getInt(FLAVOR_TEXT);
		} else {
			flavor = new Random().nextInt(6);
			tag.putInt(FLAVOR_TEXT, flavor);
		}
		tooltips.add(new TranslatableText("tooltip.skillcheck.scroll.flavor_" + flavor, getTraitName()).formatted(Formatting.GRAY, Formatting.ITALIC));
	}

	String getTraitName() {
		return new TranslatableText("class."+trait.getNamespace()+"."+trait.getPath()).asString();
	}
}
