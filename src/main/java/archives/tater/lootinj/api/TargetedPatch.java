package archives.tater.lootinj.api;

import com.mojang.serialization.Codec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface TargetedPatch<T, P> {
    @UnmodifiableView
    Collection<ResourceKey<T>> targets();

    P patch(Codec<P> codec, RegistryOps.RegistryInfoLookup registries);
}
