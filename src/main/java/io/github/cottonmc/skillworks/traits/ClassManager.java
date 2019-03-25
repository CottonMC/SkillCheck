package io.github.cottonmc.skillworks.traits;

import com.raphydaphy.crochet.data.PlayerData;
import io.github.cottonmc.skillworks.Skillworks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;


public class ClassManager {

	public static CompoundTag getClasses(PlayerEntity player) {
		CompoundTag data =  PlayerData.get(player);
		if (!data.containsKey("Classes")) {
			data.put("Classes", new CompoundTag());
			PlayerData.markDirty(player);
		}
		return data.getCompound("Classes");
	}

	public static ClassTrait getClass(PlayerEntity player, Identifier id) {
		CompoundTag classes = getClasses(player);
		if (!classes.containsKey(id.toString())) return null;
		ClassTrait trait = new ClassTrait(id);
		trait.fromNBT(classes.getCompound(id.toString()));
		return trait;
	}

	public static boolean hasClass(PlayerEntity player, Identifier id) {
		if (Skillworks.config.disableClasses) return true;
		return (getClasses(player).containsKey(id.toString()));
	}

	public static void addClass(PlayerEntity player, Identifier id) {
		putClass(player, id, new ClassTrait(id));
	}

	public static void putClass(PlayerEntity player, Identifier id, ClassTrait trait) {
		CompoundTag classes = getClasses(player);
		if (classes.containsKey(id.toString())) classes.remove(id.toString());
		classes.put(id.toString(), trait.toNBT());
		PlayerData.markDirty(player);
	}

	public static boolean hasLevel(PlayerEntity player, Identifier id, int level) {
		if (Skillworks.config.disableClasses) return true;
		ClassTrait trait = getClass(player, id);
		if (trait == null) return false;
		return trait.getLevel() >= level;
	}

	public static int getLevel(PlayerEntity player, Identifier id) {
		if (Skillworks.config.disableClasses) return 0;
		if (!hasClass(player, id)) return 0;
		ClassTrait trait = getClass(player, id);
		return trait.getLevel();
	}

	public static void levelUp(PlayerEntity player, Identifier id) {
		levelUp(player, id, 1);
	}

	public static void levelUp(PlayerEntity player, Identifier id, int amount) {
		if (Skillworks.config.disableClasses) return;

		if (!hasClass(player, id)) addClass(player, id);
		ClassTrait trait = getClass(player, id);
		trait.setLevel(trait.getLevel() + amount);
		putClass(player, id, trait);
	}
}
