package net.vercte.jolly.content.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ConfettiParticle extends TextureSheetParticle {
    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square((double)100.0F);
    private float rX = 0;
    private float rZ = 0;
    private final float drX;
    private final float drZ;
    private boolean stoppedByCollision = false;

    private final int color;

    protected ConfettiParticle(ClientLevel clientLevel, double d, double e, double f, double x, double y, double z, SpriteSet spriteSet) {
        super(clientLevel, d, e, f, x, y, z);
        this.gravity = 0.2f;
        this.friction = 1.0f;
        this.quadSize = 1/16f;
        this.xd = x + (Math.random() * 2.0f - 1.0f) * 0.1f;
        this.yd = y + Math.random() * 0.1f;
        this.zd = z + (Math.random() * 2.0f - 1.0f) * 0.1f;
        this.drX = (float)Math.random() * 0.2f - 0.1f;
        this.drZ = (float)Math.random() * 0.2f - 0.1f;

        this.color = getRandomColor();

        this.lifetime = 80 + (int)(this.random.nextFloat() * 40f);
        this.setColor(0, (float) 140/255, 1);
        this.setSpriteFromAge(spriteSet);
    }

    public void tick() {
        super.tick();
        this.xd *= 0.95f;
        this.yd *= 0.9f;
        this.zd *= 0.95f;
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.set(0, camera.rotation().y, 0, camera.rotation().w);

        if(!this.stoppedByCollision && !Minecraft.getInstance().isPaused()) {
            rX = Mth.lerp(partialTicks, rX, rX + drX);
            rZ = Mth.lerp(partialTicks, rZ, rZ + drZ);
        }
        quaternionf.rotateLocalX(rX);
        quaternionf.rotateLocalZ(rZ);

        this.renderRotatedQuad(vertexConsumer, camera, quaternionf, partialTicks);

        quaternionf.rotateLocalX((float)Math.PI);
        quaternionf.rotateLocalZ((float)Math.PI);

        this.renderRotatedQuad(vertexConsumer, camera, quaternionf, partialTicks);
    }

    @Override
    protected void renderRotatedQuad(@NotNull VertexConsumer vertexConsumer, @NotNull Quaternionf quaternionf, float f, float g, float h, float i) {
        float size = this.getQuadSize(i);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(i);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, size, u1, v1, light);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, size, u1, v0, light);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, size, u0, v0, light);
        this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, size, u0, v1, light);
    }

    private void renderVertex(VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n) {
        Vector3f vector3f = (new Vector3f(i, j, 0.0F)).rotate(quaternionf).mul(k).add(f, g, h);
        vertexConsumer.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(l, m).setColor(this.color).setLight(n);
    }

    @Override
    public void move(double d, double e, double f) {
        if (!this.stoppedByCollision) {
            double g = d;
            double h = e;
            double i = f;
            if (this.hasPhysics && (d != (double)0.0F || e != (double)0.0F || f != (double)0.0F) && d * d + e * e + f * f < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
                Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(d, e, f), this.getBoundingBox(), this.level, List.of());
                d = vec3.x;
                e = vec3.y;
                f = vec3.z;
            }

            if (d != (double)0.0F || e != (double)0.0F || f != (double)0.0F) {
                this.setBoundingBox(this.getBoundingBox().move(d, e, f));
                this.setLocationFromBoundingbox();
            }

            if (Math.abs(h) >= (double)1.0E-5F && Math.abs(e) < (double)1.0E-5F) {
                this.stoppedByCollision = true;
            }

            this.onGround = h != e && h < (double)0.0F;
            if (g != d) {
                this.xd = 0.0F;
            }

            if (i != f) {
                this.zd = 0.0F;
            }

        }
    }

    private int getRandomColor() {
        List<Integer> colors = List.of(
                0xff008cff,
                0xfff24f3d,
                0xfff0f0f0,
                0xfff5ef53,
                0xff5bf553,
                0xffeea9ff // ok yoshi
        );
        return colors.get(random.nextInt(colors.size()));
    }

    @Override
    @NotNull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet set) {
            this.sprites = set;
        }

        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new ConfettiParticle(clientLevel, d, e, f, g, h, i, this.sprites);
        }
    }
}
