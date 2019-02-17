package io.github.cottonmc.skillworks.mixins;

import com.mojang.authlib.GameProfile;
import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Traits;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/*
	Much code in this class is taken from Wall-Jump, and modified for Yarn, 1.14, and the Gymnast skill.
	The original repository can be found here: https://github.com/genandnic/Wall-Jump
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinGymnistClient extends AbstractClientPlayerEntity {

	@Shadow public Input input;

	private static int airTime;
	private static int jumpCount = 0;
	private static boolean jumpKey = false;

	public MixinGymnistClient(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "updateMovement", at = @At("TAIL"))
	public void gymnistMovement(CallbackInfo ci) {
		if (Traits.has(this, Skillworks.GYMNIST)) {
			// double-jump code
			if (this.onGround) {

				airTime = 0;
				jumpCount = 1;

			} else if (this.input.jumping && !this.abilities.flying) {

				if (!jumpKey && jumpCount < 2 && airTime > 1 && this.getHungerManager().getFoodLevel() > 6) {

					this.method_6043();
					jumpCount++;

					this.fallDistance = 0.0F;

				}

				jumpKey = true;

			} else {

				airTime++;
				jumpKey = false;

			}
		}

	}

}
