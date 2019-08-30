package io.github.cottonmc.skillcheck.item;

import io.github.cottonmc.skillcheck.SkillCheck;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CharacterSheetItem extends Item {

	public CharacterSheetItem() {
		super(new Item.Settings().group(SkillCheck.SKILLCHECK_GROUP).maxCount(1));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (world.isClient) return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
		ContainerProviderRegistry.INSTANCE.openContainer(SkillCheck.CHARACTER_SHEET_CONTAINER, player, buf -> buf.writeBlockPos(player.getBlockPos()));
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.UNCOMMON;
	}
}
