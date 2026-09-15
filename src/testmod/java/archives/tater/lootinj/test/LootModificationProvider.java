package archives.tater.lootinj.test;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;

public class LootModificationProvider extends FabricDynamicRegistryProvider {
    public LootModificationProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.add(ResourceKey.create(LootInj.LOOT_MODIFICATION, LootInj.id("add_diamonds")), new LootModification(
                List.of(
                        BuiltInLootTables.BASTION_BRIDGE
                ),
                List.of(
                        lootPool()
                                .add(lootTableItem(Items.DIAMOND))
                                .build()
                ),
                List.of(),
                Optional.empty()
        ));
    }

    @Override
    public String getName() {
        return "Loot Modifications";
    }
}
