package io.github.cottonmc.skillworks;

import blue.endless.jankson.Comment;

public class SkillworksConfig {

	@Comment("Remove the requirement for obtaining classes like Weaver or Gymnist to be able to use their skills.")
	public boolean disableClasses = false;

	@Comment("If true, the \"skillworks:slippery\" tag will act as a whitelist instead.")
	public boolean invertSlipperyTag = false;

	@Comment("The likelihood of catching an arrow with the Gymnist skill, as a percentage between 0 and 1.")
	public double arrowCatchChance = 0.33;
}
