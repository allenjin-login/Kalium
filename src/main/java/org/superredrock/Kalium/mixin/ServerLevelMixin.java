package org.superredrock.Kalium.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.superredrock.Kalium.parallelised.Pool.BlockPool;
import org.superredrock.Kalium.parallelised.Pool.PoolManager;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    @Shadow @Final protected Raids raids;
    @Shadow @Final private boolean tickTime;
    @Unique
    private BlockPool kalium$BlockPool = PoolManager.mainPool.fork(this, BlockPool::new);

    @Unique
    private final ReentrantLock kalium$lock = new ReentrantLock();

    protected ServerLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);
    }


    @Override
    public void addBlockEntityTicker(@NotNull TickingBlockEntity p_151526_) {
        super.addBlockEntityTicker(p_151526_);
        this.kalium$BlockPool.addTicker(p_151526_);
    }


    @Override
    public void addFreshBlockEntities(@NotNull Collection<BlockEntity> beList) {
        super.addFreshBlockEntities(beList);
        this.kalium$BlockPool.addFleshBlockTicker(beList);
    }

    @Override
    protected void tickBlockEntities() {
        ProfilerFiller profilerfiller = this.getProfiler();
        profilerfiller.push("blockEntities");
        this.kalium$BlockPool.onTick();
        profilerfiller.pop();
    }

    @Inject(method = "close",at = @At("RETURN"))
    public void onClose(CallbackInfo ci){
        this.kalium$BlockPool.shutdownNow();
        this.kalium$BlockPool = null;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos p_46716_) {
        this.kalium$lock.lock();
        if (this.isOutsideBuildHeight(p_46716_)) {
            this.kalium$lock.unlock();
            return null;
        } else {
            this.kalium$lock.unlock();
            return this.isClientSide ? null : this.getChunkAt(p_46716_).getBlockEntity(p_46716_, LevelChunk.EntityCreationType.IMMEDIATE);
        }
    }


    @Override
    public boolean setBlock(@NotNull BlockPos p_46605_, @NotNull BlockState p_46606_, int p_46607_, int p_46608_) {
        //TODO : deadlock easily remove later
        this.kalium$lock.lock();
        boolean result =  super.setBlock(p_46605_, p_46606_, p_46607_, p_46608_);
        this.kalium$lock.unlock();
        return result;
    }

    @Override
    public boolean destroyBlock(@NotNull BlockPos p_46626_, boolean p_46627_, @Nullable Entity p_46628_, int p_46629_) {
        this.kalium$lock.lock();
        boolean result =  super.destroyBlock(p_46626_, p_46627_, p_46628_, p_46629_);
        this.kalium$lock.unlock();
        return result;
    }

    @Override
    public void setBlockEntity(@NotNull BlockEntity p_151524_) {
        this.kalium$lock.lock();
        super.setBlockEntity(p_151524_);
        this.kalium$lock.unlock();
    }

    @Override
    public boolean setBlockAndUpdate(@NotNull BlockPos p_46598_, @NotNull BlockState p_46599_) {
        this.kalium$lock.lock();
        boolean result =  super.setBlockAndUpdate(p_46598_, p_46599_);
        this.kalium$lock.unlock();
        return result;
    }
}
