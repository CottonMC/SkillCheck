package io.github.cottonmc.skillworks.block;

import io.github.cottonmc.skillworks.Skillworks;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.tag.FabricItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ScribingTable extends Block {

	public ScribingTable() {
		super(FabricBlockSettings.of(Material.WOOD).breakByTool(FabricItemTags.AXES).build());
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return true;
		ContainerProviderRegistry.INSTANCE.openContainer(Skillworks.SCRIBING_CONTAINER, player, buf -> buf.writeBlockPos(pos));
		return true;
	}
}
