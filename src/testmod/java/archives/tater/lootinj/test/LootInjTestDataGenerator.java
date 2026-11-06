package archives.tater.lootinj.test;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.EmptyTagLookupWrapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootInjTestDataGenerator implements DataGeneratorEntrypoint {
    static Holder<LootTable> createRef(HolderLookup.RegistryLookup<LootTable> registry, ResourceKey<LootTable> recipeResourceKey) {
        return Holder.Reference.createStandAlone(registry instanceof EmptyTagLookupWrapper<LootTable>(var parent) ? parent : registry, recipeResourceKey);
    }

    @Override
    public void buildReloadableRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.PREDICATE, TestLootModificationProvider::bootstrap);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(TestLootModificationProvider::new);
        pack.addProvider(TestLootTableProvider::new);
    }
}
