package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.skillcheck.util.ArrowEffects;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ArrowEntity.class)
public abstract class MixinArrow implements ArrowEffects {

	@Accessor("effects")
	public abstract Set<StatusEffectInstance> getEffects();
}
