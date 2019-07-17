package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.ClassManager;
import io.github.cottonmc.skillcheck.api.classes.PlayerClassType;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CharacterSheetContainer extends Container {
	private final PlayerEntity player;
	private Identifier currentSkill;
	public List<Identifier> classes;

	public CharacterSheetContainer(int syncId, PlayerEntity player) {
		super(null, syncId);
		this.player = player;
		this.classes = new ArrayList<>(SkillCheck.PLAYER_CLASS_TYPES.getIds());
		this.classes.sort(Comparator.comparing(Identifier::getPath));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public int getLevelCost() {
		if (currentSkill == null) return 0;
		int level = ClassManager.getLevel(player, currentSkill);
		PlayerClassType pClass = SkillCheck.PLAYER_CLASS_TYPES.get(currentSkill);
		return pClass.getNextLevelCost(level, player);
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
		if (ClassManager.getLevel(player, currentSkill) >= SkillCheck.PLAYER_CLASS_TYPES.get(currentSkill).getMaxLevel()) return false;
		return player.experienceLevel >= getLevelCost() || player.isCreative();
	}
}
