package io.github.cottonmc.skillcheck.classes;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.PlayerClassType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleClassType implements PlayerClassType {
	private int maxLevel;
	private List<Text> additionalLines = new ArrayList<>();

	public SimpleClassType(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	@Override
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	public int getNextLevelCost(int currentLevel, PlayerEntity player) {
		if (currentLevel > 3) return 30;
		if (currentLevel == 0) return 5;
		return 10*currentLevel;
	}

	@Override
	public List<Text> getClassDescription() {
		List<Text> lines = new ArrayList<>();
		Identifier id = SkillCheck.PLAYER_CLASS_TYPES.getId(this);
		if (id != null) {
			for (int i = 0; i < 10; i++) {
				String key = "desc.class." + id.getNamespace() + "." + id.getPath() + "." + i;
				if (!I18n.hasTranslation(key)) break;
				lines.add(new TranslatableText(key));
			}
		}
		lines.addAll(additionalLines);
		return lines;
	}

	@Override
	public void addAdditionalDescription(Text... lines) {
		additionalLines.addAll(Arrays.asList(lines));
	}
}
