package io.github.cottonmc.skillcheck.mixins;

import com.mojang.authlib.GameProfile;
import io.github.cottonmc.cottonrpg.data.CharacterData;
import io.github.cottonmc.cottonrpg.data.clazz.CharacterClasses;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResourceEntry;
import io.github.cottonmc.cottonrpg.data.resource.CharacterResources;
import io.github.cottonmc.skillcheck.SkillCheck;
import io.github.cottonmc.skillcheck.util.ClassUtils;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import io.github.cottonmc.skillcheck.util.VoxelShapeGetter;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

import static net.minecraft.util.math.Direction.*;

/**
 * NOTICE: Much code in this class is taken from Wall-Jump by Genandnic, and modified for Yarn, 1.14, and the Gymnast skill.
 * The original repository can be found here: https://github.com/genandnic/Wall-Jump
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

	@Shadow public Input input;

	//Wall-Jump config default values
	@Unique
	private static final float wallJumpHeight = 0.8f;
	@Unique
	private static final int wallSlideDelay = 15;
	@Unique
	private static final double wallSlideSpeed = 0.1;

	//Wall-Jump cling/jump fields
	private static int clingTime;
	private static int keyTimer;
	private static double clingX, clingZ;
	private static Direction clingDirection = UP;
	private static Direction lastDirection = DOWN;
	private static Direction clingDirection2 = UP;
	private static Direction lastDirection2 = DOWN;
	private static double lastJumpY = Double.MAX_VALUE;

	//Wall-Jump double-jump fields
	private static int airTime;
	private static int jumpCount = 0;
	private static boolean jumpKey = false;

	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void thiefMovement(CallbackInfo ci) {
		CharacterClasses classes = CharacterData.get(this).getClasses();

		// wall-cling/wall-jump code from Wall-Jump
		if (classes.has(SkillCheck.THIEF)) this.handleWallJump();

		// double-jump code from Wall-Jump
		if (ClassUtils.hasLevel(classes, SkillCheck.THIEF, 2)) this.handleDoubleJump();

	}

	private void handleWallJump() {

		if (this.input.sneaking) keyTimer++;
		else keyTimer = 0;
		CharacterResources resources = CharacterData.get(this).getResources();

		if (this.onGround || this.abilities.flying) {

			clingTime = 0;
			clingDirection = UP;
			clingDirection2 = UP;
			lastDirection = DOWN;
			lastDirection2 = DOWN;

		} else if (clingTime > 0) {

			BlockPos wall = getWallPos(this);

			if (keyTimer == 0 || this.onGround || !nearWall(this, 0.2)) {

				clingTime = 0;
				if ((this.forwardSpeed != 0 || this.sidewaysSpeed != 0) && (tryUseStamina(resources, 5) || !SkillCheck.config.useStamina) && nearWall(this, 0.5)) {

					lastDirection = clingDirection;
					lastDirection2 = clingDirection2;
					lastJumpY = this.getPos().getY() - 2.0;

					playBreakSound(this, wall);
					spawnWallParticle(this, wall);
					wallJump(this, wallJumpHeight, this.sidewaysSpeed, this.forwardSpeed);
				}

			} else {

				this.updatePosition(clingX, this.getPos().getY(), clingZ);
				Vec3d previousVelocity = this.getVelocity();
				SkillCheckNetworking.clearFall();
				double velY = previousVelocity.y;

				if (previousVelocity.y < -0.5) {

					velY = velY + 0.25;
					spawnWallParticle(this, wall);

				} else {

					if (!tryUseStamina(resources, 1)) clingTime++;
					if ((clingTime > wallSlideDelay)) {

						velY = -wallSlideSpeed;
						spawnWallParticle(this, wall);

					} else {

						velY = 0.0;

					}

				}
				this.setVelocity(0, velY, 0);

			}

		} else {

			clingTime--;
			if (keyTimer > 0 && (keyTimer < 5 || clingTime < -15) && canWallCling(this)) {
				Vec3d previousVelocity = this.getVelocity();
				double velY = previousVelocity.y;
				if (velY > -0.75) velY = 0.0;
				this.setVelocity(0, velY, 0);

				clingTime = 1;
				clingX = this.getPos().getX();
				clingZ = this.getPos().getZ();
				switch(clingDirection) {
					// all these are player's direction from container view (opposite of player look when looking at wall)
					case NORTH:
						clingZ -= 0.1;
						break;
					case SOUTH:
						clingZ += 0.1;
						break;
					case EAST:
						clingX += 0.1;
						break;
					case WEST:
						clingX -= 0.1;
						break;
					default:
						break;
				}
				switch(clingDirection2) {
					case NORTH:
						clingZ -= 0.1;
						break;
					case SOUTH:
						clingZ += 0.1;
						break;
					case EAST:
						clingX += 0.1;
						break;
					case WEST:
						clingX -= 0.1;
						break;
					default:
						break;
				}


				BlockPos wall = getWallPos(this);
				playHitSound(this, wall);
				spawnWallParticle(this, wall);

			}

		}
	}

	public boolean tryUseStamina(CharacterResources resources, int amount) {
		//TODO: figure out a way to make this all happen serverside instead?
		if (!resources.has(SkillCheck.STAMINA) || !SkillCheck.config.useStamina) return false;
		CharacterResourceEntry stamina = resources.get(SkillCheck.STAMINA);
//		System.out.println("current: " + stamina.getCurrent());
//		System.out.println("max: " + stamina.getMax());
		if (stamina.getCurrent() < amount) return false;
		SkillCheckNetworking.consumeStamina(amount);
		return true;
	}

	private static boolean nearWall(Entity entity, double dist) {
		return !entity.world.isSpaceEmpty(entity.getBoundingBox().expand(dist, 0, dist));
	}

	private static boolean canWallCling(PlayerEntity player) {
		CharacterResources resources = CharacterData.get(player).getResources();
		long currentStamina = 0;
		if (resources.has(SkillCheck.STAMINA)) {
			currentStamina = resources.get(SkillCheck.STAMINA).getCurrent();
		}

		if (clingTime > -5 || (currentStamina <= 0 && SkillCheck.config.useStamina) || player.isClimbing()) return false;

		BlockPos down = new BlockPos(player.getPos().getX(), player.getPos().getY() - 0.8, player.getPos().getZ());
		if (collidesWith(player.world.getBlockState(down).getCollisionShape(player.world, down), player.getPos(), player.getBlockPos())) return false;

		if (!player.world.getFluidState(player.getBlockPos()).isEmpty() || !player.world.getFluidState(player.getBlockPos().up()).isEmpty()) return false;

		double dist = 0.4;
		Box box = player.getBoundingBox().shrink(0.2, 0, 0.2);
		Box[] axes = { box.stretch(0, 0, -dist), box.stretch(dist, 0, 0), box.stretch(0, 0, dist), box.stretch(-dist, 0, 0) };

		Set<Direction> walls = new HashSet<>();
		clingDirection = UP;
		clingDirection2 = UP;
		Direction direction;

		int i = 0;
		for (Box axis : axes) {
			direction = fromHorizontal(i++);
			if (!player.world.isSpaceEmpty(axis)) {

				if (clingDirection == UP) clingDirection = direction;
				else clingDirection2 = direction;

				walls.add(direction);
			}
		}

		if (walls.size() == 0) return false;

		if (SkillCheck.SLIPPERY_BLOCKS.contains(player.world.getBlockState(getWallPos(player)).getBlock()) ^ SkillCheck.config.invertSlipperyTag
				|| player.world.getBlockState(getWallPos(player)).getBlock() instanceof FluidBlock) return false;

		//let players vault up walls if they're a good enough thief
		if (player.getPos().getY() < lastJumpY || CharacterData.get(player).getClasses().get(SkillCheck.THIEF).getLevel() >= 5) return true;

		if (walls.size() == 1) {

			return !walls.contains(lastDirection) && !walls.contains(lastDirection2);

		} else return !walls.contains(lastDirection) || !walls.contains(lastDirection2);

	}

	private static BlockPos getWallPos(Entity entity) {
		BlockPos pos = entity.getBlockPos().offset(clingDirection.getOpposite());
		return entity.world.getBlockState(pos).getMaterial().isSolid()? pos : pos.offset(UP);
	}

	private static void wallJump(PlayerEntity player, float up, float strafe, float forward) {

		float f = 1.0F / MathHelper.sqrt(strafe * strafe + up * up + forward * forward);

		up = up * (f * .7f);
		strafe = strafe * f;
		forward = forward * f;

		float f1 = MathHelper.sin(player.yaw * 0.017453292F) / 5;
		float f2 = MathHelper.cos(player.yaw * 0.017453292F) / 5;

		int jumpBoostLevel = 0;
		StatusEffectInstance jumpBoostEffect = player.getStatusEffect(StatusEffects.JUMP_BOOST);
		if (jumpBoostEffect != null) jumpBoostLevel = jumpBoostEffect.getAmplifier() + 1;

		SkillCheckNetworking.clearFall();
		player.setVelocity(player.getVelocity().x + (strafe * f2 - forward * f1), up + (jumpBoostLevel * .075), player.getVelocity().z + (forward * f2 + strafe * f1));

	}

	private static void playHitSound(Entity entity, BlockPos pos) {

		BlockState state = entity.world.getBlockState(pos);
		BlockSoundGroup soundtype = state.getBlock().getSoundGroup(state);
		entity.playSound(soundtype.getHitSound(), soundtype.getVolume() * 0.25F, soundtype.getPitch());

	}

	private static void playBreakSound(Entity entity, BlockPos pos) {

		BlockState state = entity.world.getBlockState(pos);
		BlockSoundGroup soundtype = state.getBlock().getSoundGroup(state);
		entity.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch());

	}

	private static void spawnWallParticle(Entity entity, BlockPos pos) {

		BlockState state = entity.world.getBlockState(pos);
		if (state.getRenderType() != BlockRenderType.INVISIBLE) {
			entity.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), entity.getX(), entity.getY(), entity.getZ(), 0.0D, 0.0D, 0.0D);
		}

	}

	private void handleDoubleJump() {
		if (this.onGround || !world.getFluidState(this.getBlockPos()).isEmpty()) {

			airTime = 0;
			jumpCount = 1;

		} else if (this.input.jumping && !this.abilities.flying) {

			if (!jumpKey && jumpCount < 2 && airTime > 1 && (tryUseStamina(CharacterData.get(this).getResources(), 5) || !SkillCheck.config.useStamina)) {

				this.jump();
				jumpCount++;

				SkillCheckNetworking.clearFall();

			}

			jumpKey = true;

		} else {

			airTime++;
			jumpKey = false;

		}
	}

	private static boolean collidesWith(VoxelShape shape, Vec3d pos, Vec3i blockPos) {
		double x = Math.abs(pos.getX()) - Math.abs(blockPos.getX());
		double y = Math.abs(pos.getY()) - Math.abs(blockPos.getY());
		double z = Math.abs(pos.getZ()) - Math.abs(blockPos.getZ());
		return ((VoxelShapeGetter)shape).skillcheck_contains(x, y, z);
	}

}
 