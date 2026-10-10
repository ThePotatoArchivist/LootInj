package archives.tater.lootinj.api;

import archives.tater.lootinj.impl.LootModificationBuilderImpl;
import archives.tater.lootinj.impl.LootModificationImpl;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApiStatus.NonExtendable
public interface LootModification {

    Codec<LootModification> CODEC = LootModificationImpl.CODEC;
    HolderLookup.Provider EMPTY_REGISTRIES = new HolderLookup.Provider() {
        @Override
        public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return Stream.empty();
        }

        @Override
        public <T> Optional<? extends HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
            return Optional.empty();
        }
    };

    void apply(LootTable.Builder builder, HolderLookup.Provider registries);

    @Deprecated
    default void apply(LootTable.Builder builder) {
        apply(builder, EMPTY_REGISTRIES);
    }

    static Builder builder() {
        return new LootModificationBuilderImpl();
    }

    @ApiStatus.NonExtendable
    interface Builder extends FunctionUserBuilder<Builder> {
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

        default Builder pools(Collection<? extends LootPool> pools) {
            for (var pool : pools)
                pool(pool);
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
