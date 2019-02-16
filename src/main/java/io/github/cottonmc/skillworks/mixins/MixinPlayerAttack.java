package io.github.cottonmc.skillworks.mixins;

import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Traits;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerAttack extends LivingEntity {

	protected MixinPlayerAttack(EntityType<?> entityType_1, World world_1) {
		super(entityType_1, world_1);
	}

	@ModifyVariable(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;method_7261(F)F"), ordinal = 0)
	public float addPlayerUnarmedDamage(float orig) {
		if (((PlayerEntity)(Object)this).getMainHandStack().isEmpty()) {
			return orig + Traits.get((PlayerEntity)(Object)this, Skillworks.FISTICUFFS).getValue();
		}
		return orig;
	}
}