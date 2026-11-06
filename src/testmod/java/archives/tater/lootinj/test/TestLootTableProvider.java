package archives.tater.lootinj.test;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.entries.NestedLootTable.lootTableReference;

public class TestLootTableProvider extends FabricBlockLootSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public TestLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public void generate() {
        var registries = registryLookupFuture.join();
        var registry = registries.lookupOrThrow(Registries.LOOT_TABLE);

        add(Blocks.MUD, lootTable()
                .withPool(lootPool()
                        .add(lootTableReference(LootInjTestDataGenerator.createRef(registry, Blocks.DIRT.getLootTable().orElseThrow())))
                )
        );
    }
}
