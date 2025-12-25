package net.vercte.jolly;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import java.util.HashMap;
import java.util.Map;

@Mod(JollyCore.ID)
public class JollyCore {
    public static final String ID = "jolly";

    public JollyCore(IEventBus modEventBus) {
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);

        modEventBus.addListener(ModItems::addToCreative);
        modEventBus.addListener(JollyCore::registerEntityAttributes);
        NeoForge.EVENT_BUS.addListener(JollyCore::replaceCreepers);

        modEventBus.addListener(JollyCoreClient::onClientSetup);
        modEventBus.addListener(JollyCoreClient::registerParticles);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        HashMap<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> map = ModEntities.getAttributeSuppliers();
        for(Map.Entry<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> entry : map.entrySet()) {
            event.put(entry.getKey(), entry.getValue().build());
        }
    }

    public static void replaceCreepers(FinalizeSpawnEvent event) {
        boolean isNatural = event.getSpawnType().equals(MobSpawnType.NATURAL);
        if(!isNatural) return;

        boolean isCreeper = event.getEntity().getType().equals(EntityType.CREEPER);
        if(!isCreeper) return;

        ModEntities.SURPRISE_CREEPER.get().spawn(event.getLevel().getLevel(), event.getEntity().blockPosition(), MobSpawnType.NATURAL);

        event.setSpawnCancelled(true);
    }

    public static ResourceLocation at(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
