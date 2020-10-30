package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClass;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

import java.util.List;

public class CharacterSheetContainer extends ScreenHandler {
	private final PlayerEntity player;
	private CharacterClass currentSkill;
	public List<CharacterClass> classes;

	public CharacterSheetContainer(int syncId, PlayerEntity player) {
		super(null, syncId);
		this.player = player;
		this.classes = SkillCheck.getCharSheetClasses();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
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
		CharacterClasses classes = CharacterClasses.get(player);
		CharacterClassEntry entry = classes.get(currentSkill);
		int currentLevel = 0;
		if (entry != null) {
			currentLevel = entry.getLevel();
			if (currentLevel >= currentSkill.getMaxLevel()) return false;
		}
		return currentSkill.canLevelUp(currentLevel, player);
	}
}
