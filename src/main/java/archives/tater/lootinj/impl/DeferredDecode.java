package archives.tater.lootinj.impl;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;

public sealed interface DeferredDecode<T> {

    T get(UnaryOperator<DynamicOps<?>> opsModifier);

    default T get() {
        return get(identity());
    }

    static <T> DeferredDecode<T> of(T value) {
        return new Constant<>(value);
    }

    static <A> Codec<DeferredDecode<A>> codec(Codec<A> codec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<DeferredDecode<A>, T>> decode(DynamicOps<T> ops, T input) {
                return DataResult.success(Pair.of(new Deferred<>(new Dynamic<>(ops, input), codec), ops.empty()));
            }

            @Override
            public <T> DataResult<T> encode(DeferredDecode<A> input, DynamicOps<T> ops, T prefix) {
                return switch (input) {
                    case Constant(var value) -> codec.encode(value, ops, prefix);
                    case Deferred(var value, var containedCodec) -> {
                        if (codec != containedCodec) throw new IllegalStateException("Codec contained by Deferred did not match");
                        yield DataResult.success(value.convert(ops).getValue());
                    }
                };
            }
        };
    }

    record Constant<T>(T value) implements DeferredDecode<T> {
        @Override
        public T get(UnaryOperator<DynamicOps<?>> opsModifier) {
            return value;
        }
    }

    record Deferred<T>(Dynamic<?> value, Codec<T> codec) implements DeferredDecode<T> {
        @Override
        public T get(UnaryOperator<DynamicOps<?>> opsModifier) {
            return decode(value, codec, opsModifier).getOrThrow().getFirst();
        }

        @SuppressWarnings("unchecked")
        private static <T, A> DataResult<Pair<T, A>> decode(Dynamic<A> value, Codec<T> codec, UnaryOperator<DynamicOps<?>> opsModifier) {
            return codec.decode((DynamicOps<A>) opsModifier.apply(value.getOps()), value.getValue());
        }
    }
}
