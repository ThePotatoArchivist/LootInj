package archives.tater.lootinj.mixin;

import archives.tater.lootinj.impl.LootInjImpl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.level.storage.loot.LootDataType;

import java.util.stream.Stream;

@Mixin(LootDataType.class)
public class LootDataTypeMixin {
    @ModifyReturnValue(
            method = "values",
            at = @At("RETURN")
    )
    private static Stream<LootDataType<?>> addLootModifications(Stream<LootDataType<?>> original) {
        return Stream.concat(original, Stream.of(LootInjImpl.LOOT_DATA_TYPE));
    }
}
