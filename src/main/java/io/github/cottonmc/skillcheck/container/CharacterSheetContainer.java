package io.github.cottonmc.skillcheck.container;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.List;

public class CharacterSheetContainer extends ScreenHandler {
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

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
	}

	public void setCurrentSkill(int index) {
		this.currentSkill = classes.get(index);
	}

	public boolean canLevelUp() {
		if (currentSkill == null || !CottonRPG.CLASSES.containsId(currentSkill)) return false;
		CharacterClasses classes = CharacterData.get(player).getClasses();
		CharacterClassEntry entry = classes.get(currentSkill);
		int currentLevel = 0;
		if (entry != null) {
			currentLevel = entry.getLevel();
			if (currentLevel >= CottonRPG.CLASSES.get(currentSkill).getMaxLevel()) return false;
		}
		return CottonRPG.CLASSES.get(currentSkill).canLevelUp(currentLevel, player);
	}
}
