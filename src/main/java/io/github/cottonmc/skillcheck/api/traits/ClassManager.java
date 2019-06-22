package io.github.cottonmc.skillcheck.api.traits;

import com.raphydaphy.crochet.data.PlayerData;
import io.github.cottonmc.skillcheck.SkillCheck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ClassManager {

	private static Map<Identifier, Integer> CLASSES = new HashMap<>();

	/**
	 * Add a class to the SkillCheck registry.
	 * @param id the ID of the class to add
	 * @param maxLevel the max level the class can have
	 * @return the registered identifier
	 */
	public static Identifier registerClass(Identifier id, int maxLevel) {
		if (!CLASSES.containsKey(id)) CLASSES.put(id, maxLevel);
		return id;
	}

	/**
	 * Query the max level of a class.
	 * @param id the class to check
	 * @return the max level of the class, or 0 if the class doesn't exist
	 */
	public static int getClassMaxLevel(Identifier id) {
		return CLASSES.getOrDefault(id, 0);
	}

	/**
	 * @return a set of all class identifiers
	 */
	public static Set<Identifier> getClasses() {
		return CLASSES.keySet();
	}

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
	public static ClassTrait getPlayerClass(PlayerEntity player, Identifier id) {
		CompoundTag classes = getPlayerClasses(player);
		if (!classes.containsKey(id.toString())) return null;
		ClassTrait trait = new ClassTrait(id);
		trait.fromNBT(classes.getCompound(id.toString()));
		return trait;
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
		putPlayerClass(player, id, new ClassTrait(id));
	}

	/**
	 * Set a player's class info to a specific value.
	 * @param player the player to set on
	 * @param id the ID of the class to set
	 * @param trait the values for the class
	 */
	public static void putPlayerClass(PlayerEntity player, Identifier id, ClassTrait trait) {
		CompoundTag classes = getPlayerClasses(player);
		if (classes.containsKey(id.toString())) classes.remove(id.toString());
		classes.put(id.toString(), trait.toNBT());
		PlayerData.markDirty(player);
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
		ClassTrait trait = getPlayerClass(player, id);
		if (trait == null) return false;
		return trait.getLevel() >= level;
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
		ClassTrait trait = getPlayerClass(player, id);
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
		ClassTrait trait = getPlayerClass(player, id);
		int levelTo = trait.getLevel() + amount;
		if (levelTo > CLASSES.get(id)) {
			trait.setLevel(CLASSES.get(id));
			putPlayerClass(player, id, trait);
			return false;
		}
		trait.setLevel(trait.getLevel() + amount);
		putPlayerClass(player, id, trait);
		return true;
	}
}
