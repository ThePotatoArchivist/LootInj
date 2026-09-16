package archives.tater.lootinj.api;

import archives.tater.lootinj.impl.LootPoolPatchBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public record LootPoolPatch(
        List<LootPoolEntryContainer> entries,
        List<Holder<LootItemCondition>> conditions,
        List<Holder<LootItemFunction>> functions
) {
    public static final Codec<LootPoolPatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootPoolEntries.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(LootPoolPatch::entries),
            compactListCodec(LootItemCondition.CODEC).optionalFieldOf("condition", List.of()).forGetter(LootPoolPatch::conditions),
            compactListCodec(LootItemFunctions.CODEC).optionalFieldOf("modifier", List.of()).forGetter(LootPoolPatch::functions)
    ).apply(instance, LootPoolPatch::new));

    public void apply(LootPool.Builder builder) {
        builder.add(entries);
        builder.when(conditions, holder -> holder::value);
        builder.apply(functions, holder -> holder::value);
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

        default Builder addAll(List<? extends UniformContainerBase.Builder<?>> entries) {
            for (var entry : entries)
                add(entry);
            return this;
        }

        @Override
        Builder when(Holder<LootItemCondition> condition);

        default Builder when(LootItemCondition condition) {
            return when(Holder.direct(condition));
        }

        default Builder when(Collection<? extends LootItemCondition> conditions) {
            for (var condition : conditions)
                when(condition);
            return this;
        }

        @Override
        Builder apply(Holder<LootItemFunction> function);

        default Builder apply(LootItemFunction function) {
            return apply(Holder.direct(function));
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
