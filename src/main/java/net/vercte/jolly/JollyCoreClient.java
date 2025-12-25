package net.vercte.jolly;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.vercte.jolly.content.particle.ConfettiParticle;
import net.vercte.jolly.content.surprise.SurpriseCreeperModel;
import net.vercte.jolly.content.surprise.SurpriseCreeperRenderer;

public class JollyCoreClient {
    public static void onClientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.SURPRISE_CREEPER.get(), SurpriseCreeperRenderer.getProvider());
        ClientHooks.registerLayerDefinition(SurpriseCreeperRenderer.LAYER, SurpriseCreeperModel::createBodyLayer);
    }

    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CONFETTI.get(), ConfettiParticle.Provider::new);
    }
}
