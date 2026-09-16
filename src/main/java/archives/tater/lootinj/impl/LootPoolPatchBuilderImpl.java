package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootPoolPatch;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import com.google.common.collect.ImmutableList;

public class LootPoolPatchBuilderImpl implements LootPoolPatch.Builder {
    private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
    private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();
    private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();

    @Override
    public LootPoolPatch.Builder add(LootPoolEntryContainer entry) {
        entries.add(entry);
        return this;
    }

    @Override
    public LootPoolPatch.Builder when(LootItemCondition condition) {
        conditions.add(condition);
        return this;
    }

    @Override
    public LootPoolPatch.Builder apply(LootItemFunction function) {
        functions.add(function);
        return this;
    }

    @Override
    public LootPoolPatch build() {
        return new LootPoolPatch(entries.build(), conditions.build(), functions.build());
    }
}
