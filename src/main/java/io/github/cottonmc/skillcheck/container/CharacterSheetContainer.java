package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterClass;
import io.github.cottonmc.cottonrpg.data.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.util.CharSheetClass;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

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
		CharacterClasses classes = CharacterData.get(player).getClasses();
		int level;
		if (!classes.has(currentSkill)) level = 0;
		else level = classes.get(currentSkill).getLevel();
		CharacterClass pClass = CottonRPG.CLASSES.get(currentSkill);
		if (!(pClass instanceof CharSheetClass)) return 0;
		return ((CharSheetClass)pClass).getNextLevelCost(level);
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
		CharacterClasses classes = CharacterData.get(player).getClasses();
		CharacterClassEntry entry = classes.get(currentSkill);
		if (entry != null) {
			if (entry.getLevel() >= CottonRPG.CLASSES.get(currentSkill).getMaxLevel()) return false;
		}
		return player.experienceLevel >= getLevelCost() || player.isCreative();
	}
}
