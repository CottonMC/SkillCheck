package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.BooleanListEntry;
import me.shedaniel.cloth.gui.entries.IntegerSliderEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class SkillCheckConfMenu implements ModMenuApi {
	@Override
	public String getModId() {
		return SkillCheck.MOD_ID;
	}

	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return (prevScreen) -> {
			ClothConfigScreen.Builder builder = new ClothConfigScreen.Builder(MinecraftClient.getInstance().currentScreen, "SkillCheck Config", null);
			builder.addCategories("General Settings", "Dice Roll Requirements");
			ConfigScreenBuilder.CategoryBuilder gameplay = builder.getCategory("General Settings");
			gameplay.addOption(new BooleanListEntry("Disable class requirements", SkillCheck.config.disableClasses, "text.cloth-config.reset_value", () -> false, null));
			gameplay.addOption(new BooleanListEntry("Invert slippery tag", SkillCheck.config.invertSlipperyTag, "text.cloth-config.reset_value", () -> false, null));
			gameplay.addOption(new BooleanListEntry("Show dice rolls in chat", SkillCheck.config.showDiceRolls, "text.cloth-config.reset_value", () -> false, null));
			gameplay.addOption(new BooleanListEntry("Enable critical failures", SkillCheck.config.haveCriticalFailures, "text.cloth-config.reset_value", () -> true, null));
			ConfigScreenBuilder.CategoryBuilder dice = builder.getCategory("Dice Roll Requirements");
			dice.addOption(new IntegerSliderEntry("Arrow catch (1d20+thief)", 1, 26, SkillCheck.config.arrowCatchRoll, "text.cloth-config.reset_value", () -> 15, null));
			dice.addOption(new IntegerSliderEntry("Armor theft (1d20+thief)", 1, 26, SkillCheck.config.stealArmorRoll, "text.cloth-config.reset_value", () -> 8, null));
			dice.addOption(new IntegerSliderEntry("Silent armor theft (1d20+thief)", 1, 26, SkillCheck.config.silentStealArmorRoll, "text.cloth-config.reset_value", () -> 12, null));
			dice.addOption(new IntegerSliderEntry("Enemy weaken (1d20+brawler)", 1, 31, SkillCheck.config.weakenEnemyRoll, "text.cloth-config.reset_value", () -> 12, null));
			return builder.build();
		};
	}
}
