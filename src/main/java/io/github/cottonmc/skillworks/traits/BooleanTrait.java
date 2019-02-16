package io.github.cottonmc.skillworks.traits;

import me.elucent.earlgray.api.Trait;
import net.minecraft.nbt.CompoundTag;

public class BooleanTrait extends Trait {
	boolean value = false;

	public BooleanTrait() {

	}

	public CompoundTag write(CompoundTag tag) {
		tag.putBoolean("value", this.value);
		return tag;
	}

	public Trait read(CompoundTag tag) {
		this.value = tag.getBoolean("value");
		return this;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean b) {
		this.value = b;
	}
}
