package archives.tater.lootinj.impl;

import archives.tater.lootinj.api.LootInj;
import archives.tater.lootinj.api.LootModification;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTable;

import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class LootModificationTargetsManager extends SimpleJsonResourceReloadListener<List<ResourceKey<LootTable>>> {

    private @Unmodifiable Map<ResourceKey<LootTable>, @Unmodifiable List<ResourceKey<LootModification>>> byTarget = Map.of();

    protected LootModificationTargetsManager() {
        super(LootModification.TARGETS_CODEC, FileToIdConverter.registry(LootInj.LOOT_MODIFICATION));
    }

    public List<ResourceKey<LootModification>> getModifications(ResourceKey<LootTable> table) {
        return byTarget.getOrDefault(table, List.of());
    }

    @Override
    protected void apply(Map<Identifier, List<ResourceKey<LootTable>>> preparations, ResourceManager manager, ProfilerFiller profiler) {
        byTarget = preparations.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(target -> Pair.of(target, ResourceKey.create(LootInj.LOOT_MODIFICATION, entry.getKey())))
                )
                .collect(groupingBy(Pair::key, mapping(Pair::value, toUnmodifiableList())));
    }
}
