package io.github.cottonmc.skillworks.traits;

import me.elucent.earlgray.api.Trait;
import net.minecraft.nbt.CompoundTag;

public class FloatTrait extends Trait {
	float value = 0;

	public FloatTrait() {

	}

	public CompoundTag write(CompoundTag tag) {
		tag.putFloat("value", this.value);
		return tag;
	}

	public Trait read(CompoundTag tag) {
		this.value = tag.getFloat("value");
		return this;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float f) {
		this.value = f;
	}
}
