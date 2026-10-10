package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootModification;
import archives.tater.lootinj.api.LootPoolPatch;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public record LootModificationImpl(
        List<ResourceKey<LootTable>> targets,
        List<DeferredDecode<LootPool>> pools,
        List<DeferredDecode<Holder<LootItemFunction>>> functions,
        Optional<DeferredDecode<LootPoolPatch>> modifyPools
) implements LootModification {
    public static final Codec<LootModification> CODEC = RecordCodecBuilder.<LootModificationImpl>create(instance -> instance.group(
            LootInjImpl.TARGETS_MAP_CODEC.forGetter(LootModificationImpl::targets),
            DeferredDecode.codec(LootPool.CODEC).listOf().optionalFieldOf("pools", List.of()).forGetter(LootModificationImpl::pools),
            compactListCodec(DeferredDecode.codec(LootItemFunctions.CODEC)).optionalFieldOf("modifier", List.of()).forGetter(LootModificationImpl::functions),
            DeferredDecode.codec(LootPoolPatch.CODEC).optionalFieldOf("modify_pools").forGetter(LootModificationImpl::modifyPools)
    ).apply(instance, LootModificationImpl::new)).flatComapMap(Function.identity(), lm -> lm instanceof LootModificationImpl lmi ? DataResult.success(lmi) : DataResult.error(() -> "Expected instance of LootModificationImpl, got " + lm.getClass()));

    @Override
    public void apply(LootTable.Builder builder, HolderLookup.Provider registries) {
        var opsModifier = (UnaryOperator<DynamicOps<?>>) ops -> RegistryOps.create(ops, registries);
        for (var pool : pools) builder.pool(pool.get(opsModifier));
        for (var function : functions) builder.apply(function.get(opsModifier));
        modifyPools.ifPresent(patch -> builder.modifyPools(patch.get(opsModifier)::apply));
    }

}
