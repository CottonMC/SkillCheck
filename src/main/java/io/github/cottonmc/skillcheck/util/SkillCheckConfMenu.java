package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

import java.util.function.Function;

public class SkillCheckConfMenu implements ModMenuApi {
	@Override
	public String getModId() {
		return SkillCheck.MOD_ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return (prevScreen) -> {
			ConfigBuilder builder = ConfigBuilder.create();
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();
			ConfigCategory gameplay = builder.getOrCreateCategory(new LiteralText("General Settings"));
			gameplay.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Disable class requirements"), SkillCheck.config.disableClasses).setDefaultValue(false).setSaveConsumer(b -> SkillCheck.config.disableClasses = b).build());
			gameplay.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Invert slippery tag"), SkillCheck.config.invertSlipperyTag).setDefaultValue(false).setSaveConsumer(b -> SkillCheck.config.invertSlipperyTag = b).build());
			gameplay.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Show dice rolls in chat"), SkillCheck.config.showDiceRolls).setDefaultValue(false).setSaveConsumer(b -> SkillCheck.config.showDiceRolls = b).build());
			gameplay.addEntry(entryBuilder.startBooleanToggle(new LiteralText("Enable critical failures"), SkillCheck.config.haveCriticalFailures).setDefaultValue(true).setSaveConsumer(b -> SkillCheck.config.haveCriticalFailures = b).build());
			ConfigCategory dice = builder.getOrCreateCategory(new LiteralText("Dice Roll Requirements"));
			dice.addEntry(entryBuilder.startIntSlider(new LiteralText("Arrow catch (1d20+thief)"), SkillCheck.config.arrowCatchRoll, 1, 26).setDefaultValue(15).setSaveConsumer(i -> SkillCheck.config.arrowCatchRoll = i).build());
			dice.addEntry(entryBuilder.startIntSlider(new LiteralText("Armor theft (1d20+thief)"), SkillCheck.config.stealArmorRoll, 1, 26).setDefaultValue(8).setSaveConsumer(i -> SkillCheck.config.stealArmorRoll = i).build());
			dice.addEntry(entryBuilder.startIntSlider(new LiteralText("Silent armor theft (1d20+thief)"), SkillCheck.config.silentStealArmorRoll, 1, 26).setDefaultValue(12).setSaveConsumer(i -> SkillCheck.config.silentStealArmorRoll = i).build());
			dice.addEntry(entryBuilder.startIntSlider(new LiteralText("Enemy weaken (1d20+brawler)"), SkillCheck.config.weakenEnemyRoll, 1, 31).setDefaultValue(12).setSaveConsumer(i -> SkillCheck.config.weakenEnemyRoll = i).build());
			return builder.setSavingRunnable(() -> ConfigManager.saveConfig(SkillCheck.config)).build();
		};
	}
}
