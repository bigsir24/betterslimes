package bigsir.betterslimes.mixin;

import bigsir.betterslimes.interfaces.EntitySlimeInterface;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.colorizer.Colorizers;
import net.minecraft.client.render.entity.LivingRenderer;
import net.minecraft.client.render.entity.SlimeRenderer;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.entity.animal.EntitySheep;
import net.minecraft.core.entity.monster.EntitySlime;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SlimeRenderer.class, remap = false)
public abstract class SlimeRendererMixin extends LivingRenderer<EntitySlime> {

	public float[] rgb = {1.0F, 0.0F, 0.0F};
	public int[] incr = {-1, 1, 1};
	public int toChange = 2;
	public int rCol = -1;
	public int gCol = -1;
	public int bCol = -1;

	@Shadow
	private ModelBase scaleAmount;

	public SlimeRendererMixin(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	@Inject(at = @At("TAIL"), method = "scaleSlime(Lnet/minecraft/core/entity/monster/EntitySlime;F)V", cancellable = true)
	void scaleSlimeTailMixin(EntitySlime entityslime, float f, CallbackInfo ci) {
		ItemStack itemstack = entityslime.getHeldItem();
		if(itemstack != null){
			GL11.glPushMatrix();
			GL11.glRotated(180, 1, 0, 0);
			GL11.glRotated(entityslime.id, 0, 1, 0);
			GL11.glScaled(0.25, 0.25, 0.25);
			//2.4
			GL11.glTranslated(0,2.4,0);
			ItemModelDispatcher.getInstance().getDispatch(itemstack).renderItemInWorld(Tessellator.instance,entityslime,itemstack, 1, 1, true);
			GL11.glColor4f(1,1,1,1);
			GL11.glPopMatrix();
		}
		//renderEquippedItems(entityslime, 0.625f);
		int c = entityslime.getEntityData().getInt(18) == -1 ? 0 : entityslime.getEntityData().getInt(18);
		float r = EntitySheep.fleeceColorTable[c][0];
		float g = EntitySheep.fleeceColorTable[c][1];
		float b = EntitySheep.fleeceColorTable[c][2];
		float brightness = 1.0F;
		if(!LightmapHelper.isLightmapEnabled()) brightness = entityslime.getBrightness(f);
		if(entityslime.nickname.equals("jonk")) {
			this.rgb[this.toChange] += 0.01F * this.incr[this.toChange];
			if (this.rgb[this.toChange] >= 1.0F || this.rgb[this.toChange] <= 0.0F) {
				this.incr[toChange] *= -1;
				this.toChange = this.toChange + 1 > 2 ? 0 : this.toChange + 1;
			}
			GL11.glColor4f(this.rgb[0] * brightness, this.rgb[1] * brightness, this.rgb[2] * brightness, 1);
			//entityslime.world.players.get(0).sendMessage(this.r + "");
		}else if(entityslime.getEntityData().getInt(18) == -1){
			int color = Colorizers.grass.getColor(entityslime.world, (int) entityslime.x, (int) entityslime.y, (int) entityslime.z);
			int red = (color >> 16) & 255;
			int green = (color >> 8) & 255;
			int blue = color & 255;
			this.rCol = red; this.gCol = green; this.bCol = blue;
			GL11.glColor4f(red/255F * brightness, green/255F * brightness, blue/255F * brightness, 1);
		}else{
			GL11.glColor4f(r * brightness,g * brightness,b * brightness,1);
		}
	}

}
