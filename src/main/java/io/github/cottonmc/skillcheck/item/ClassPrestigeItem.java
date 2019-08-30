package io.github.cottonmc.skillcheck.item;

import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ClassPrestigeItem extends Item {
	public ClassPrestigeItem() {
		super(new Item.Settings().group(SkillCheck.SKILLCHECK_GROUP));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		CharacterClasses classes = CharacterData.get(player).getClasses();
		for (Identifier id : SkillCheck.getCharSheetClasses()) {
			classes.remove(id);
		}
		player.addChatMessage(new TranslatableText("msg.skillcheck.prestige"), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltips, TooltipContext ctx) {
		tooltips.add(new TranslatableText("tooltip.skillcheck.prestige.0").formatted(Formatting.GRAY));
		tooltips.add(new TranslatableText("tooltip.skillcheck.prestige.1").formatted(Formatting.GRAY));
	}
}
