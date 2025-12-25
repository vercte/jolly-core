package net.vercte.jolly.content.surprise;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SurpriseCreeperSwellGoal extends Goal {
    private final SurpriseCreeper creeper;
    @Nullable
    private LivingEntity target;

    public SurpriseCreeperSwellGoal(SurpriseCreeper creeper) {
        this.creeper = creeper;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        LivingEntity livingEntity = this.creeper.getTarget();
        return this.creeper.getSwellDirection() > 0 || livingEntity != null && this.creeper.distanceToSqr(livingEntity) < 9;
    }

    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    public void stop() {
        this.target = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDirection(-1);
        } else if (this.creeper.distanceToSqr(this.target) > 49) {
            this.creeper.setSwellDirection(-1);
        } else if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
            this.creeper.setSwellDirection(-1);
        } else {
            this.creeper.setSwellDirection(1);
        }
    }
}
