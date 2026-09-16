package archives.tater.lootinj.api;

import archives.tater.lootinj.impl.LootModificationBuilderImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public record LootModification(
        List<ResourceKey<LootTable>> targets,
        List<LootPool> pools,
        List<LootItemFunction> functions,
        Optional<LootPoolPatch> modifyPools
) implements Validatable {
    private static final MapCodec<List<ResourceKey<LootTable>>> TARGETS_MAP_CODEC = compactListCodec(ResourceKey.codec(Registries.LOOT_TABLE), ResourceKey.codec(Registries.LOOT_TABLE).listOf(1, Integer.MAX_VALUE)).fieldOf("targets");

    public static final Codec<List<ResourceKey<LootTable>>> TARGETS_CODEC = TARGETS_MAP_CODEC.codec();

    public static final Codec<LootModification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TARGETS_MAP_CODEC.forGetter(LootModification::targets),
            LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter(LootModification::pools),
            compactListCodec(LootItemFunctions.ROOT_CODEC).optionalFieldOf("modifier", List.of()).forGetter(LootModification::functions),
            LootPoolPatch.CODEC.optionalFieldOf("modify_pools").forGetter(LootModification::modifyPools)
    ).apply(instance, LootModification::new));

    public void apply(LootTable.Builder builder) {
        builder.pools(pools);
        builder.apply(functions);
        modifyPools.ifPresent(patch -> builder.modifyPools(patch::apply));
    }

    public static Builder builder() {
        return new LootModificationBuilderImpl();
    }

    /**
     * Does nothing
     */
    @ApiStatus.Internal
    @Deprecated
    @Override
    public void validate(ValidationContext context) {

    }

    @ApiStatus.NonExtendable
    public interface Builder extends FunctionUserBuilder<Builder> {
        Builder target(ResourceKey<LootTable> target);

        default Builder target(Identifier target) {
            return target(ResourceKey.create(Registries.LOOT_TABLE, target));
        }

        Builder targets(Collection<ResourceKey<LootTable>> targets);

        default Builder targets(Identifier... targets) {
            for (var target : targets)
                target(target);
            return this;
        }

        Builder pool(LootPool pool);

        default Builder pool(LootPool.Builder pool) {
            return pool(pool.build());
        }

        @Deprecated
        default Builder withPool(LootPool.Builder pool) {
            return pool(pool);
        }

        Builder pools(Collection<? extends LootPool> pools);

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

        Builder modifyPools(LootPoolPatch patch);

        default Builder modifyPools(LootPoolPatch.Builder patch) {
            return modifyPools(patch.build());
        }

        default Builder modifyPools(Consumer<LootPoolPatch.Builder> patch) {
            var builder = LootPoolPatch.builder();
            patch.accept(builder);
            return modifyPools(builder);
        }

        LootModification build();

        @Override
        default Builder unwrap() {
            return this;
        }
    }
}
