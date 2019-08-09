package io.github.cottonmc.skillcheck.api.classes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

/**
 * Use CottonRPG instead.
 */
@Deprecated
public class PlayerClass {

	public Identifier id;
	int level = 0;
	int experience = 0;

	public PlayerClass(Identifier id) {
		this.id = id;
	}

	public CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("level", this.level);
		tag.putInt("experience", this.experience);
		return tag;
	}

	public PlayerClass fromNBT(CompoundTag tag) {
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
