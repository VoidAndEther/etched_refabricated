package gg.moonflower.etched;

import gg.moonflower.etched.api.sound.download.SoundSourceManager;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.block.entity.EtchedBlockEntities;
import gg.moonflower.etched.registry.component.EtchedComponents;
import gg.moonflower.etched.registry.entity.EtchedEntities;
import gg.moonflower.etched.registry.item.EtchedItems;
import gg.moonflower.etched.registry.menu.EtchedMenuTypes;
import gg.moonflower.etched.registry.network.EtchedMessages;
import gg.moonflower.etched.registry.poi.EtchedPoiTypes;
import gg.moonflower.etched.registry.recipe.EtchedRecipes;
import gg.moonflower.etched.registry.sound.EtchedSounds;
import gg.moonflower.etched.registry.sound.download.BandcampSource;
import gg.moonflower.etched.registry.sound.download.SoundCloudSource;
import gg.moonflower.etched.registry.profession.EtchedVillagerProfessions;
import gg.moonflower.etched.registry.event.EtchedEvents;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Etched implements ModInitializer {
	public static final String MOD_ID = "etched";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SoundSourceManager.register(new SoundCloudSource());
		SoundSourceManager.register(new BandcampSource());
		EtchedItems.register();
		EtchedComponents.register();
		EtchedBlocks.register();
		EtchedBlockEntities.register();
		EtchedEntities.register();
		EtchedMenuTypes.register();
		EtchedRecipes.register();
		EtchedSounds.register();
		EtchedPoiTypes.register();
		EtchedVillagerProfessions.register();
		EtchedMessages.register();
		EtchedEvents.register();
		EtchedConfig.INSTANCE = EtchedConfig.deserialize();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
