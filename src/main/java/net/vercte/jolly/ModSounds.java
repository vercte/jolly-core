package net.vercte.jolly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, JollyCore.ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SURPRISE_CREEPER_EXPLODES = register("surprise_creeper_explodes");

    public static final DeferredHolder<SoundEvent, SoundEvent> ALL_I_WANT = register("all_i_want");
    public static final ResourceKey<JukeboxSong> ALL_I_WANT_KEY = createSong("all_i_want");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(JollyCore.at(id)));
    }

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, JollyCore.at(name));
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
