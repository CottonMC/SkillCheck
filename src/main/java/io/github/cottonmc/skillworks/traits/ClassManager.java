package io.github.cottonmc.skillworks.traits;

import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Trait;
import me.elucent.earlgray.api.TraitEntry;
import me.elucent.earlgray.api.Traits;
import net.minecraft.entity.Entity;


public class ClassManager {

	public static boolean hasClass(Entity target, TraitEntry trait) {
		return Skillworks.config.disableClasses || Traits.has(target, trait);
	}

	public static boolean hasLevel(Entity target, TraitEntry entry, int level) {
		if (Skillworks.config.disableClasses) return true;
		Trait trait = Traits.get(target, entry);
		if (trait instanceof ClassTrait) {
			ClassTrait playerClass = (ClassTrait)trait;
			return playerClass.getLevel() >= level;
		}
		return false;
	}
}
