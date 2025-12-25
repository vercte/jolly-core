package net.vercte.jolly;

import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JollyCore.ID);

    public static final DeferredItem<DeferredSpawnEggItem> SURPRISE_CREEPER_SPAWN_EGG = ITEMS.register(
            "surprise_creeper_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SURPRISE_CREEPER, 0xFFE33F3F, 0xFFECF8FD, new Item.Properties())
    );

    public static final DeferredItem<Item> MUSIC_DISC_ALL_I_WANT = ITEMS.register(
            "music_disc_all_i_want",
            () -> new Item(
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(ModSounds.ALL_I_WANT_KEY)
            )
    );

    public static void addToCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.insertAfter(new ItemStack(Items.MUSIC_DISC_11), MUSIC_DISC_ALL_I_WANT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.insertAfter(new ItemStack(Items.CREEPER_SPAWN_EGG), SURPRISE_CREEPER_SPAWN_EGG.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
