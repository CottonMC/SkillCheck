package io.github.cottonmc.skillcheck;

import com.raphydaphy.crochet.data.PlayerData;
import net.minecraft.ChatFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class TraitPrestigeItem extends Item {
	public TraitPrestigeItem() {
		super(new Item.Settings().itemGroup(SkillCheck.SKILLCHECK_GROUP));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		PlayerData.get(player, SkillCheck.MOD_ID).remove("Classes");
		PlayerData.markDirty(player);
		player.addChatMessage(new TranslatableComponent("msg.skillcheck.prestige"), true);
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Override
	public void buildTooltip(ItemStack stack, World world, List<Component> tooltips, TooltipContext ctx) {
		tooltips.add(new TranslatableComponent("tooltip.skillcheck.prestige.0").applyFormat(ChatFormat.GRAY));
		tooltips.add(new TranslatableComponent("tooltip.skillcheck.prestige.1").applyFormat(ChatFormat.GRAY));
	}
}
