package io.github.cottonmc.skillworks.mixins;

import com.sun.istack.internal.Nullable;
import io.github.cottonmc.skillworks.ArrowEffects;
import io.github.cottonmc.skillworks.Skillworks;
import io.github.cottonmc.skillworks.traits.ClassManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
public abstract class MixinGymnistCommon extends LivingEntity {

	@Shadow @Nullable
	public abstract ItemEntity dropItem(ItemStack stack, boolean fromSelf);

	@Shadow @Final public PlayerInventory inventory;

	protected MixinGymnistCommon(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	public void catchArrow(DamageSource source, float amount, CallbackInfoReturnable ci) {
		if (source.isProjectile() && source.getSource() instanceof ArrowEntity) {
			if (ClassManager.hasLevel(this, Skillworks.GYMNIST, 3)
					&& world.random.nextFloat() < Skillworks.config.arrowCatchChance
					&& canCatchArrow()) {
				ArrowEntity arrow = (ArrowEntity) source.getSource();
				if (!((ArrowEffects)arrow).getEffects().isEmpty()) {
					for (StatusEffectInstance effect : (((ArrowEffects) arrow).getEffects())) {
						this.addPotionEffect(effect);
					}
				}
				if (arrow.isOnFire()) this.setOnFireFor(5);
				ItemStack main = this.getMainHandStack();
				ItemStack off = this.getOffHandStack();
				if (main.isEmpty()) {
					this.setStackInHand(Hand.MAIN, new ItemStack(Items.ARROW));
					this.swingHand(Hand.MAIN);
				} else if (main.getItem() == Items.ARROW && main.getAmount() < Items.ARROW.getMaxAmount()) {
					main.addAmount(1);
					this.swingHand(Hand.MAIN);
				} else if (off.isEmpty()) {
					this.setStackInHand(Hand.OFF, new ItemStack(Items.ARROW));
					this.swingHand(Hand.OFF);
				} else if (off.getItem() == Items.ARROW && off.getAmount() < Items.ARROW.getMaxAmount()) {
					off.addAmount(1);
					this.swingHand(Hand.OFF);
				} else this.dropItem(new ItemStack(Items.ARROW), false);

				ci.setReturnValue(true);
			}
		}
	}

	private boolean canCatchArrow() {
		return this.getMainHandStack().isEmpty() || this.getOffHandStack().isEmpty()
				|| (this.getMainHandStack().getItem() == Items.ARROW && this.getMainHandStack().getAmount() < Items.ARROW.getMaxAmount())
				|| (this.getOffHandStack().getItem() == Items.ARROW && this.getOffHandStack().getAmount() < Items.ARROW.getMaxAmount());
	}


}
