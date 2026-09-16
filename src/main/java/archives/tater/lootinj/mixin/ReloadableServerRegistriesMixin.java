package archives.tater.lootinj.mixin;

import archives.tater.lootinj.impl.LootInjImpl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.Validatable;

import com.google.gson.JsonElement;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {

    /// @see net.fabricmc.fabric.mixin.loot.ReloadableServerRegistriesMixin
    @WrapOperation(
            method = "scheduleRegistryLoad",
            at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"),
            order = 1100
    )
    private static <T extends Validatable, U> CompletableFuture<U> saveRegistryLookup(Supplier<U> supplier, Executor executor, Operation<CompletableFuture<U>> original, LootDataType<T> type, RegistryOps<JsonElement> ops) {
        return ScopedValue.where(LootInjImpl.LOOT_REGISTRIES, ops::getter).call(() -> original.call(supplier, executor));
    }
}
