package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CharacterSheetContainer extends Container {
	private final PlayerEntity player;
	private Identifier currentSkill;
	public List<Identifier> classes;

	public CharacterSheetContainer(int syncId, PlayerEntity player) {
		super(null, syncId);
		this.player = player;
		this.classes = new ArrayList<>(ClassManager.getClasses());
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public int getLevelCost() {
		if (currentSkill == null) return 0;
		int level = ClassManager.getLevel(player, currentSkill);
		if (level > 3) return 30;
		if (level == 0) return 5;
		return 10*level;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
	}

	public void setCurrentSkill(int index) {
		this.currentSkill = classes.get(index);
	}

	public boolean canLevelUp() {
		if (currentSkill == null) return false;
		if (ClassManager.getLevel(player, currentSkill) >= ClassManager.getClassMaxLevel(currentSkill)) return false;
		return player.experienceLevel >= getLevelCost() || player.isCreative();
	}
}
