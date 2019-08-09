package io.github.cottonmc.skillcheck.api.classes;

import com.raphydaphy.crochet.data.PlayerData;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

/**
 * Now replaced with CottonRPG. Only exists to preserve levels for players.
 * Will be removed in SkillCheck 2.2.
 */
@Deprecated
public class LegacyClassManager {

	/**
	 * Query the classes a player has data for.
	 * @param player the player to query
	 * @return the compound tag of all class data
	 */
	public static CompoundTag getPlayerClasses(PlayerEntity player) {
		CompoundTag data =  PlayerData.get(player, SkillCheck.MOD_ID);
		if (!data.containsKey("Classes")) {
			data.put("Classes", new CompoundTag());
			PlayerData.markDirty(player);
		}
		return data.getCompound("Classes");
	}

	/**
	 * Get an object representing a player's progress with a class.
	 * @param player the player to query
	 * @param id the ID of the class to query about
	 * @return the player's class, or null
	 */
	@Nullable
	public static PlayerClass getPlayerClass(PlayerEntity player, Identifier id) {
		CompoundTag classes = getPlayerClasses(player);
		if (!classes.containsKey(id.toString())) return null;
		PlayerClass trait = new PlayerClass(id);
		trait.fromNBT(classes.getCompound(id.toString()));
		return trait;
	}

	public static boolean hasClass(PlayerEntity player, PlayerClassType type) {
		return hasClass(player, SkillCheck.PLAYER_CLASS_TYPES.getId(type));
	}

	/**
	 * Query whether a player has a certain class.
	 * @param player the player to query
	 * @param id the ID of the class to query about
	 * @return whether the player should be considered as having the class
	 */
	public static boolean hasClass(PlayerEntity player, Identifier id) {
		if (SkillCheck.config.disableClasses) return true;
		return (getPlayerClasses(player).containsKey(id.toString()));
	}

	/**
	 * Add info for a class to a player, starting at level 0 with 0 XP.
	 * @param player the player to add to
	 * @param id the ID of the class to add
	 */
	public static void addPlayerClass(PlayerEntity player, Identifier id) {
		putPlayerClass(player, id, new PlayerClass(id));
	}

	/**
	 * Set a player's class info to a specific value.
	 * @param player the player to set on
	 * @param id the ID of the class to set
	 * @param trait the values for the class
	 */
	public static void putPlayerClass(PlayerEntity player, Identifier id, PlayerClass trait) {
		CompoundTag classes = getPlayerClasses(player);
		if (classes.containsKey(id.toString())) classes.remove(id.toString());
		classes.put(id.toString(), trait.toNBT());
		PlayerData.markDirty(player);
	}

	public static boolean hasLevel(PlayerEntity player, PlayerClassType type, int level) {
		return hasLevel(player, SkillCheck.PLAYER_CLASS_TYPES.getId(type), level);
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
		PlayerClass trait = getPlayerClass(player, id);
		if (trait == null) return false;
		return trait.getLevel() >= level;
	}

	public static int getLevel(PlayerEntity player, PlayerClassType type) {
		return getLevel(player, SkillCheck.PLAYER_CLASS_TYPES.getId(type));
	}

	/**
	 * Query the level of a certain class that a player has.
	 * @param player the player to query
	 * @param id the ID to query about
	 * @return he level the player has, or 0 if they don't have the class or classes are disabled
	 */
	public static int getLevel(PlayerEntity player, Identifier id) {
		if (SkillCheck.config.disableClasses) return 0;
		if (!hasClass(player, id)) return 0;
		PlayerClass trait = getPlayerClass(player, id);
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

		if (!hasClass(player, id)) addPlayerClass(player, id);
		PlayerClass trait = getPlayerClass(player, id);
		int levelTo = trait.getLevel() + amount;
		if (levelTo > SkillCheck.PLAYER_CLASS_TYPES.get(id).getMaxLevel()) {
			trait.setLevel(SkillCheck.PLAYER_CLASS_TYPES.get(id).getMaxLevel());
			putPlayerClass(player, id, trait);
			return false;
		}
		trait.setLevel(trait.getLevel() + amount);
		putPlayerClass(player, id, trait);
		return true;
	}
}
