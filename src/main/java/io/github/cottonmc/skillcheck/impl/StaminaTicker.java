package io.github.cottonmc.skillcheck.impl;

import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.resource.Ticker;
import net.minecraft.nbt.CompoundTag;

public class StaminaTicker implements Ticker {
	private int ticker = 0;
	private int tickTo;
	private int delay;
	private int cooldown = 0;
	private long lastEntryValue = 0;
	private transient boolean dirty = false;

	public StaminaTicker(int tickTo, int delay) {
		this.tickTo = tickTo;
		this.delay = delay;
	}

	public boolean shouldTick(CharacterResourceEntry entry) {
		if (entry.getCurrent() >= entry.getMax()) return false;
		if (lastEntryValue > entry.getCurrent()) {
			lastEntryValue = entry.getCurrent();
			cooldown = delay;
			this.markDirty();
			return false;
		}
		if (cooldown > 0) {
			cooldown--;
			markDirty();
		}
		return cooldown <= 0;
	}

	public void tick(CharacterResourceEntry entry) {
		if (shouldTick(entry)) {
			if (this.ticker++ >= this.tickTo) {
				this.ticker = 0;
				entry.setCurrent(entry.getCurrent() + 1);
			}

			this.markDirty();
		}

	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("Ticks", this.ticker);
		tag.putInt("Cooldown", this.cooldown);
		tag.putLong("LastEntryValue", this.lastEntryValue);
		return tag;
	}

	public StaminaTicker fromTag(CompoundTag tag) {
		this.ticker = tag.getInt("Ticks");
		this.cooldown = tag.getInt("Cooldown");
		this.lastEntryValue = tag.getLong("LastEntryValue");
		return this;
	}

	public void markDirty() {
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public void clearDirty() {
		this.dirty = false;
	}
}
