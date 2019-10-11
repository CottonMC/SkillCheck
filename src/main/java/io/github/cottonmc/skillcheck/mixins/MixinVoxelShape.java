package io.github.cottonmc.skillcheck.mixins;

import io.github.cottonmc.skillcheck.util.VoxelShapeGetter;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VoxelShape.class)
public abstract class MixinVoxelShape implements VoxelShapeGetter {
	@Shadow protected abstract boolean contains(double x, double y, double z);

	@Override
	public boolean skillcheck_contains(double x, double y, double z) {
		return contains(x, y, z);
	}
}
