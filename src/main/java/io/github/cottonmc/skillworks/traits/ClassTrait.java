package io.github.cottonmc.skillworks.traits;

import me.elucent.earlgray.api.Trait;
import net.minecraft.nbt.CompoundTag;

public class ClassTrait extends Trait {
	int level = 0;
	int experience = 0;

	public ClassTrait() {

	}

	public CompoundTag write(CompoundTag tag) {
		tag.putInt("level", this.level);
		tag.putInt("experience", this.experience);
		return tag;
	}

	public Trait read(CompoundTag tag) {
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
