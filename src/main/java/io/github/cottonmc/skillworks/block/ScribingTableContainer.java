package io.github.cottonmc.skillworks.block;

import io.github.cottonmc.skillworks.api.traits.ClassManager;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ScribingTableContainer extends Container {
	private final PlayerEntity player;
	private Identifier currentSkill;
	public List<Identifier> classes;

	public ScribingTableContainer(int syncId, PlayerEntity player, BlockContext ctx) {
		super(null, syncId);
		this.player = player;
		this.classes = new ArrayList<>(ClassManager.getClasses());
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	private int getLevelCost() {
		if (currentSkill == null) return 0;
		int level = ClassManager.getLevel(player, currentSkill);
		if (level > 3) return 30;
		return 10*level;
	}

	public void close(PlayerEntity player) {
		super.close(player);
	}

	public void setCurrentSkill(int index) {
		this.currentSkill = classes.get(index);
	}

	public boolean canLevelUp() {
		if (currentSkill == null) return false;
		if (ClassManager.getLevel(player, currentSkill) >= ClassManager.getClassMaxLevel(currentSkill)) return false;
		return player.experience >= getLevelCost();
	}
}
