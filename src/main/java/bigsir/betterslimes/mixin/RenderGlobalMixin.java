package bigsir.betterslimes.mixin;

import bigsir.betterslimes.custom.CustomSlimeFX;
import bigsir.betterslimes.interfaces.RenderGlobalInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.core.world.LevelListener;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(value = RenderGlobal.class, remap = false)
public abstract class RenderGlobalMixin implements LevelListener, RenderGlobalInterface {

	@Shadow
	private Minecraft mc;
	@Shadow
	private World worldObj;

	//@Override
	public void addSlimeParticle(double x, double y, double z, float r, float g, float b){
		if (this.mc != null && this.mc.activeCamera != null && this.mc.effectRenderer != null) {
			double xDistSqr = Math.pow(this.mc.activeCamera.getX() - x, 2);
			double yDistSqr = Math.pow(this.mc.activeCamera.getY() - y, 2);
			double zDistSqr = Math.pow(this.mc.activeCamera.getZ() - z, 2);
			if (!(xDistSqr + yDistSqr + zDistSqr > 16.0 * 16.0)) {
				this.mc.effectRenderer.addEffect(new CustomSlimeFX(this.worldObj, x, y, z,r,g,b));
			}
		}
	}
}
