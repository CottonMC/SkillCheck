package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.cotton.config.annotations.ConfigFile;
import blue.endless.jankson.Comment;

@ConfigFile(name = "SkillCheck")
public class SkillCheckConfig {

	@Comment("Remove the requirement for obtaining classes to be able to use their skills.\n" +
			"This will not apply to skills like Brawler's damage-boost, which scale according to level.")
	public boolean disableClasses = false;

	@Comment("If true, the \"skillcheck:slippery\" tag will act as a whitelist of wall-cligable blocks instead.")
	public boolean invertSlipperyTag = false;

	@Comment("Send a player's dice rolls to their chat.")
	public boolean showDiceRolls = false;

	@Comment("Whether critical failures should happen if any die rolls a 1.")
	public boolean haveCriticalFailures = true;

	@Comment("The minimum dice roll for catching an arrow with the Thief skill.\n" +
			"The formula is 1d20+<thief level>.")
	public int arrowCatchRoll = 15;

	@Comment("The minimum dice roll for successfully stealing armor from a mob with the Thief skill,\n"
			+ "but alerting the mob to your presence.\n"
			+ "The formula is 1d20+<thief level>.")
	public int stealArmorRoll = 8;

	@Comment("The minimum dice roll for successfully stealing armor from a mob with the Thief skill,\n"
			+ "without alerting the mob.\n"
			+ "The formula is 1d20+<thief level>.")
	public int silentStealArmorRoll = 12;

	@Comment("The minimum dice roll for weakening an unarmored enemy with the Brawler skill.\n" +
			"The formula is 1d20+<brawler level>.")
	public int weakenEnemyRoll = 12;
}
