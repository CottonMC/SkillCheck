package io.github.cottonmc.skillcheck.api.traits;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * Flyweight for interacting with classes.
 * A ClassTrait stores the Identifier of the class, the player's level of the class, and how much XP in the class the player has.
 * Experience is not used in the base mod, and is separate from vanilla XP.
 * For most cases, you likely want to use {@link ClassManager}.
 * However, you may want to implement experience on your classes, so feel free to access this then.
 */
public class ClassTrait {

	public Identifier id;
	int level = 0;
	int experience = 0;

	public ClassTrait(Identifier id) {
		this.id = id;
	}

	public CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("level", this.level);
		tag.putInt("experience", this.experience);
		return tag;
	}

	public ClassTrait fromNBT(CompoundTag tag) {
		this.level = tag.getInt("level");
		this.experience = tag.getInt("experience");
		return this;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int i) {
		this.level = i;
	}

	public int getExperience() {
		return this.experience;
	}

	public void setExperience(int i) {
		this.experience = i;
	}
}
