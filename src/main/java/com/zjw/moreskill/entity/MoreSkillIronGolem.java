package com.zjw.moreskill.entity;

import java.util.EnumSet;
import java.util.UUID;

import javax.annotation.Nullable;

import com.zjw.moreskill.MoreSkill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MoreSkillIronGolem extends IronGolem {
    private static final String OWNER_UUID_TAG = "OwnerUUID";
    private static final String DESPAWN_TICKS_TAG = "DespawnTicks";

    private int despawnTicks = 24000; // 消失时间，20分钟

    public MoreSkillIronGolem(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide) {
            // 移除原版的村庄相关 AI（可选）
            this.goalSelector.getAvailableGoals()
                    .removeIf(goal -> goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.MoveBackToVillageGoal
                            ||
                            goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal
                            ||
                            goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.OfferFlowerGoal);

            // 添加跟随主人目标
            this.goalSelector.addGoal(1, new FollowOwnerGoal(this));
            this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));

            this.targetSelector.removeAllGoals(goal -> true);
            this.targetSelector.addGoal(3,
                    new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
            // 强化攻击：主动寻找所有 Monster 敌对生物
            this.targetSelector.addGoal(1,
                    new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (mobAttack) -> {
                        return mobAttack instanceof Monster || mobAttack instanceof Creeper
                                || mobAttack instanceof Enemy || mobAttack instanceof PowerableMob;
                    }));

        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        MoreSkill.LOGGER.info("尝试输出数据");
        MoreSkill.LOGGER.info(IronGolem.createAttributes().toString());
        return IronGolem.createAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
    }

    // 设置主人
    public void setOwner(@Nullable UUID ownerUUID) {
        if (ownerUUID != null) {
            this.getPersistentData().putUUID(OWNER_UUID_TAG, ownerUUID);
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.getPersistentData().contains(OWNER_UUID_TAG) ? this.getPersistentData().getUUID(OWNER_UUID_TAG)
                : null;
    }

    @Nullable
    public LivingEntity getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid != null && !this.level().isClientSide()) {
            return ((ServerLevel) this.level()).getEntity(uuid) instanceof LivingEntity living ? living : null;
        }
        return null;
    }

    // 重写 canAttackType：禁止攻击主人
    @Override
    public boolean canAttack(LivingEntity target) {
        if (target instanceof Player player) {
            UUID owner = getOwnerUUID();
            if (owner != null && owner.equals(player.getUUID())) {
                return false; // 不攻击主人
            }
        }
        return super.canAttack(target);
    }

    // 自动消失逻辑
    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // 减少倒计时
            if (despawnTicks > 0) {
                despawnTicks--;
                if (despawnTicks <= 0) {
                    this.discard(); // 消失
                }
            }
        }
    }

    // 保存/加载 NBT
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getOwnerUUID() != null) {
            tag.putUUID(OWNER_UUID_TAG, getOwnerUUID());
        }
        tag.putInt(DESPAWN_TICKS_TAG, despawnTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(OWNER_UUID_TAG)) {
            this.getPersistentData().putUUID(OWNER_UUID_TAG, tag.getUUID(OWNER_UUID_TAG));
        }
        if (tag.contains(DESPAWN_TICKS_TAG)) {
            this.despawnTicks = tag.getInt(DESPAWN_TICKS_TAG);
        }
    }

    // 设置存活时间（单位：tick）
    public void setDespawnTicks(int ticks) {
        this.despawnTicks = ticks;
    }

    // ———————————————— 跟随主人 AI ————————————————
    public static class FollowOwnerGoal extends Goal {
        private final MoreSkillIronGolem golem;
        private LivingEntity owner;
        private int timeToRecalcPath;

        public FollowOwnerGoal(MoreSkillIronGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = this.golem.getOwner();
            if (owner == null || owner.isDeadOrDying() || owner.distanceToSqr(golem) < 225.0D) {
                return false;
            }
            this.owner = owner;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.golem.getNavigation().isDone() && this.owner != null && !this.owner.isDeadOrDying()
                    && owner.distanceToSqr(golem) > 16.0D;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void tick() {
            this.golem.getLookControl().setLookAt(this.owner, 10.0F, (float) this.golem.getMaxHeadXRot());
            // 实时检查：如果已经靠得太近，立即停止导航
            if (this.golem.distanceToSqr(this.owner) <= 16.0D) {
                this.golem.getNavigation().stop();
                return;
            }
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                // Move to a position 4 blocks away from the owner
                moveToPositionNearOwner();
            }
        }

        /**
         * Move to a position approximately 4 blocks away from the owner
         */
        private void moveToPositionNearOwner() {
            if (this.owner == null)
                return;

            // Get direction vector from owner to golem
            double dx = this.golem.getX() - this.owner.getX();
            double dy = this.golem.getY() - this.owner.getY();
            double dz = this.golem.getZ() - this.owner.getZ();

            // Calculate distance
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0) {
                // Normalize and scale to 4 blocks away
                double scale = 4.0 / distance;
                double targetX = this.owner.getX() + dx * scale;
                double targetY = this.owner.getY() + dy * scale;
                double targetZ = this.owner.getZ() + dz * scale;

                this.golem.getNavigation().moveTo(targetX, targetY, targetZ, 1.0);
            }
        }
    }

}