package io.github.cottonmc.skillworks.util;

import blue.endless.jankson.Comment;

public class SkillworksConfig {

	@Comment("Remove the requirement for obtaining classes to be able to use their skills.\n" +
			"This will not apply to skills like Brawler's damage-boost, which scale according to level.")
	public boolean disableClasses = false;

	@Comment("If true, the \"skillworks:slippery\" tag will act as a whitelist instead.")
	public boolean invertSlipperyTag = false;

	@Comment("The likelihood of catching an arrow with the Thief skill, as a D&D-style dice roll.\n" +
			"The formula is 1d20+<thief level>.")
	public int arrowCatchRoll = 4;

	@Comment("The likelihood of successfully stealing armor from a mob with the Thief skill, as a D&D-style dice roll.\n"
			+ "The formula is 1d20+<thief level>."
			+ "A roll four greater than this value will succeed without alerting the mob.")
	public int stealArmorRoll = 8;
}
