package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootModification;
import archives.tater.lootinj.api.LootPoolPatch;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class LootModificationBuilderImpl implements LootModification.Builder {
    private final ImmutableList.Builder<ResourceKey<LootTable>> targets = ImmutableList.builder();
    private final ImmutableList.Builder<LootPool> pools = ImmutableList.builder();
    private final ImmutableList.Builder<Holder<LootItemFunction>> functions = ImmutableList.builder();
    private @Nullable LootPoolPatch modifyPools = null;

    @Override
    public LootModification.Builder target(ResourceKey<LootTable> target) {
        targets.add(target);
        return this;
    }

    @Override
    public LootModification.Builder targets(Collection<ResourceKey<LootTable>> targets) {
        this.targets.addAll(targets);
        return this;
    }

    @Override
    public LootModification.Builder pool(LootPool pool) {
        pools.add(pool);
        return this;
    }

    @Override
    public LootModification.Builder pools(Collection<? extends LootPool> pools) {
        this.pools.addAll(pools);
        return this;
    }

    @Override
    public LootModification.Builder apply(Holder<LootItemFunction> function) {
        functions.add(function);
        return this;
    }

    @Override
    public LootModification.Builder modifyPools(LootPoolPatch patch) {
        if (modifyPools != null) throw new IllegalArgumentException("Already has a loot pool patch");
        modifyPools = patch;
        return this;
    }

    @Override
    public LootModification build() {
        return new LootModification(targets.build(), pools.build(), functions.build(), Optional.ofNullable(modifyPools));
    }
}
