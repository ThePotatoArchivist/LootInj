package archives.tater.lootinj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {
    @Accessor
    Map<T, Holder.Reference<T>> getByValue();

    @Accessor
    Reference2IntMap<T> getToId();
}
