package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import io.github.cottonmc.skillcheck.container.CharacterSheetContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.container.Container;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SkillCheckNetworking {

	public static final Identifier SYNC_SELECTION = new Identifier(SkillCheck.MOD_ID, "sync_selection");
	public static final Identifier SYNC_LEVELUP = new Identifier(SkillCheck.MOD_ID, "sync_levelup");
	public static final Identifier SYNC_PLAYER_LEVEL = new Identifier(SkillCheck.MOD_ID, "sync_player_level");

	public static void init() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) initClient();
		else initServer();
	}

	public static void initClient() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_PLAYER_LEVEL, (packetContext, packetByteBuf) -> {
			packetContext.getPlayer().experienceLevel = packetByteBuf.readInt();
		});
	}

	public static void initServer() {
		ServerSidePacketRegistry.INSTANCE.register(SYNC_SELECTION, (packetContext, packetByteBuf) -> {
			Container container = packetContext.getPlayer().container;
			if (container instanceof CharacterSheetContainer) {
				int index = packetByteBuf.readInt();
				CharacterSheetContainer table = (CharacterSheetContainer)container;
				table.setCurrentSkill(index);
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(SYNC_LEVELUP, (packetContext, packetByteBuf) -> {
			Identifier id = packetByteBuf.readIdentifier();
			int xpCost = packetByteBuf.readInt();
			if (!packetContext.getPlayer().isCreative()) {
				packetContext.getPlayer().experienceLevel -= xpCost;
				setSyncPlayerLevel(packetContext.getPlayer().experienceLevel, (ServerPlayerEntity)packetContext.getPlayer());
			}
			ClassManager.levelUp(packetContext.getPlayer(), id);
		});
	}

	@Environment(EnvType.CLIENT)
	public static void syncSelection(int index) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(index);
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(new CustomPayloadC2SPacket(SYNC_SELECTION, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void syncLevelup(Identifier id, int xpCost) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(id);
		buf.writeInt(xpCost);
		MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(new CustomPayloadC2SPacket(SYNC_LEVELUP, buf));
	}

	@Environment(EnvType.CLIENT)
	public static void setSyncPlayerLevel(int level, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(level);
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(SYNC_PLAYER_LEVEL, buf));
	}
}