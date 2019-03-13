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

	public static int getLevel(Entity target, TraitEntry entry) {
		if (Skillworks.config.disableClasses || !Traits.has(target, entry)) return 0;
		Trait trait = Traits.get(target, entry);
		if (trait instanceof ClassTrait) {
			ClassTrait playerClass = (ClassTrait)trait;
			return playerClass.getLevel();
		}
		return 0;
	}

	public static void levelUp(Entity target, TraitEntry entry) {
		levelUp(target, entry, 1);
	}

	public static void levelUp(Entity target, TraitEntry entry, int amount) {
		if (Skillworks.config.disableClasses) return;
		if (!Traits.has(target, entry)) {
			Traits.add(target, entry.generate());
		}
		Trait trait = Traits.get(target, entry);
		if (trait instanceof ClassTrait) {
			ClassTrait entityClass = (ClassTrait) trait;
			entityClass.setLevel(entityClass.getLevel() + amount);
		}

	}
}
