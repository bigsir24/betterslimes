package bigsir.betterslimes.custom;

import bigsir.betterslimes.interfaces.EntitySlimeInterface;
import net.minecraft.core.entity.monster.EntitySlime;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.WeatherClear;

import java.util.Random;

public class WeatherRainSlime extends WeatherClear {
	public WeatherRainSlime(int id) {
		super(id);
	}

	@Override
	public void doEnvironmentUpdate(World world, Random rand, int x, int z) {
		super.doEnvironmentUpdate(world, rand, x, z);
		if(rand.nextInt(100) == 0){
			int y = world.getHeightValue(x,z) + 100;
			EntitySlime entityslime = new EntitySlime(world);
			entityslime.setPos(x,y,z);
			entityslime.setSlimeSize(rand.nextInt(6) == 0 ? 2 : 1);
			((EntitySlimeInterface) entityslime).setColor(rand.nextInt(17)-1);
			world.entityJoinedWorld(entityslime);
		}
	}
}
