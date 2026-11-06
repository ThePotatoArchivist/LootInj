package archives.tater.lootinj.mixin;

import archives.tater.lootinj.impl.LootInjImpl;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerRegistries;

import java.util.concurrent.CompletableFuture;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    @ModifyExpressionValue(
            method = "reload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryDataLoader;load(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/List;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    )
    private static CompletableFuture<RegistryAccess.Frozen> modifyLootTables(CompletableFuture<RegistryAccess.Frozen> original) {
        return original.thenApply(registries -> {
            LootInjImpl.finalizeLootTables(registries);
            return registries;
        });
    }
}
