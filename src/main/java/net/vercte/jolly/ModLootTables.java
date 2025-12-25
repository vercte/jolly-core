package net.vercte.jolly;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> SURPRISE_CREEPER = register("entites/surprise_creeper");
    public static final ResourceKey<LootTable> SURPRISE_CREEPER_COMMON_GIFTS = register("gameplay/surprise_creeper/common_gifts");
    public static final ResourceKey<LootTable> SURPRISE_CREEPER_RARE_GIFTS = register("gameplay/surprise_creeper/rare_gifts");

    private static ResourceKey<LootTable> register(String id) {
        return ResourceKey.create(Registries.LOOT_TABLE, JollyCore.at(id));
    }
}
