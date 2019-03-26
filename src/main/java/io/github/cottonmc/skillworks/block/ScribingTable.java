package io.github.cottonmc.skillworks.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.FabricItemTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class ScribingTable extends Block {

	public ScribingTable() {
		super(FabricBlockSettings.of(Material.WOOD).breakByTool(FabricItemTags.AXES).build());
	}

}
