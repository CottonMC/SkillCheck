package io.github.cottonmc.skillcheck.impl;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClass;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillCheckCharacterClass implements CharacterClass {
	private int maxLevel;
	private List<Text> additionalLines = new ArrayList<>();

	public SkillCheckCharacterClass(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getNextLevelCost(int currentLevel) {
		//TODO: make cheaper?
		if (currentLevel > 3) {
			return 30;
		} else {
			return currentLevel == 0 ? 5 : 10 * currentLevel;
		}
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	public boolean canLevelUp(int currentLevel, PlayerEntity player) {
		return player.experienceLevel >= getNextLevelCost(currentLevel);
	}

	@Override
	public void applyLevelUp(int currentLevel, PlayerEntity player) {
		if (!player.isCreative() && player instanceof ServerPlayerEntity) {
			player.experienceLevel -= getNextLevelCost(currentLevel);
			SkillCheckNetworking.syncPlayerXP(player.experienceLevel, (ServerPlayerEntity) player);
		}
	}

	public List<Text> getDescription() {
		List<Text> lines = new ArrayList<>();
		Identifier id = CottonRPG.CLASSES.getId(this);
		if (id != null) {
			for(int i = 0; i < 10; ++i) {
				String key = "desc.class." + id.getNamespace() + "." + id.getPath() + "." + i;
				if (!I18n.hasTranslation(key)) {
					break;
				}

				lines.add(new TranslatableText(key, new Object[0]));
			}
		}

		lines.addAll(this.additionalLines);
		return lines;
	}

	public void addAdditionalDescription(Text... lines) {
		this.additionalLines.addAll(Arrays.asList(lines));
	}

	public Text getLevelRequirement(int currentLevel, PlayerEntity player) {
		return new TranslatableText("text.skillcheck.cost", getNextLevelCost(currentLevel));
	}

}
