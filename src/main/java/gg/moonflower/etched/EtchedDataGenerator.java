package gg.moonflower.etched;

import gg.moonflower.etched.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EtchedDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(EtchedBlockTagsProvider::new);
		pack.addProvider(EtchedItemTagsProvider::new);
		pack.addProvider(EtchedEntityTypeTagsProvider::new);
		pack.addProvider(EtchedPointOfInterestTagsProvider::new);
		pack.addProvider(EtchedRecipeProvider::new);
		pack.addProvider(EtchedBlockLootProvider::new);
		pack.addProvider(EtchedModelProvider::new);
	}
}
