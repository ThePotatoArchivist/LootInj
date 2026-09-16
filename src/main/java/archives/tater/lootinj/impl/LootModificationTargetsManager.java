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

public class LootModificationTargetsManager extends SimpleJsonResourceReloadListener<LootModification> {

    private @Unmodifiable Map<ResourceKey<LootTable>, @Unmodifiable List<LootModification>> byTarget = Map.of();

    protected LootModificationTargetsManager() {
        super(LootModification.CODEC, FileToIdConverter.registry(LootInj.LOOT_MODIFICATION));
    }

    public List<LootModification> getModifications(ResourceKey<LootTable> table) {
        return byTarget.getOrDefault(table, List.of());
    }

    @Override
    protected void apply(Map<Identifier, LootModification> preparations, ResourceManager manager, ProfilerFiller profiler) {
        byTarget = preparations.values().stream()
                .flatMap(modification -> modification.targets().stream()
                        .map(target -> Pair.of(target, modification))
                )
                .collect(groupingBy(Pair::key, mapping(Pair::value, toUnmodifiableList())));
    }
}
