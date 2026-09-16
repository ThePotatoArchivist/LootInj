package archives.tater.lootinj.test;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class LootInjTestDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void buildReloadableRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.PREDICATE, LootModificationProvider::bootstrap);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(LootModificationProvider::new);
    }
}
