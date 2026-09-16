package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootInjImpl implements ModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(LootInj.MOD_ID);

    public static final LootModificationTargetsManager targetsLoader = new LootModificationTargetsManager();

    public static final LootDataType<LootModification> LOOT_DATA_TYPE = new LootDataType<>(LootInj.LOOT_MODIFICATION, LootModification.CODEC, LootDataType.ContextGetter.constant(LootContextParamSets.EMPTY));

    public static final ScopedValue<HolderGetter.Provider> LOOT_REGISTRIES = ScopedValue.newInstance();

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            for (var modificationKey : targetsLoader.getModifications(key))
                LOOT_REGISTRIES.get().getOrThrow(modificationKey).value().apply(tableBuilder);
        });
    }

}
