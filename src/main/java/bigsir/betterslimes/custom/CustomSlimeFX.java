package bigsir.betterslimes.custom;

import bigsir.betterslimes.BetterSlimes;
import net.minecraft.client.entity.fx.EntityItemFX;
import net.minecraft.core.world.World;

public class CustomSlimeFX extends EntityItemFX {
	public CustomSlimeFX(World world, double x, double y, double z, float r, float g, float b) {
		super(world, x, y, z, BetterSlimes.slimeEffect);
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
	}
}
