package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.api.traits.ClassManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ClassScrollItem extends Item {

	Identifier trait;

	public ClassScrollItem(Identifier trait) {
		super(new Item.Settings().itemGroup(Skillworks.SKILLWORKS_GROUP).stackSize(1));
		this.trait = trait;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ClassManager.levelUp(player, trait);
		if (!player.isCreative()) player.getStackInHand(hand).subtractAmount(1);
		player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
		player.addChatMessage(new TranslatableTextComponent("msg.skillworks.levelup", getTraitName()), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltips, TooltipContext ctx) {
		int flavor = 0;
		if (!stack.hasTag()) {
			CompoundTag tag = new CompoundTag();
			tag.putInt("FlavorText", new Random().nextInt(6));
			stack.setTag(tag);
		} else if (!stack.getTag().containsKey("FlavorText")) {
			stack.getTag().putInt("FlavorText", new Random().nextInt(6));
		} else {
			flavor = stack.getTag().getInt("FlavorText");
		}
		tooltips.add(new TranslatableTextComponent("tooltip.skillworks.scroll.flavor_"+flavor, getTraitName()).applyFormat(TextFormat.GRAY, TextFormat.ITALIC));
	}

	String getTraitName() {
		return new TranslatableTextComponent("trait."+trait.getNamespace()+"."+trait.getPath()).getText();
	}
}
