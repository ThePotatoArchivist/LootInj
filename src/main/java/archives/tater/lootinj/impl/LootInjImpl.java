package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootInj;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.util.ExtraCodecs.compactListCodec;

public class LootInjImpl implements ModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(LootInj.MOD_ID);

    public static final MapCodec<List<ResourceKey<LootTable>>> TARGETS_MAP_CODEC = compactListCodec(ResourceKey.codec(Registries.LOOT_TABLE), ResourceKey.codec(Registries.LOOT_TABLE).listOf(1, Integer.MAX_VALUE)).fieldOf("targets");

    public static final LootModificationTargetsManager targetsLoader = new LootModificationTargetsManager();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            for (var modification : targetsLoader.getModifications(key))
                modification.apply(tableBuilder);
        });
    }

}
