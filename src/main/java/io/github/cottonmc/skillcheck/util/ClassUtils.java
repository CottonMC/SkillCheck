package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ClassUtils {
	public static boolean levelUp(PlayerEntity player, Identifier id, int levels) {
		CharacterClasses classes = CharacterData.get(player).getClasses();
		classes.giveIfAbsent(new CharacterClassEntry(id));
		CharacterClassEntry entry = classes.get(id);
		int newLevel = entry.getLevel() + levels;
		if (newLevel > CottonRPG.CLASSES.get(id).getMaxLevel()) return false;
		entry.setLevel(newLevel);
		return true;
	}

	public static boolean hasLevel(CharacterClasses classes, Identifier id, int minLevel) {
		if (!classes.has(id)) return false;
		else return classes.get(id).getLevel() >= minLevel;
	}
}