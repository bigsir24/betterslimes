package bigsir.betterslimes.custom;

import bigsir.betterslimes.BetterSlimes;
import net.minecraft.client.entity.fx.EntityFX;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class CustomSlimeFX extends EntityFX {
	public CustomSlimeFX(World world, double x, double y, double z, float r, float g, float b) {
		super(world, x, y, z, 0, 0 ,0);
		ItemStack itemstack = BetterSlimes.slimeEffect.getDefaultStack();
		this.particleTexture = ((ItemModel) ItemModelDispatcher.getInstance().getDispatch(itemstack)).getIcon((Entity)null, itemstack);
		this.particleRed = r;
		this.particleGreen = g;
		this.particleBlue = b;
		this.particleGravity = Block.blockSnow.blockParticleGravity;
		this.particleScale /= 2.0F;
	}

	public int getFXLayer() {
		return 2;
	}

	public void renderParticle(Tessellator t, float partialTick, float rotationX, float rotationXZ, float rotationZ, float rotationYZ, float rotationXY) {
		float f6 = (float)this.particleTexture.getSubIconU((double)(this.particleTextureJitterX / 4.0F));
		float f7 = (float)this.particleTexture.getSubIconU((double)(0.25F + this.particleTextureJitterX / 4.0F));
		float f8 = (float)this.particleTexture.getSubIconV((double)(this.particleTextureJitterX / 4.0F));
		float f9 = (float)this.particleTexture.getSubIconV((double)(0.25F + this.particleTextureJitterX / 4.0F));
		float f10 = 0.1F * this.particleScale;
		float f11 = (float)(this.xo + (this.x - this.xo) * (double)partialTick - lerpPosX);
		float f12 = (float)(this.yo + (this.y - this.yo) * (double)partialTick - lerpPosY);
		float f13 = (float)(this.zo + (this.z - this.zo) * (double)partialTick - lerpPosZ);
		float brightness = 1.0F;
		if (LightmapHelper.isLightmapEnabled()) {
			t.setLightmapCoord(this.getLightmapCoord(partialTick));
		} else {
			brightness = this.getBrightness(partialTick);
		}

		t.setColorOpaque_F(brightness * this.particleRed, brightness * this.particleGreen, brightness * this.particleBlue);
		t.addVertexWithUV((double)(f11 - rotationX * f10 - rotationYZ * f10), (double)(f12 - rotationXZ * f10), (double)(f13 - rotationZ * f10 - rotationXY * f10), (double)f6, (double)f9);
		t.addVertexWithUV((double)(f11 - rotationX * f10 + rotationYZ * f10), (double)(f12 + rotationXZ * f10), (double)(f13 - rotationZ * f10 + rotationXY * f10), (double)f6, (double)f8);
		t.addVertexWithUV((double)(f11 + rotationX * f10 + rotationYZ * f10), (double)(f12 + rotationXZ * f10), (double)(f13 + rotationZ * f10 + rotationXY * f10), (double)f7, (double)f8);
		t.addVertexWithUV((double)(f11 + rotationX * f10 - rotationYZ * f10), (double)(f12 - rotationXZ * f10), (double)(f13 + rotationZ * f10 - rotationXY * f10), (double)f7, (double)f9);
	}
}
