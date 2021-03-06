package com.mcmoddev.endmetals.integration.plugins;

import com.mcmoddev.endmetals.EndMetals;
import com.mcmoddev.lib.data.Names;
import com.mcmoddev.lib.init.Materials;
import com.mcmoddev.lib.integration.IIntegration;
import com.mcmoddev.lib.integration.MMDPlugin;
import com.mcmoddev.lib.material.MMDMaterial;
import com.mcmoddev.lib.util.Config.Options;
import com.mcmoddev.lib.util.Oredicts;
import com.mcmoddev.lib.integration.plugins.TinkersConstruct;
import com.mcmoddev.lib.integration.plugins.tinkers.events.TinkersExtraMeltingsEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.mantle.util.RecipeMatch;

/**
 *
 * @author Jasmine Iwanek
 *
 */
@MMDPlugin(addonId = EndMetals.MODID,
pluginId = EMeTinkersConstruct.PLUGIN_MODID,
versions = EMeTinkersConstruct.PLUGIN_MODID + "@[1.12.2-2.7.4.0,);")
public final class EMeTinkersConstruct implements IIntegration {

	public static final String PLUGIN_MODID = TinkersConstruct.PLUGIN_MODID;

	public EMeTinkersConstruct() {
		// do nothing
	}

	@Override
	public void init() {
		TinkersConstruct.INSTANCE.init();
		if (!Options.isModEnabled(EMeTinkersConstruct.PLUGIN_MODID)) {
			return;
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	private boolean registered = false;

	/**
	 *
	 * @param event The Event.
	 */
	@SubscribeEvent
	public void registerMeltings(TinkersExtraMeltingsEvent ev) {
		if (registered) {
			return;
		}
		Materials.getAllMaterials().stream()
		.filter(this::isMaterialEmpty)
		.filter(this::hasOre)
		.filter(this::hasFluid)
		.forEach(material -> {
			final RecipeMatch input = RecipeMatch.of(Oredicts.ORE_END + material.getCapitalizedName(), 576);
			final MeltingRecipe recipe = new MeltingRecipe(input, FluidRegistry.getFluid(material.getName()));
			TinkerRegistry.registerMelting(recipe);
		});
		registered = true;
	}

	private boolean hasFluid(final MMDMaterial material) {
		return FluidRegistry.getFluid(material.getName()) != null;
	}

	private boolean hasOre(final MMDMaterial material) {
		return material.hasBlock(Names.ENDORE);
	}

	private boolean isMaterialEmpty(final MMDMaterial material) {
		return !material.isEmpty();
	}
}
