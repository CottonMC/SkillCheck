package io.github.cottonmc.skillcheck.mixins;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ArrowEntity.class)
public interface ArrowEffects {

	@Accessor("effects")
	Set<StatusEffectInstance> getEffects();
}
