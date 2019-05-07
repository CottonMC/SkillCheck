package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.block.ScribingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

public class SkillworksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(Skillworks.SCRIBING_CONTAINER, (syncId, id, player, buf) -> new ScribingTableScreen(syncId, player));
	}
}
