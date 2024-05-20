package bigsir.betterslimes.mixin;

import bigsir.betterslimes.interfaces.EntitySlimeInterface;
import net.minecraft.core.block.BlockWool;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.monster.EntitySlime;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemDye;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDye.class, remap = false)
public abstract class ItemDyeMixin extends Item {
	public ItemDyeMixin(int id) {
		super(id);
	}

	@Inject(at = @At("TAIL"), method = "useItemOnEntity(Lnet/minecraft/core/item/ItemStack;Lnet/minecraft/core/entity/EntityLiving;Lnet/minecraft/core/entity/player/EntityPlayer;)Z", cancellable = true)
	public void useItemOnEntityMixin(ItemStack itemstack, EntityLiving entityliving, EntityPlayer entityPlayer, CallbackInfoReturnable<Boolean> cir){
		if(entityliving instanceof EntitySlime && BlockWool.func_21034_c(itemstack.getMetadata()) != entityliving.getEntityData().getInt(18)){
            EntitySlime entitySlime = (EntitySlime) entityliving;
			((EntitySlimeInterface) entitySlime).setColor(BlockWool.func_21034_c(itemstack.getMetadata()));
			itemstack.consumeItem(entityPlayer);
		}
	}
}
