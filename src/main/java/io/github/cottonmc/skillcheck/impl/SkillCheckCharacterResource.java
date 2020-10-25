package io.github.cottonmc.skillcheck.impl;

import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.resource.SimpleCharacterResource;
import io.github.cottonmc.cottonrpg.data.resource.Ticker;

public class SkillCheckCharacterResource extends SimpleCharacterResource {
	private int ticksPerUnit;

	public SkillCheckCharacterResource(int defaultValue, int maxValue, int unitsPerBar, int ticksPerUnit, int color, ResourceVisibility vis) {
		super(defaultValue, maxValue, unitsPerBar, ticksPerUnit, color, vis);
		this.ticksPerUnit = ticksPerUnit;
	}

	@Override
	public Ticker makeTicker(CharacterResourceEntry entry) {
		return new StaminaTicker(this.ticksPerUnit, 20);
	}
}
