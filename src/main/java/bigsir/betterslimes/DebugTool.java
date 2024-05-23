package bigsir.betterslimes;

import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.monster.EntitySlime;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

public class DebugTool extends Item {
	public DebugTool(String name, int id) {
		super(name, id);
	}

	@Override
	public boolean useItemOnEntity(ItemStack itemstack, EntityLiving entityliving, EntityPlayer entityPlayer) {
		if(entityliving instanceof EntitySlime){
			if(!entityPlayer.isSneaking()){
				((EntitySlime) entityliving).setSlimeSize(((EntitySlime) entityliving).getSlimeSize() + 1);
			}else{
				((EntitySlime) entityliving).setSlimeSize(Math.max(((EntitySlime) entityliving).getSlimeSize() - 1, 1));
			}
			return true;
		}
		return false;
	}

}
