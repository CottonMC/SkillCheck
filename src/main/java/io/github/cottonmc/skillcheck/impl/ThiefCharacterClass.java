package io.github.cottonmc.skillcheck.impl;

import io.github.cottonmc.cottonrpg.data.rpgresource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.rpgresource.CharacterResources;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;

public class ThiefCharacterClass extends SkillCheckCharacterClass {
	public ThiefCharacterClass(int maxLevel) {
		super(maxLevel);
	}

	@Override
	public void applyLevelUp(int currentLevel, PlayerEntity player) {
		super.applyLevelUp(currentLevel, player);
		if (SkillCheck.config.useStamina) {
			CharacterResourceEntry res = CharacterResources.get(player).giveIfAbsent(SkillCheck.STAMINA);
			res.setMax(10*currentLevel + 10);
//			res.setCurrent(res.getMax());
		}
	}
}
