package archives.tater.lootinj.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public final class LootInj {
	private LootInj() {}

	public static final String MOD_ID = "lootinj";
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static final ResourceKey<Registry<LootModification>> LOOT_MODIFICATION = ResourceKey.createRegistryKey(id("loot_modification"));

	public static final Identifier LOOT_MODIFICATION_TARGETS_LISTENER = id("loot_modification_targets");
}
