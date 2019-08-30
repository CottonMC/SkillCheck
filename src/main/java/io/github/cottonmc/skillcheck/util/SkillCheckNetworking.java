package io.github.cottonmc.skillcheck.util;

import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResources;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.container.CharacterSheetContainer;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class SkillCheckNetworking {

	public static final Identifier SYNC_SELECTION = new Identifier(SkillCheck.MOD_ID, "sync_selection");
	public static final Identifier SYNC_LEVELUP = new Identifier(SkillCheck.MOD_ID, "sync_levelup");
	public static final Identifier SYNC_PLAYER_LEVEL = new Identifier(SkillCheck.MOD_ID, "sync_player_level");

	public static final Identifier CLEAR_FALL = new Identifier(SkillCheck.MOD_ID, "clear_fall");
	public static final Identifier CONSUME_STAMINA = new Identifier(SkillCheck.MOD_ID, "consume_stamina");

	public static void init() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) initClient();
		initServer();
	}

	public static void initClient() {
		ClientSidePacketRegistry.INSTANCE.register(SYNC_PLAYER_LEVEL, (packetContext, packetByteBuf) ->
				packetContext.getPlayer().experienceLevel = packetByteBuf.readInt());
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
			if (((CharacterSheetContainer)packetContext.getPlayer().container).canLevelUp()) {
				Identifier id = packetByteBuf.readIdentifier();
				CharacterClasses classes = CharacterData.get(packetContext.getPlayer()).getClasses();
				classes.giveIfAbsent(new CharacterClassEntry(id));
				int currentLevel = classes.get(id).getLevel();
				CottonRPG.CLASSES.get(id).applyLevelUp(currentLevel, packetContext.getPlayer());
				ClassUtils.levelUp(packetContext.getPlayer(), id, 1);
			}
		});
		ServerSidePacketRegistry.INSTANCE.register(CLEAR_FALL, (packetContext, packetByteBuf) -> {
			PlayerEntity player = packetContext.getPlayer();
			player.fallDistance = 0;
		});
		ServerSidePacketRegistry.INSTANCE.register(CONSUME_STAMINA, ((packetContext, packetByteBuf) -> {
			CharacterResources resources = CharacterData.get(packetContext.getPlayer()).getResources();
			if (resources.has(SkillCheck.STAMINA)) {
				CharacterResourceEntry entry = resources.get(SkillCheck.STAMINA);
				entry.setCurrent(entry.getCurrent() - packetByteBuf.readInt());
				//TODO: kicking the player instead of crashing the server is a good idea
			} else throw new IllegalStateException("");
		}));
	}

	public static void syncPlayerXP(int level, ServerPlayerEntity player) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(level);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new CustomPayloadS2CPacket(SYNC_PLAYER_LEVEL, buf));
	}

	public static void syncSelection(int index) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(index);
		ClientSidePacketRegistry.INSTANCE.sendToServer(new CustomPayloadC2SPacket(SYNC_SELECTION, buf));
	}

	public static void syncLevelup(Identifier id) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(id);
		ClientSidePacketRegistry.INSTANCE.sendToServer(new CustomPayloadC2SPacket(SYNC_LEVELUP, buf));
	}

	public static void clearFall() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		ClientSidePacketRegistry.INSTANCE.sendToServer(new CustomPayloadC2SPacket(CLEAR_FALL, buf));
	}

	public static void consumeStamina(int amount) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(amount);
		ClientSidePacketRegistry.INSTANCE.sendToServer(new CustomPayloadC2SPacket(CONSUME_STAMINA, buf));
	}
}