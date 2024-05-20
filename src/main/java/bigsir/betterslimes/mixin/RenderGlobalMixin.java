package bigsir.betterslimes.mixin;

import bigsir.betterslimes.custom.CustomSlimeFX;
import bigsir.betterslimes.interfaces.RenderGlobalInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.sound.SoundCategory;
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
			double d6 = this.mc.activeCamera.getX() - x;
			double d7 = this.mc.activeCamera.getY() - y;
			double d8 = this.mc.activeCamera.getZ() - z;
			if (!(d6 * d6 + d7 * d7 + d8 * d8 > 16.0 * 16.0)) {
				this.mc.effectRenderer.addEffect(new CustomSlimeFX(this.worldObj, x, y, z,r,g,b));
			}
		}
	}
}
