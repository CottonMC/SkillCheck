package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.util.ArrowEffects;
import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PlayerEntity.class)
public abstract class MixinThiefCommon extends LivingEntity {

	@Shadow @Nullable
	public abstract ItemEntity dropItem(ItemStack stack, boolean fromSelf);

	@Shadow public abstract void addChatMessage(Component textComponent_1, boolean boolean_1);

	protected MixinThiefCommon(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
	}

	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	public void catchArrow(DamageSource source, float amount, CallbackInfoReturnable ci) {
		if (source.isProjectile() && source.getSource() instanceof ArrowEntity) {
			if (ClassManager.hasLevel((PlayerEntity)(Object)this, SkillCheck.THIEF, 3)
					&& canCatchArrow()) {
				RollResult roll = Dice.roll("1d20+"+ClassManager.getLevel((PlayerEntity)(Object)this, SkillCheck.THIEF));
				if (SkillCheck.config.showDiceRolls) {
					if (roll.isCritFail()) ((PlayerEntity)(Object)this).addChatMessage(new TranslatableComponent("msg.skillcheck.roll.fail", roll.getFormattedNaturals()), false);
					else ((PlayerEntity)(Object)this).addChatMessage(new TranslatableComponent("msg.skillcheck.roll.result", roll.getTotal(), roll.getFormattedNaturals()), false);
				}
				if (roll.isCritFail()) {
					this.addPotionEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 2, 1));
				} else if (roll.getTotal() >= SkillCheck.config.arrowCatchRoll) {
					ArrowEntity arrow = (ArrowEntity) source.getSource();
					if (!((ArrowEffects) arrow).getEffects().isEmpty()) {
						for (StatusEffectInstance effect : (((ArrowEffects) arrow).getEffects())) {
							this.addPotionEffect(effect);
						}
					}
					if (arrow.isOnFire()) this.setOnFireFor(5);
					ItemStack main = this.getMainHandStack();
					ItemStack off = this.getOffHandStack();
					if (main.isEmpty()) {
						this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.ARROW));
						this.swingHand(Hand.MAIN_HAND);
					} else if (main.getItem() == Items.ARROW && main.getAmount() < Items.ARROW.getMaxAmount()) {
						main.addAmount(1);
						this.swingHand(Hand.MAIN_HAND);
					} else if (off.isEmpty()) {
						this.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.ARROW));
						this.swingHand(Hand.OFF_HAND);
					} else if (off.getItem() == Items.ARROW && off.getAmount() < Items.ARROW.getMaxAmount()) {
						off.addAmount(1);
						this.swingHand(Hand.OFF_HAND);
					} else this.dropItem(new ItemStack(Items.ARROW), false);

					ci.setReturnValue(true);
				}
			}
		}
	}

	private boolean canCatchArrow() {
		return this.getMainHandStack().isEmpty() || this.getOffHandStack().isEmpty()
				|| (this.getMainHandStack().getItem() == Items.ARROW && this.getMainHandStack().getAmount() < Items.ARROW.getMaxAmount())
				|| (this.getOffHandStack().getItem() == Items.ARROW && this.getOffHandStack().getAmount() < Items.ARROW.getMaxAmount());
	}

}
