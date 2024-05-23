package bigsir.betterslimes;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.ItemBuilder;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class BetterSlimes implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "betterslimes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("Better Slimes initialized.");
    }

	public static Item slimeEffect;
	public static Item debugTool;

	@Override
	public void beforeGameStart() {
		slimeEffect = new ItemBuilder(MOD_ID)
			.setIcon("betterslimes:item/slime_effect")
			.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.setItemModel(item -> new ItemModelStandard(item, null))
			.build(new Item("effect.slime", 21000));

		debugTool = new ItemBuilder(MOD_ID)
			.setIcon("minecraft:item/stick")
			.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
			.setItemModel(item -> new ItemModelStandard(item, null))
			.build(new DebugTool("tool.debug", 21001));
	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}

	@Override
	public void initNamespaces() {

	}
}
