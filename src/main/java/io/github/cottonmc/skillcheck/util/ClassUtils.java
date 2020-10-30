package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClass;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClasses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ClassUtils {
	public static boolean levelUp(PlayerEntity player, Identifier id, int levels) {
		CharacterClasses classes = CharacterClasses.get(player);
		CharacterClassEntry entry = classes.giveIfAbsent(id);
		int newLevel = entry.getLevel() + levels;
		if (newLevel > entry.getType().getMaxLevel()) return false;
		entry.setLevel(newLevel);
		return true;
	}

	public static boolean hasLevel(CharacterClasses classes, CharacterClass clazz, int minLevel) {
		if (!classes.has(clazz)) return false;
		else return classes.get(clazz).getLevel() >= minLevel;
	}
}
