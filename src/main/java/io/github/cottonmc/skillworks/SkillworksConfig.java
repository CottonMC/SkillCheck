package io.github.cottonmc.skillworks;

import blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

public class SkillworksConfig {

	@Comment("Remove the requirement for obtaining classes like Weaver or Gymnist to be able to use their skills.")
	public boolean disableClasses = false;

	@Comment("Blocks in this blacklist can not be clung to with the Gymnist class.")
	public List<String> clingBlackList = new ArrayList<>();

	@Comment("If true, clingBlackList will act as a whitelist instead.")
	public boolean invertClngBlackList = false;
}
