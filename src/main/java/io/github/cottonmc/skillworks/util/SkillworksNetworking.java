package io.github.cottonmc.skillworks.util;

import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.api.traits.ClassManager;
import io.github.cottonmc.skillworks.container.CharacterSheetContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SkillworksNetworking {

	public static final Identifier SYNC_SELECTION = new Identifier(Skillworks.MOD_ID, "sync_selection");
	public static final Identifier SYNC_LEVELUP = new Identifier(Skillworks.MOD_ID, "sync_levelup");

	public static void init() {
		ServerSidePacketRegistry.INSTANCE.register(SYNC_SELECTION, ((packetContext, packetByteBuf) -> {
			Container container = packetContext.getPlayer().container;
			if (container instanceof CharacterSheetContainer) {
				int index = packetByteBuf.readInt();
				CharacterSheetContainer table = (CharacterSheetContainer)container;
				table.setCurrentSkill(index);
			}
		}));
		ServerSidePacketRegistry.INSTANCE.register(SYNC_LEVELUP, (((packetContext, packetByteBuf) -> {
			Identifier id = packetByteBuf.readIdentifier();
			ClassManager.levelUp(packetContext.getPlayer(), id);
		})));
	}

	@Environment(EnvType.CLIENT)
	public static void syncSelection(int index) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(index);
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(new CustomPayloadC2SPacket(SYNC_SELECTION, buf));
	}

	public static void syncLevelup(Identifier id) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(id);
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(new CustomPayloadC2SPacket(SYNC_LEVELUP, buf));
	}
}