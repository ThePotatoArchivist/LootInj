package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootModification;
import archives.tater.lootinj.api.LootPoolPatch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public record LootModificationImpl(
        List<ResourceKey<LootTable>> targets,
        List<LootPool> pools,
        List<Holder<LootItemFunction>> functions,
        Optional<LootPoolPatch> modifyPools
) implements LootModification {
    public static final Codec<LootModification> CODEC = RecordCodecBuilder.<LootModificationImpl>create(instance -> instance.group(
            LootInjImpl.TARGETS_MAP_CODEC.forGetter(LootModificationImpl::targets),
            LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter(LootModificationImpl::pools),
            compactListCodec(LootItemFunctions.CODEC).optionalFieldOf("modifier", List.of()).forGetter(LootModificationImpl::functions),
            LootPoolPatch.CODEC.optionalFieldOf("modify_pools").forGetter(LootModificationImpl::modifyPools)
    ).apply(instance, LootModificationImpl::new)).flatComapMap(Function.identity(), lm -> lm instanceof LootModificationImpl lmi ? DataResult.success(lmi) : DataResult.error(() -> "Expected instance of LootModificationImpl, got " + lm.getClass()));

    @Override
    public void apply(LootTable.Builder builder) {
        builder.pools(pools);
        for (var function : functions) builder.apply(function);
        modifyPools.ifPresent(patch -> builder.modifyPools(patch::apply));
    }

}
