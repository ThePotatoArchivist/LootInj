package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

import net.minecraft.server.packs.PackType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootInjImpl implements ModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(LootInj.MOD_ID);

    private static final LootModificationTargetsLoader targetsLoader = new LootModificationTargetsLoader();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        DynamicRegistries.registerReloadable(LootInj.LOOT_MODIFICATION, LootModification.CODEC);

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(LootInj.LOOT_MODIFICATION_TARGETS_LISTENER, targetsLoader);

        LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            for (var modificationKey : targetsLoader.getModifications(key)) {
                holder.getOrThrow(modificationKey).value().apply(tableBuilder);
            }
        });
    }

}
