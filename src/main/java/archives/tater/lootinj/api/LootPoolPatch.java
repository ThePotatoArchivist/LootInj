package archives.tater.lootinj.api;

import archives.tater.lootinj.impl.LootPoolPatchBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

public record LootPoolPatch(
        List<LootPoolEntryContainer> entries,
        List<LootItemCondition> conditions,
        List<LootItemFunction> functions
) {
    public static final Codec<LootPoolPatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootPoolEntries.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(LootPoolPatch::entries),
            LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(LootPoolPatch::conditions),
            LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(LootPoolPatch::functions)
    ).apply(instance, LootPoolPatch::new));

    public void apply(LootPool.Builder builder) {
        builder.add(entries);
        builder.when(conditions);
        builder.apply(functions);
    }

    public static Builder builder() {
        return new LootPoolPatchBuilderImpl();
    }

    @ApiStatus.NonExtendable
    public interface Builder extends FunctionUserBuilder<Builder>, ConditionUserBuilder<Builder> {

        Builder add(LootPoolEntryContainer entry);

        default Builder add(LootPoolEntryContainer.Builder<?> entry) {
            return add(entry.build());
        }

        default Builder add(Collection<? extends LootPoolEntryContainer> entries) {
            for (var entry : entries)
                add(entry);
            return this;
        }

        default Builder addAll(List<? extends LootPoolSingletonContainer.Builder<?>> entries) {
            for (var entry : entries)
                add(entry);
            return this;
        }

        Builder when(LootItemCondition condition);

        @Override
        default Builder when(LootItemCondition.Builder builder) {
            return when(builder.build());
        }

        default Builder when(Collection<? extends LootItemCondition> conditions) {
            for (var condition : conditions)
                when(condition);
            return this;
        }

        Builder apply(LootItemFunction function);

        @Override
        default Builder apply(LootItemFunction.Builder builder) {
            return apply(builder.build());
        }

        default Builder apply(Collection<? extends LootItemFunction> functions) {
            for (var function : functions)
                apply(function);
            return this;
        }

        LootPoolPatch build();

        @Override
        default Builder unwrap() {
            return this;
        }
    }
}
