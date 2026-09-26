package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootPoolPatch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public record LootPoolPatchImpl(
        List<LootPoolEntryContainer> entries,
        List<Holder<LootItemCondition>> conditions,
        List<Holder<LootItemFunction>> functions
) implements LootPoolPatch {

    public static final Codec<LootPoolPatch> CODEC = RecordCodecBuilder.<LootPoolPatchImpl>create(instance -> instance.group(
            LootPoolEntries.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(LootPoolPatchImpl::entries),
            compactListCodec(LootItemCondition.CODEC).optionalFieldOf("condition", List.of()).forGetter(LootPoolPatchImpl::conditions),
            compactListCodec(LootItemFunctions.CODEC).optionalFieldOf("modifier", List.of()).forGetter(LootPoolPatchImpl::functions)
    ).apply(instance, LootPoolPatchImpl::new)).flatComapMap(Function.identity(), p -> p instanceof LootPoolPatchImpl pi ? DataResult.success(pi) : DataResult.error(() -> "Expected instance of LootPoolPatchImpl, got " + p.getClass()));

    @Override
    public void apply(LootPool.Builder builder) {
        builder.add(entries);
        for (var condition : conditions) builder.when(condition);
        for (var function : functions) builder.apply(function);
    }

}
