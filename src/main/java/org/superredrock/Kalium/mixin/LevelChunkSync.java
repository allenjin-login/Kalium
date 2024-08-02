package org.superredrock.Kalium.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(LevelChunk.class)
public abstract class LevelChunkSync extends ChunkAccess  {

    @Unique
    private final ReentrantReadWriteLock kalium$lock = new ReentrantReadWriteLock();
    @Unique
    private final ReentrantReadWriteLock.ReadLock kalium$readLock = this.kalium$lock.readLock();
    @Unique
    private final ReentrantReadWriteLock.WriteLock kalium$writeLock = this.kalium$lock.writeLock();


    public LevelChunkSync(ChunkPos p_187621_, UpgradeData p_187622_, LevelHeightAccessor p_187623_, Registry<Biome> p_187624_, long p_187625_, @Nullable LevelChunkSection[] p_187626_, @Nullable BlendingData p_187627_) {
        super(p_187621_, p_187622_, p_187623_, p_187624_, p_187625_, p_187626_, p_187627_);
    }


    @Inject(method = {"getBlockState","isTicking","getBlockEntities","getLevel","getBlockEntityNbtForSaving"},at = @At("HEAD"))
    public void onGetLock(CallbackInfoReturnable<Map<BlockPos, BlockEntity>> cir){
        this.kalium$readLock.lock();
    }
    @Inject(method = {"getBlockState","isTicking","getBlockEntities","getLevel","getBlockEntityNbtForSaving"},at = @At("RETURN"))
    public void onGetUnLock(CallbackInfoReturnable<Map<BlockPos, BlockEntity>> cir){
        this.kalium$readLock.unlock();
    }


    @Inject(method = {"removeBlockEntity","removeBlockEntityTicker"},at = @At("HEAD"))
    public void onRemoveBlockLock(BlockPos p_62919_, CallbackInfo ci){
        this.kalium$writeLock.lock();
    }
    @Inject(method = {"removeBlockEntity","removeBlockEntityTicker"},at = @At("RETURN"))
    public void onRemoveBlockUnlock(BlockPos p_46748_, CallbackInfo ci){
        this.kalium$writeLock.unlock();
    }

    @Inject(method = {"addAndRegisterBlockEntity","setBlockEntity"},at = @At("HEAD"))
    public void onSetBlockLock(BlockEntity p_156391_, CallbackInfo ci){
        this.kalium$writeLock.lock();
    }
    @Inject(method = {"addAndRegisterBlockEntity","setBlockEntity"},at = @At("RETURN"))
    public void onSetBlockUnlock(BlockEntity p_156391_, CallbackInfo ci){
        this.kalium$writeLock.unlock();
    }

    @Inject(method = {"addGameEventListener","removeGameEventListener"},at = @At("HEAD"))
    public <T extends BlockEntity> void onGameEventListenerLock(T p_223416_, ServerLevel p_223417_, CallbackInfo ci){
        this.kalium$writeLock.lock();
    }
    @Inject(method = {"addGameEventListener","removeGameEventListener"},at = @At("RETURN"))
    public <T extends BlockEntity> void onGameEventListenerUnLock(T p_223416_, ServerLevel p_223417_, CallbackInfo ci){
        this.kalium$writeLock.unlock();
    }





}
