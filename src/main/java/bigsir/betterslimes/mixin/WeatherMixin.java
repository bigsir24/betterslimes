package bigsir.betterslimes.mixin;

import bigsir.betterslimes.custom.WeatherRainSlime;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Weather.class)
public abstract class WeatherMixin {
	private static final Weather overworldRainSlime = (new WeatherRainSlime(9))
		.setLanguageKey("overworld.rain.slime")
		.setPrecipitation("/environment/rain.png", 0)
		.setSpawnRainParticles(true);
}
