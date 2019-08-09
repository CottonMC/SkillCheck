package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterClass;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.LegacyClassManager;
import io.github.cottonmc.skillcheck.api.classes.PlayerClassType;
import io.github.cottonmc.skillcheck.util.CharacterSheetCapable;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;

public class CharacterSheetContainer extends Container {
	private final PlayerEntity player;
	private Identifier currentSkill;
	public List<Identifier> classes;

	public CharacterSheetContainer(int syncId, PlayerEntity player) {
		super(null, syncId);
		this.player = player;
		this.classes = SkillCheck.getCharSheetClasses();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	public int getLevelCost() {
		if (currentSkill == null) return 0;
		int level = LegacyClassManager.getLevel(player, currentSkill);
		CharacterClass pClass = CottonRPG.CLASSES.get(currentSkill);
		if (!(pClass instanceof CharacterSheetCapable)) return 0;
		return ((CharacterSheetCapable)pClass).getNextLevelCost(level);
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
		if (LegacyClassManager.getLevel(player, currentSkill) >= CottonRPG.CLASSES.get(currentSkill).getMaxLevel()) return false;
		return player.experienceLevel >= getLevelCost() || player.isCreative();
	}
}
