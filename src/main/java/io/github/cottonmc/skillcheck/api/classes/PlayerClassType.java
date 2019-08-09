package io.github.cottonmc.skillcheck.api.classes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Use CottonRPG instead.
 */
@Deprecated
public interface PlayerClassType {
	/**
	 * @return The max level you can obtain with this class.
	 */
	int getMaxLevel();

	/**
	 * @param currentLevel The curent class level of the player seeking to level up.
	 * @param player The player seeking to level up.
	 * @return How much their next level will cost.
	 */
	int getNextLevelCost(int currentLevel, PlayerEntity player);

	/**
	 * @return The lines of description to display in the character sheet. Will be auto-wrapped.
	 */
	@Environment(EnvType.CLIENT)
	List<Text> getClassDescription();

	/**
	 * Allow other mods to add description if they use your player class.
	 * @param lines The lines to add.
	 */
	void addAdditionalDescription(Text... lines);
}
