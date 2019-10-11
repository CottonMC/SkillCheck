package io.github.cottonmc.skillcheck.impl;

import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ThiefCharacterClass extends SkillCheckCharacterClass {
	public ThiefCharacterClass(int maxLevel) {
		super(maxLevel);
	}

	@Override
	public void applyLevelUp(int currentLevel, PlayerEntity player) {
		super.applyLevelUp(currentLevel, player);
		if (SkillCheck.config.useStamina) {
			CharacterData.get(player).getResources().giveIfAbsent(new CharacterResourceEntry(new Identifier(SkillCheck.MOD_ID, "stamina")));
			CharacterResourceEntry res = CharacterData.get(player).getResources().get(SkillCheck.STAMINA);
			res.setMax(10*currentLevel + 10);
//			res.setCurrent(res.getMax());
		}
	}
}
