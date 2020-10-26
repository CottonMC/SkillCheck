package io.github.cottonmc.skillcheck.item;

import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClass;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.rpgresource.CharacterResources;
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
		CharacterClasses classes = CharacterClasses.get(player);
		for (CharacterClass id : SkillCheck.getCharSheetClasses()) {
			classes.remove(id);
		}
		CharacterResources.get(player).remove(SkillCheck.STAMINA);
		player.sendMessage(new TranslatableText("msg.skillcheck.prestige"), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltips, TooltipContext ctx) {
		tooltips.add(new TranslatableText("tooltip.skillcheck.prestige.0").formatted(Formatting.GRAY));
		tooltips.add(new TranslatableText("tooltip.skillcheck.prestige.1").formatted(Formatting.GRAY));
	}
}
