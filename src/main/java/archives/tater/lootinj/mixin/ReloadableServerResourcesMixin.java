package archives.tater.lootinj.mixin;

import archives.tater.lootinj.impl.LootInjImpl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @WrapOperation(
            method = "loadResources",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries;reload(Lnet/minecraft/core/LayeredRegistryAccess;Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;")
    )
    private static CompletableFuture<ReloadableServerRegistries.LoadResult> loadLootModifications(LayeredRegistryAccess<RegistryLayer> context, List<Registry.PendingTags<?>> updatedContextTags, ResourceManager manager, Executor executor, Operation<CompletableFuture<ReloadableServerRegistries.LoadResult>> original, @Local(argsOnly = true, name = "mainThreadExecutor") Executor mainThreadExecutor) {
        return LootInjImpl.targetsLoader.reload(new PreparableReloadListener.SharedState(manager), executor, CompletableFuture::completedFuture, mainThreadExecutor)
                .thenCompose(_ -> original.call(context, updatedContextTags, manager, executor));
    }
}
