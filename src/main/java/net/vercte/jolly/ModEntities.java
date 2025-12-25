package net.vercte.jolly;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vercte.jolly.content.surprise.SurpriseCreeper;

import java.util.HashMap;
import java.util.function.Supplier;

public class ModEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JollyCore.ID);

    public static final Supplier<EntityType<SurpriseCreeper>> SURPRISE_CREEPER = ENTITY_TYPES.register(
            "surprise_creeper",
            () -> EntityType.Builder.of(SurpriseCreeper::new, MobCategory.MONSTER)
                    .sized(0.8f, 1)
                    .eyeHeight(10/16f)
                    .clientTrackingRange(8)
                    .build("surprise_creeper")
        );

    public static HashMap<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> getAttributeSuppliers() {
        HashMap<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> map = new HashMap<>();
        map.put(SURPRISE_CREEPER.get(), SurpriseCreeper.createAttributes());
        return map;
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
