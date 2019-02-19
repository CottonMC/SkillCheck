package io.github.cottonmc.skillworks.mixins;

import com.mojang.authlib.GameProfile;
import io.github.cottonmc.skillworks.Skillworks;
import me.elucent.earlgray.api.Traits;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/*
	Much code in this class is taken from Wall-Jump, and modified for Yarn, 1.14, and the Gymnast skill.
	The original repository can be found here: https://github.com/genandnic/Wall-Jump
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinGymnistClient extends AbstractClientPlayerEntity {

	@Shadow public Input input;

	@Shadow public abstract boolean isSneaking();

	private static float wallJumpHeight = 0.8f;
	private static int wallSlideDelay = 15;
	private static double wallSlideSpeed = 0.1;

	private static int clingTime;
	private static int keyTimer;
	private static double clingX, clingZ;
	private static Direction clingDirection = Direction.UP;
	private static Direction lastDirection = Direction.DOWN;
	private static Direction clingDirection2 = Direction.UP;
	private static Direction lastDirection2 = Direction.DOWN;
	private static double lastJumpY = Double.MAX_VALUE;

	private static int airTime;
	private static int jumpCount = 0;
	private static boolean jumpKey = false;

	public MixinGymnistClient(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "updateMovement", at = @At("TAIL"))
	public void gymnistMovement(CallbackInfo ci) {
		if (Traits.has(this, Skillworks.GYMNIST)) {
			// ### WALL-CLING/WALL-JUMP CODE ###
			if (this.input.sneaking) keyTimer++;
			else keyTimer = 0;

			if (this.onGround || this.abilities.flying) {

				clingTime = 0;
				clingDirection = Direction.UP;
				clingDirection2 = Direction.UP;
				lastDirection = Direction.DOWN;
				lastDirection2 = Direction.DOWN;

			} else if (clingTime > 0) {

				BlockPos wall = getWallPos(this);

				if (keyTimer == 0 || this.onGround || !nearWall(this, 0.2)) {

					clingTime = 0;
//					CommonProxy.NETWORK.sendToServer(new PacketWallCling(false));
					if ((this.input.forward || this.input.back || this.input.left || this.input.right) && this.getHungerManager().getFoodLevel() > 6 && nearWall(this, 0.2)) {

						lastDirection = clingDirection;
						lastDirection2 = clingDirection2;
						lastJumpY = this.getPos().getY() - 2.0;

						playBreakSound(this, wall);
						spawnWallParticle(this, wall);
						wallJump(this, wallJumpHeight, this.velocityX, this.velocityZ);
//						CommonProxy.NETWORK.sendToServer(new PacketWallJump());

					}

				} else {

					this.setPosition(clingX+0.5, this.getPos().getY(), clingZ+0.5);
					this.fallDistance = 0.0F;
					this.velocityX = 0.0;
					this.velocityZ = 0.0;

					if (this.velocityY < -0.5) {

						this.velocityY = this.velocityY + 0.25;
						spawnWallParticle(this, wall);

					} else {

						if ((clingTime++ > wallSlideDelay || this.getHungerManager().getFoodLevel() < 7)) {

							this.velocityY = -wallSlideSpeed;
							spawnWallParticle(this, wall);

						} else {

							this.velocityY = 0.0;

						}

					}

				}

			} else {

				clingTime--;
				if (keyTimer > 0 && (keyTimer < 5 || clingTime < -15) && canWallCling(this)) {

					this.velocityX = 0.0;
					this.velocityZ = 0.0;
					if (this.velocityY > -0.75) this.velocityY = 0.0;

					clingTime = 1;
					clingX = this.getPos().getX();
					clingZ = this.getPos().getZ();

					BlockPos wall = getWallPos(this);
					playHitSound(this, wall);
					spawnWallParticle(this, wall);

				}

			}

			// ### DOUBLE-JUMP CODE ###
			if (this.onGround) {

				airTime = 0;
				jumpCount = 1;

			} else if (this.input.jumping && !this.abilities.flying) {

				if (!jumpKey && jumpCount < 2 && airTime > 1 && this.getHungerManager().getFoodLevel() > 6) {

					this.doJump(true);
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

	private static boolean nearWall(Entity entity, double dist) {
		return entity.world.isAreaNotEmpty(entity.getBoundingBox().expand(dist, 0, dist));
	}

	private static boolean canWallCling(PlayerEntity player) {

		if (clingTime > -5 /*|| player.canClimb()*/ || player.getHungerManager().getFoodLevel() < 1) return false;

		if (player.world.getBlockState(new BlockPos(player.getPos().getX(), player.getPos().getY() - 0.8, player.getPos().getZ())).isFullBoundsCubeForCulling()) return false;

		double dist = 0.4;
		BoundingBox box = player.getBoundingBox().shrink(0.3, 0, 0.3);
		BoundingBox[] axes = { box.stretch(0, 0, -dist), box.stretch(dist, 0, 0), box.stretch(0, 0, dist), box.stretch(-dist, 0, 0) };

		Set<Direction> walls = new HashSet<>();
		clingDirection = Direction.UP;
		clingDirection2 = Direction.UP;
		Direction direction;

		int i = 0;
		for (BoundingBox axis : axes) {
			direction = Direction.fromHorizontal(i++);
			if (player.world.isAreaNotEmpty(axis)) {

				if (clingDirection == Direction.UP) clingDirection = direction;
				else clingDirection2 = direction;

				walls.add(direction);
			}
		}

		if (walls.size() == 0) return false;

		if (Skillworks.config.clingBlackList.contains(player.world.getBlockState(getWallPos(player)).getBlock().getTranslationKey()) ^ Skillworks.config.invertClngBlackList) return false;

		if (Traits.has(player, Skillworks.GYMNIST) || player.getPos().getY() < lastJumpY) return true; //TODO: change to use levels later

		if (walls.size() == 1) {

			return !walls.contains(lastDirection) && !walls.contains(lastDirection2);

		} else return !walls.contains(lastDirection) || !walls.contains(lastDirection2);

	}

	private static BlockPos getWallPos(Entity entity) {
		BlockPos pos = new BlockPos(entity).offset(clingDirection);
		return entity.world.getBlockState(pos).getMaterial().suffocates()? pos : pos.offset(Direction.UP);
	}

	private static void wallJump(LivingEntity entity, float up, double velX, double velZ) {

		int jumpBoostLevel = 0;
		StatusEffectInstance jumpBoost = entity.getPotionEffect(StatusEffects.JUMP_BOOST);
		if (jumpBoost != null) jumpBoostLevel = jumpBoost.getAmplifier() + 1;

		entity.velocityY = up + (jumpBoostLevel * .075);
		entity.velocityX += velX;
		entity.velocityZ += velZ;

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

	private static Random rand = new Random();

	private static void spawnWallParticle(Entity entity, BlockPos pos) {

		BlockState state = entity.world.getBlockState(pos);
		if (state.getRenderType() != BlockRenderType.INVISIBLE) {
			entity.world.addParticle(new BlockStateParticleParameters(ParticleTypes.BLOCK, state), entity.x, entity.y, entity.z, 0.0D, 0.0D, 0.0D);
		}

	}

}
 