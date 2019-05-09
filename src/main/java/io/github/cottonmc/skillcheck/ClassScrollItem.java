package io.github.cottonmc.skillcheck;

import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ClassScrollItem extends Item {
	static final String FLAVOR_TEXT = "FlavorText";

	Identifier trait;

	public ClassScrollItem(Identifier trait) {
		super(new Item.Settings().itemGroup(SkillCheck.SKILLCHECK_GROUP).stackSize(1));
		this.trait = trait;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ClassManager.levelUp(player, trait);
		ItemStack held = player.getStackInHand(hand);
		if (!player.abilities.creativeMode) held.subtractAmount(1);
		player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		player.addChatMessage(new TranslatableComponent("msg.skillcheck.levelup", getTraitName()), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, held);
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<Component> tooltips, TooltipContext ctx) {
		int flavor;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.containsKey(FLAVOR_TEXT, NbtType.INT)) {
			flavor = tag.getInt(FLAVOR_TEXT);
		} else {
			flavor = new Random().nextInt(6);
			tag.putInt(FLAVOR_TEXT, flavor);
		}
		tooltips.add(new TranslatableComponent("tooltip.skillcheck.scroll.flavor_" + flavor, getTraitName()).applyFormat(ChatFormat.GRAY, ChatFormat.ITALIC));
	}

	String getTraitName() {
		return new TranslatableComponent("class."+trait.getNamespace()+"."+trait.getPath()).getText();
	}
}
