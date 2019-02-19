package io.github.cottonmc.skillworks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class PlayerVectorHelper {

	public static float forwardVelocity(PlayerEntity player) {
		return playerHorizontalMotion(player).x;

	}

	public static float strafingVelocity(PlayerEntity player) {
		return playerHorizontalMotion(player).y;
	}

	public static Vec2f playerHorizontalMotion(PlayerEntity player) {
		float yawSin = MathHelper.sin(player.yaw * 0.017453292F) / 5; // 0.017453292F is approx. pi/180, converts degrees to radians
		float yawCos = MathHelper.cos(player.yaw * 0.017453292F) / 5; // equivalent to (sin(θ)/5) and (cos(θ)/5)

		// velocityX = strafe * yawCos - forward * yawSin;
		// strafe * yawCos = velocityX + (forward * yawSin)
		// forward * yawSin = -velocityX + (strafe * yawCos)

		// velocityZ = forward * yawCos + strafe * yawSin
		// strafe * yawSin = velocityZ - (forward * yawCos)
		// forward * yawCos = -velocityZ - (strafe * yawSin)

		// forward = (-velocityX + (strafe * yawCos)) / yawSin
		// forward = (-velocityZ - (strafe * yawSin)) / yawCos
		// (-velocityX + (strafe * yawCos)) / yawSin = (-velocityZ - (strafe * yawSin)) / yawCos


		// strafe = (velocityX + (forward * yawSin)) / yawCos
		// strafe = (velocityZ - (forward * yawCos)) / yawSin
		// (velocityX + (forward * yawSin)) / yawCos = (velocityZ - (forward * yawCos)) / yawSin

	}

}
