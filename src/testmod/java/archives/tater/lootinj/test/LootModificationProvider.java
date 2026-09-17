package archives.tater.lootinj.test;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.minecraft.advancements.criterion.ItemPredicate.Builder.item;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.predicates.MatchTool.toolMatches;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;

public class LootModificationProvider extends FabricCodecDataProvider<LootModification> {
    protected LootModificationProvider(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture, LootInj.LOOT_MODIFICATION, LootModification.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, LootModification> provider, HolderLookup.Provider registryLookup) {
        var items = registryLookup.lookupOrThrow(Registries.ITEM);

        provider.accept(LootInj.id("add_diamonds"), LootModification.builder()
                .target(Blocks.DIRT.getLootTable().orElseThrow())
                .pool(lootPool()
                        .add(lootTableItem(Items.DIAMOND)))
                .apply(setCount(exactly(1)))
                .modifyPools(builder -> builder
                        .apply(setCount(exactly(1)))
                        .when(toolMatches(item().of(items, Items.DIAMOND))))
                .build()
        );
    }

    @Override
    public String getName() {
        return "Loot Modifications";
    }
}
