package net.vercte.jolly.content.surprise;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.vercte.jolly.ModLootTables;
import net.vercte.jolly.ModParticles;
import net.vercte.jolly.ModSounds;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SurpriseCreeper extends Monster {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(SurpriseCreeper.class, EntityDataSerializers.INT);
    private int oldSwell;
    private int swell;
    private final int maxSwell;

    public SurpriseCreeper(EntityType<SurpriseCreeper> entityType, Level level) {
        super(entityType, level);
        this.oldSwell = 0;
        this.swell = 0;
        this.maxSwell = 30;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SurpriseCreeperSwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0F, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0F, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.MAX_HEALTH, 14);
    }

    public void setSwellDirection(int direction) {
        this.entityData.set(DATA_SWELL_DIR, direction);
    }

    public int getSwellDirection() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder arg) {
        super.defineSynchedData(arg);
        arg.define(DATA_SWELL_DIR, -1);
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, this.oldSwell, this.swell) / (this.maxSwell - 2);
    }

    @Override
    public void tick() {
        if(this.isAlive()) {
            this.oldSwell = this.swell;

            int s = this.getSwellDirection();
            if (s > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }

            this.swell += s;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explode();
            }
        }

        super.tick();
    }

    private void explode() {
        if(!this.level().isClientSide() && this.level() instanceof ServerLevel level) {
            this.dead = true;

            level.sendParticles(ModParticles.CONFETTI.get(), this.getX(), this.getY() + 1, this.getZ(), 256, 0, 0,0, 0.15);
            this.playSound(ModSounds.SURPRISE_CREEPER_EXPLODES.get(), 2, 1 + (float)(Math.random() * 0.14f - 0.07f));

            this.dropPresents(level);

            this.triggerOnDeathMobEffects(RemovalReason.KILLED);
            this.discard();
        }
    }

    private void dropPresents(ServerLevel level) {
        double factor = level.random.nextFloat();

        if(factor < 0.10) {
            dropLoot(level, ModLootTables.SURPRISE_CREEPER_RARE_GIFTS);
        } else if(factor < 0.60) {
            dropLoot(level, ModLootTables.SURPRISE_CREEPER_COMMON_GIFTS);
        } else {
            spawnEntity(level);
        }
    }

    private void spawnEntity(ServerLevel level) {
        List<EntityType<?>> SPAWNABLE_ENTITIES = List.of(
                EntityType.CHICKEN, EntityType.SLIME, EntityType.RABBIT, EntityType.ENDERMITE
        );

        int index = level.random.nextInt(SPAWNABLE_ENTITIES.size());
        Entity entity = SPAWNABLE_ENTITIES.get(index).create(level, e -> {}, this.blockPosition(), MobSpawnType.MOB_SUMMONED, true, true);
        if(entity instanceof Slime slime) {
            slime.setSize(1, false);
        }

        assert entity != null;
        level.addFreshEntity(entity);
    }

    private void dropLoot(ServerLevel level, ResourceKey<LootTable> tableKey) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .create(LootContextParamSets.GIFT);

        table.getRandomItems(params, this.getLootTableSeed(), stack -> this.spawnAtLocation(stack, 0.5f));
    }

    @NotNull
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.CREEPER_HURT;
    }

    @NotNull
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }
}