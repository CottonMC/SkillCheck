package io.github.cottonmc.skillworks;

import com.raphydaphy.crochet.data.PlayerData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class TraitPrestigeItem extends Item {
	public TraitPrestigeItem() {
		super(new Item.Settings().itemGroup(Skillworks.SKILLWORKS_GROUP));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		PlayerData.get(player, Skillworks.MOD_ID).remove("Classes");
		PlayerData.markDirty(player);
		player.addChatMessage(new TranslatableTextComponent("msg.skillworks.prestige"), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<TextComponent> tooltips, TooltipContext ctx) {
		tooltips.add(new TranslatableTextComponent("tooltip.skillworks.prestige.0").applyFormat(TextFormat.GRAY));
		tooltips.add(new TranslatableTextComponent("tooltip.skillworks.prestige.1").applyFormat(TextFormat.GRAY));
	}
}
