package net.vercte.jolly.content.surprise;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.vercte.jolly.JollyCore;
import org.jetbrains.annotations.NotNull;

public class SurpriseCreeperRenderer extends MobRenderer<SurpriseCreeper, SurpriseCreeperModel> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(JollyCore.at("surprise_creeper"), "main");

    public SurpriseCreeperRenderer(EntityRendererProvider.Context context, SurpriseCreeperModel entityModel) {
        super(context, entityModel, 0.7f);
    }

    public static EntityRendererProvider<SurpriseCreeper> getProvider() {
        return m -> new SurpriseCreeperRenderer(m, new SurpriseCreeperModel(
                m.bakeLayer(LAYER)
        ));
    }

    @Override
    protected void scale(SurpriseCreeper creeper, PoseStack poseStack, float partial) {
        float swelling = creeper.getSwelling(partial);
        float h = 1.0F + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;
        swelling *= swelling;
        float i = (1.0F + swelling * 0.4F) * h;
        float j = (1.0F + swelling * 0.1F) / h;
        poseStack.scale(i, j, i);
    }

    @Override
    protected float getWhiteOverlayProgress(SurpriseCreeper creeper, float f) {
        float g = creeper.getSwelling(f);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(g, 0.5F, 1.0F);
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull SurpriseCreeper entity) {
        return JollyCore.at("textures/entity/surprise_creeper/surprise_creeper.png");
    }
}