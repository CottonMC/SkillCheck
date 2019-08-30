package io.github.cottonmc.skillcheck.mixins;

import com.raphydaphy.crochet.data.DataHolder;
import com.raphydaphy.crochet.data.PlayerData;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClassEntry;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResources;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.api.classes.ClassManager;
import io.github.cottonmc.skillcheck.api.classes.PlayerClass;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	private void upgradeSkillCheckClasses(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		//port over classes from SkillCheck 1.x
		CompoundTag tag = ((DataHolder)player).getAllAdditionalData();
		CharacterClasses classes = CharacterData.get(player).getClasses();
		if (tag.containsKey("skillcheck")) {
			for (Identifier id : SkillCheck.getCharSheetClasses()) {
				if (ClassManager.hasClass(player, id) && !classes.has(id)) {
					PlayerClass old = ClassManager.getPlayerClass(player, id);
					CharacterClassEntry entry = new CharacterClassEntry(id);
					entry.setLevel(old.getLevel());
					entry.setExperience(old.getExperience());
					classes.giveIfAbsent(entry);
				}
			}
			((DataHolder)player).getAllAdditionalData().remove("skillcheck");
			PlayerData.markDirty(player);
		}

		//add stamina if needed
		if (SkillCheck.config.useStamina) {
			CharacterResources resources = CharacterData.get(player).getResources();
			if (classes.has(SkillCheck.THIEF) && !resources.has(SkillCheck.STAMINA)) {
				int thief = classes.get(SkillCheck.THIEF).getLevel();
				resources.giveIfAbsent(new CharacterResourceEntry(new Identifier(SkillCheck.MOD_ID, "stamina")));
				CharacterResourceEntry entry = resources.get(SkillCheck.STAMINA);
				entry.setMax(10*thief);
				entry.setCurrent(10*thief);
			}
		}
	}
}
