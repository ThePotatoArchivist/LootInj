package archives.tater.lootinj.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.core.Holder$Reference")
public interface HolderReferenceAccessor<T> {
    @Invoker
    void callBindValue(final T value);
}
