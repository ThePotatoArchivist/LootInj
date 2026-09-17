package archives.tater.lootinj.test;

import archives.tater.lootinj.api.LootInj;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.minecraft.advancements.criterion.ItemPredicate.Builder.item;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.predicates.MatchTool.toolMatches;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;

public class TestLootProvider extends SimpleFabricLootTableSubProvider {
    private final CompletableFuture<HolderLookup.Provider> registryLookupFuture;

    public TestLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture, LootContextParamSets.ALL_PARAMS);
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        var items = registryLookupFuture.join().lookupOrThrow(Registries.ITEM);

        output.accept(ResourceKey.create(Registries.LOOT_TABLE, LootInj.id("test")), lootTable()
                .withPool(lootPool()
                        .apply(setCount(exactly(1)))
                        .when(toolMatches(item().of(items, Items.DIAMOND)))));
    }
}
