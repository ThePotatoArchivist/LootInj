package archives.tater.lootinj.impl;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.PlaceholderLookupProvider;

public final class PlaceholderCodec<A> implements Codec<A> {
    private final Codec<A> codec;

    public PlaceholderCodec(Codec<A> codec) {
        this.codec = codec;
    }

    @Override
    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        return codec.decode(new PlaceholderLookupProvider(). ops, input);
    }

    @Override
    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return codec.encode(input, ops, prefix);
    }

    public static <A> PlaceholderCodec<A> of(Codec<A> codec) {
        return new PlaceholderCodec<>(codec);
    }
}
