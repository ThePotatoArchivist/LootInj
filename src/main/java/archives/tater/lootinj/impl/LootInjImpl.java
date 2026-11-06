package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;
import archives.tater.lootinj.mixin.HolderReferenceAccessor;
import archives.tater.lootinj.mixin.MappedRegistryAccessor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public class LootInjImpl implements ModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(LootInj.MOD_ID);

    public static final MapCodec<List<ResourceKey<LootTable>>> TARGETS_MAP_CODEC = compactListCodec(ResourceKey.codec(Registries.LOOT_TABLE), ResourceKey.codec(Registries.LOOT_TABLE).listOf(1, Integer.MAX_VALUE)).fieldOf("targets");

    @SuppressWarnings("unchecked")
    public static <T> void reassignHolder(MappedRegistry<T> registry, Holder.Reference<T> holder, T newValue) {
        var oldValue = holder.value();
        ((HolderReferenceAccessor<T>) holder).callBindValue(newValue);

        var access = (MappedRegistryAccessor<T>) registry;
        if (access.getByValue().remove(oldValue) != holder)
            throw new IllegalStateException("Invalid");
        access.getByValue().put(newValue, holder);
        access.getToId().put(newValue, access.getToId().removeInt(oldValue));
    }

    public static void finalizeLootTables(RegistryAccess registries) {
        var lootTables = registries.lookupOrThrow(Registries.LOOT_TABLE);
        if (!(lootTables instanceof MappedRegistry<LootTable> registry)) return;

        var builders = new HashMap<Holder.Reference<LootTable>, LootTable.Builder>();

        for (var modification : registries.lookupOrThrow(LootInj.LOOT_MODIFICATION))
            for (var key : modification.targets()) {
                var holder = lootTables.get(key).orElse(null);
                if (holder == null) continue;

                var builder = builders.computeIfAbsent(holder, holder1 -> FabricLootTableBuilder.copyOf(holder1.value()));

                modification.apply(builder);
            }

        builders.forEach((holder, builder) ->
                reassignHolder(registry, holder, builder.build())
        );
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        DynamicRegistries.registerReloadable(LootInj.LOOT_MODIFICATION, LootModification.CODEC);
    }

}
