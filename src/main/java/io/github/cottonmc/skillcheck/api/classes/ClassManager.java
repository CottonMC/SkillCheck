package io.github.cottonmc.skillcheck.api.classes;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterClass;
import io.github.cottonmc.cottonrpg.data.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

/**
 * NOTE: This only exists to preserve levels for players.
 * Will be removed in SkillCheck 2.2.
 */
public class ClassManager {
	/**
	 * Get an object representing a player's progress with a class.
	 * @param player the player to query
	 * @param id the ID of the class to query about
	 * @return the player's class, or null
	 */
	@Nullable
	public static CharacterClassEntry getPlayerClass(PlayerEntity player, Identifier id) {
		return CharacterData.get(player).getClasses().get(id);
	}

	public static boolean hasClass(PlayerEntity player, CharacterClass type) {
		return hasClass(player, CottonRPG.CLASSES.getId(type));
	}

	/**
	 * Query whether a player has a certain class.
	 * @param player the player to query
	 * @param id the ID of the class to query about
	 * @return whether the player should be considered as having the class
	 */
	public static boolean hasClass(PlayerEntity player, Identifier id) {
		if (SkillCheck.config.disableClasses) return true;
		if (CharacterData.get(player).getClasses().has(id)) return true;
		else return LegacyClassManager.hasClass(player, id);
	}

	/**
	 * Add info for a class to a player, starting at level 0 with 0 XP.
	 * @param player the player to add to
	 * @param id the ID of the class to add
	 */
	public static void addPlayerClass(PlayerEntity player, Identifier id) {
		CharacterData.get(player).getClasses().giveIfAbsent(new CharacterClassEntry(id));
	}

	public static boolean hasLevel(PlayerEntity player, CharacterClass type, int level) {
		return hasLevel(player, CottonRPG.CLASSES.getId(type), level);
	}

	/**
	 * Query whether a player should be considered as having a certain level of a class.
	 * @param player the player to query
	 * @param id the ID to check for
	 * @param level the minimum level required
	 * @return whether the player has the level, or true if classes are disabled
	 */
	public static boolean hasLevel(PlayerEntity player, Identifier id, int level) {
		if (SkillCheck.config.disableClasses) return true;
		CharacterClassEntry entry = getPlayerClass(player, id);
		if (entry == null) {
			return LegacyClassManager.hasLevel(player, id, level);
		}
		return entry.getLevel() >= level;
	}

	public static int getLevel(PlayerEntity player, CharacterClass type) {
		return getLevel(player, CottonRPG.CLASSES.getId(type));
	}

	/**
	 * Query the level of a certain class that a player has.
	 * @param player the player to query
	 * @param id the ID to query about
	 * @return he level the player has, or 0 if they don't have the class or classes are disabled
	 */
	public static int getLevel(PlayerEntity player, Identifier id) {
		if (SkillCheck.config.disableClasses) return 0;
		CharacterClassEntry trait = getPlayerClass(player, id);
		return trait.getLevel();
	}

	/**
	 * Level up a player's class by 1.
	 * @param player the player leveling up
	 * @param id the class being leveled up
	 * @return whether the levelup was successful
	 */
	public static boolean levelUp(PlayerEntity player, Identifier id) {
		return levelUp(player, id, 1);
	}

	/**
	 * Level up a player's class.
	 * @param player the player leveling up
	 * @param id the class being leveled up
	 * @param amount how many levels the player is gaining
	 * @return whether the levelup was successful (didn't exceed level cap)
	 */
	public static boolean levelUp(PlayerEntity player, Identifier id, int amount) {
		if (SkillCheck.config.disableClasses) return false;
		CharacterClasses classes = CharacterData.get(player).getClasses();
		int current = classes.get(id).getLevel();
		int upTo = current + amount;
		CharacterClass clazz = CottonRPG.CLASSES.get(id);
		if (clazz.getMaxLevel() < upTo) return false;
		classes.get(id).setLevel(current + amount);
		return true;
	}

	public static void tryPortClasses(PlayerEntity player) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			SkillCheckNetworking.requestClassPort();
		} else {
			CharacterClasses classes = CharacterData.get(player).getClasses();
			for (Identifier id : SkillCheck.getCharSheetClasses()) {
				if (LegacyClassManager.hasClass(player, id) && !classes.has(id)) {
					PlayerClass old = LegacyClassManager.getPlayerClass(player, id);
					CharacterClassEntry entry = new CharacterClassEntry(id);
					entry.setLevel(old.getLevel());
					entry.setExperience(old.getExperience());
					classes.giveIfAbsent(entry);
				}
			}
		}
	}
}
