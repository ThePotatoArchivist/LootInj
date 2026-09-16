package archives.tater.lootinj.test;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.advancements.predicates.ItemPredicate.Builder.item;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.predicates.ConditionReference.conditionReference;
import static net.minecraft.world.level.storage.loot.predicates.MatchTool.toolMatches;

public class LootModificationProvider extends FabricDynamicRegistryProvider {
    public LootModificationProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        bootstrap(entriesContext(entries));

        entries.add(ResourceKey.create(LootInj.LOOT_MODIFICATION, LootInj.id("add_diamonds")), LootModification.builder()
                .target(Blocks.DIRT.getLootTable().orElseThrow())
                .pool(lootPool()
                        .add(lootTableItem(Items.DIAMOND))
                        .when(conditionReference(A)))
                .build()
        );
    }

    private static BootstrapContext<LootItemCondition> entriesContext(Entries entries) {
        return new BootstrapContext<>() {
            @Override
            public Holder.Reference<LootItemCondition> register(ResourceKey<LootItemCondition> key, LootItemCondition value, Lifecycle lifecycle) {
                return (Holder.Reference<LootItemCondition>) entries.add(key, value);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return entries.getLookups().lookupOrThrow(key);
            }
        };
    }

    public static final ResourceKey<LootItemCondition> A = ResourceKey.create(Registries.PREDICATE, LootInj.id("a"));

    public static void bootstrap(BootstrapContext<LootItemCondition> context) {
        context.register(A, toolMatches(item().of(context.lookup(Registries.ITEM), Items.DIAMOND)).build());
    }

    @Override
    public String getName() {
        return "Loot Modifications";
    }
}
