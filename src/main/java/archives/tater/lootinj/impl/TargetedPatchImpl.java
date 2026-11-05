package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.TargetedPatch;

import com.mojang.serialization.Codec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public record TargetedPatchImpl<T, P>(
        List<ResourceKey<T>> targets,
        P patch
) implements TargetedPatch<T, P> {
    @Override
    public P patch(Codec<P> codec, RegistryOps.RegistryInfoLookup registries) {
        return ;
    }

    public static <T, P> Codec<TargetedPatch<T, P>> codec()

}
