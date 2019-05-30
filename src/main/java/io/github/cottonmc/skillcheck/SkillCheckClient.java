package io.github.cottonmc.skillcheck;

import io.github.cottonmc.skillcheck.container.CharacterSheetScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

public class SkillCheckClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(SkillCheck.CHARACTER_SHEET_CONTAINER, (syncId, id, player, buf) -> new CharacterSheetScreen(syncId, player));
	}
}
