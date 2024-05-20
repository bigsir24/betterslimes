package bigsir.betterslimes.mixin;

import bigsir.betterslimes.interfaces.EntitySlimeInterface;
import bigsir.betterslimes.enums.FlowerColor;
import bigsir.betterslimes.interfaces.RenderGlobalInterface;
import com.mojang.nbt.CompoundTag;
import net.minecraft.client.render.colorizer.Colorizers;
import net.minecraft.core.Global;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFlower;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.animal.EntitySheep;
import net.minecraft.core.entity.monster.EntitySlime;
import net.minecraft.core.entity.monster.IEnemy;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Random;

@Mixin(value = EntitySlime.class, remap = false)
public abstract class EntitySlimeMixin extends EntityLiving implements IEnemy, EntitySlimeInterface, RenderGlobalInterface {

	public EntitySlimeMixin(World world) {
		super(world);
	}

	//Effect played when consuming slime
	//Should probably be in a separate class
	public void consumeEffect(EntityLiving entityliving){
		int size = ((EntitySlime) entityliving).getSlimeSize();
		int particleCount = size * 16;
		Random rand = new Random();
		for (int i = 0; i < particleCount; i++) {
			double offsetX = (rand.nextDouble() - 0.5) * size / 2;
			double offsetY = (rand.nextDouble() - 0.5) * size / 2;
			double offsetZ = (rand.nextDouble() - 0.5) * size / 2;
			if(entityliving.getEntityData().getInt(18) == -1){
				if(Global.isServer) return;
				int color = Colorizers.grass.getColor(this.world, (int) x, (int) y, (int) z);
				float r = ((color >> 16) & 255) / 255F;
				float g = ((color >> 8) & 255) / 255F;
				float b = (color & 255) / 255F;
				for(int j = 0; j < entityliving.world.listeners.size(); ++j) {
					((RenderGlobalInterface) entityliving.world.listeners.get(j)).addSlimeParticle(entityliving.x + offsetX,entityliving.y + size / 4f + offsetY,entityliving.z + offsetZ,r,g,b);
				}
				//entityliving.world.spawnParticle("item", entityliving.x + offsetX, entityliving.y + size / 4f + offsetY, entityliving.z + offsetZ, 0, 0, 0, Item.slimeball.id);
			}else{
				if(Global.isServer) return;
				int c = entityliving.getEntityData().getInt(18);
				float r = EntitySheep.fleeceColorTable[c][0];
				float g = EntitySheep.fleeceColorTable[c][1];
				float b = EntitySheep.fleeceColorTable[c][2];
				for(int j = 0; j < entityliving.world.listeners.size(); ++j) {
					((RenderGlobalInterface) entityliving.world.listeners.get(j)).addSlimeParticle(entityliving.x + offsetX,entityliving.y + size / 4f + offsetY,entityliving.z + offsetZ,r,g,b);
				}
			}
		}
		entityliving.playDeathSound();
	}

	public int flowerToColor(Block block){
		String string = block.asItem().getDefaultStack().getItemName().split("\\.")[2].toUpperCase();
		return FlowerColor.valueOf(string).dyeMeta;
	}

	@Shadow
	public abstract int getSlimeSize();

	@Shadow
	public abstract void setSlimeSize(int i);

	@Inject(at = @At("TAIL"), method = "init()V")
	protected void init(CallbackInfo ci) {
		this.entityData.define(17, ItemStack.NO_ITEM);
		this.entityData.define(18, -1);
	}

	@Inject(at = @At("TAIL"), method = "spawnInit()V")
	public void spawnInitMixin(CallbackInfo ci){
		Random rand = new Random();
		if(this.y >= 126.0) {
			boolean isRaining = this.world.getCurrentWeather() == Weather.overworldRain;
			int size = isRaining && rand.nextInt(3) == 0 ? 2 : 1;
			this.setSlimeSize(size);
			if(isRaining) this.entityData.set(18, DyeColor.DYE_BLUE.blockMeta);
		}
		//Changes slime color to white if biome is snowy
		/*Biome biome = this.world.getBlockBiome((int)this.x,(int)this.y,(int)this.z);
		if(biome.hasSurfaceSnow()) this.entityData.set(18, 0);
		if(rand.nextInt(180) == 0 && this.getSlimeSize() == 1){
			this.entityData.set(18, 6);
		}*/
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData", cancellable = true)
	public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		CompoundTag itemData = new CompoundTag();
		this.getHeldItem().writeToNBT(itemData);
		tag.put("SlimeData", itemData);
		tag.putInt("SlimeColor", this.entityData.getInt(18));
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData", cancellable = true)
	public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
		CompoundTag itemData = tag.getCompound("SlimeData");
		ItemStack itemstack = ItemStack.readItemStackFromNbt(itemData);
		if(itemstack == null) itemstack = ItemStack.NO_ITEM;
		this.entityData.set(17, itemstack);
		this.entityData.set(18, tag.getInteger("SlimeColor"));
	}

	@Redirect(at = @At(value = "INVOKE", target = "net/minecraft/core/world/World.spawnParticle(Ljava/lang/String;DDDDDDI)V"), method = "tick()V")
	public void tickParticleChanger(World world, String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {
		if(this.entityData.getInt(18) == -1){
			//world.spawnParticle("item", x, this.bb.minY, z, 0.0, 0.0, 0.0, Item.slimeball.id);
			int color = Colorizers.grass.getColor(this.world, (int) x, (int) y, (int) z);
			float r = ((color >> 16) & 255) / 255F;
			float g = ((color >> 8) & 255) / 255F;
			float b = (color & 255) / 255F;
			if(Global.isServer) return;
			for(int i = 0; i < this.world.listeners.size(); ++i) {
				((RenderGlobalInterface) this.world.listeners.get(i)).addSlimeParticle(x,this.bb.minY,z,r,g,b);
			}
		}else{
			int c = this.entityData.getInt(18) == -1 ? 0 : this.entityData.getInt(18);
			float r = EntitySheep.fleeceColorTable[c][0];
			float g = EntitySheep.fleeceColorTable[c][1];
			float b = EntitySheep.fleeceColorTable[c][2];
			if(Global.isServer) return;
			for(int i = 0; i < this.world.listeners.size(); ++i) {
				((RenderGlobalInterface) this.world.listeners.get(i)).addSlimeParticle(x,this.bb.minY,z,r,g,b);
			}
		}
	}


	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/core/entity/monster/EntitySlime.getSlimeSize()I"), method = "tick()V")
	public void tickLandedOnFlower(CallbackInfo ci) {
		Block block = this.world.getBlock((int) this.x, (int) this.y, (int) this.z);
		if(this.entityData.getInt(18) == -1 && block instanceof BlockFlower && block.id >= 330 && block.id <= 335){
			this.entityData.set(18, flowerToColor(block));
		}
	}

	@Inject(at = @At("TAIL"), method = "updatePlayerActionState()V", cancellable = true)
	public void updatePlayerActionStateHead(CallbackInfo ci){
		double x = this.x;
		double y = this.y;
		double z = this.z;
		List<Entity> entities = this.world.getEntitiesWithinAABB(EntityItem.class, new AABB(x - 3,y - 2,z - 3,x + 3,y + 2, z + 3));

		//Slimes will seek out items if they have none equipped
		if(!entities.isEmpty() && getHeldItem() == ItemStack.NO_ITEM){
			Entity e = entities.get(0);
			this.faceEntity(e, 10.0F, 20.0F);
			double dist = Vec3d.createVector(e.x-x,e.y-y,e.z-z).lengthVector();
			if(dist < this.getSlimeSize() / 2f){
				e.remove();
				ItemStack itemstack = ((EntityItem) e).item;
				if(itemstack.itemID != Item.slimeball.id){
					this.entityData.set(17, itemstack);
				}else{
					Random rand = new Random();
					if(rand.nextInt(3) == 0) {
						this.setSlimeSize(Math.min(this.getSlimeSize()*2, 4));
					}
					consumeEffect(this);
				}
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "updatePlayerActionState()V", cancellable = true)
	public void updatePlayerActionStateTail(CallbackInfo ci){
		//Swim
		if(this.isInWater()){
			this.isJumping = true;
			if(!this.hasCurrentTarget()){
				Random rand = new Random();
				if(this.tickCount % ((10 - rand.nextInt(6)) * 10) == 0){
					//this.moveStrafing = (1.0F - this.random.nextFloat() * 2.0F);
					int rRot = 90 - rand.nextInt(181);
					this.yRot = this.yRotO + rRot;
				}
			}
			//extracted from If statement for testing
			this.moveForward = (float)this.getSlimeSize();
			ci.cancel();
		}

		//Merge with small slimes, disabled for now
		/*
		if(this.getSlimeSize() == 1){
			List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, new AABB(x - 5,y - 2,z - 5,x + 5,y + 2, z + 5));
			if(!entities.isEmpty() && entities.get(0) instanceof EntitySlime && ((EntitySlime) entities.get(0)).getSlimeSize() == 1){
				Entity e = entities.get(0);
				this.faceEntity(e, 10.0F, 20.0F);
				double dist = Vec3d.createVector(e.x-x,e.y-y,e.z-z).lengthVector();
				if(dist < 1){
					e.remove();
					this.setSlimeSize(2);
				}
			}
		}*/
	}

	@Inject(at = @At(value="INVOKE", target = "net/minecraft/core/world/World.entityJoinedWorld(Lnet/minecraft/core/entity/Entity;)Z"), method = "remove()V", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void slimeColorTransfer(CallbackInfo ci, int i, int j, int k, float f1, float f2, EntitySlime entitySlime){
		((EntitySlimeInterface) entitySlime).setColor(this.entityData.getInt(18));
	}

	@Inject(at = @At("HEAD"), method = "playerTouch", cancellable = true)
	public void playerTouchDamageCancel(CallbackInfo ci){
		if(this.vehicle instanceof EntityPlayer){
			ci.cancel();
		}
	}

	@Override
	public void rideTick() {
		super.rideTick();
		this.yRot = ((EntityPlayer)this.vehicle).yRot;
	}

	@Override
	public double getRidingHeight() {
		return -1.1f;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		super.interact(entityplayer);
		ItemStack itemstack = entityplayer.getHeldItem();
		if(this.vehicle == null && itemstack != null && itemstack.getItem() == Item.saddle){
			this.startRiding(entityplayer);
			return true;
		}else if(entityplayer.passenger == this && itemstack == null){
			entityplayer.ejectRider();
			return true;
		}

		if(itemstack != null && itemstack.itemID == Block.spongeDry.id){
			this.entityData.set(18, -1);
			return true;
		}
		return false;
	}

	@Override
	public void setColor(int i) {
		this.entityData.set(18, i);
	}

	@Override
	public String getEntityTexture() {
		//if(this.nickname.equals("jonk") || this.nickname.equals("test")) return "assets/slimed/textures/entity/slimeNew.png";
		//return this.entityData.getInt(18) != -1 ? "assets/slimed/textures/entity/slimeNew.png" : super.getEntityTexture();
		//if(this.getHeldItem() != ItemStack.NO_ITEM) return "assets/slimed/textures/entity/slimeNewItem.png";
		return "assets/betterslimes/textures/entity/slime_colorable.png";
	}

	@Override
	public ItemStack getHeldItem() {
		return this.entityData.getItemStack(17);
	}

	@Override
	public boolean getCanSpawnHere(){
		Chunk chunk = this.world.getChunkFromBlockCoords(MathHelper.floor_double(this.x), MathHelper.floor_double(this.z));
		if (this.y > 32.0 && this.y < 127.0) {
			return false;
		} else if (chunk.getChunkRandom(987234911L).nextInt(10) != 0) {
			return false;
		} else {
			Random rand = new Random();
			if(this.y > 126.0 && rand.nextInt(4) != 0 && this.world.getCurrentWeather() != Weather.overworldRain) return false;
			return this.world.difficultySetting != 0 && super.getCanSpawnHere();
		}
	}

	@Override
	protected boolean canDespawn() {
		return this.nickname.isEmpty() && this.getHeldItem() == ItemStack.NO_ITEM;
	}

	@Override
	public List<WeightedRandomLootObject> getMobDrops() {
		//Adds equipped item to drops
		ItemStack itemstack = this.entityData.getItemStack(17);
		this.mobDrops.add(new WeightedRandomLootObject(itemstack, itemstack.stackSize));
		return this.mobDrops;
	}

	@Override
	protected void checkFallDamage(double d, boolean flag) {
		if(this.getSlimeSize() < 3) return;
		super.checkFallDamage(d, flag);
	}
}
