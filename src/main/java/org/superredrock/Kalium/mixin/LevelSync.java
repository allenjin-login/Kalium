package org.superredrock.Kalium.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.locks.ReentrantLock;


@Mixin(Level.class)
public abstract class LevelSync implements LevelAccessor {
    @Shadow @Final public boolean isClientSide;

    @Shadow public abstract LevelChunk getChunkAt(BlockPos p_46746_);

    @Unique
    private final ReentrantLock kalium$lock = new ReentrantLock();

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",at = @At("HEAD"))
    public void onSetBlockLock(BlockPos p_46605_, BlockState p_46606_, int p_46607_, int p_46608_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.lock();
    }
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",at = @At("RETURN"))
    public void onSetBlockUnlock(BlockPos p_46605_, BlockState p_46606_, int p_46607_, int p_46608_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.unlock();
    }

    @Inject(method = "destroyBlock",at = @At("HEAD"))
    public void onDestroyBlockLock(BlockPos p_46626_, boolean p_46627_, Entity p_46628_, int p_46629_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.lock();
    }
    @Inject(method = "destroyBlock",at = @At("RETURN"))
    public void onDestroyBlockUnlock(BlockPos p_46626_, boolean p_46627_, Entity p_46628_, int p_46629_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.unlock();
    }

    @Inject(method = "setBlockAndUpdate",at = @At("HEAD"))
    public void onSetBlockAndUpdateLock(BlockPos p_46598_, BlockState p_46599_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.lock();
    }
    @Inject(method = "setBlockAndUpdate",at = @At("RETURN"))
    public void onSetBlockAndUpdateUnlock(BlockPos p_46598_, BlockState p_46599_, CallbackInfoReturnable<Boolean> cir){
        kalium$lock.unlock();
    }

    @Inject(method = "setBlockEntity",at = @At("HEAD"))
    public void onSetBlockEntityLock(BlockEntity p_151524_, CallbackInfo ci){
        kalium$lock.lock();
    }
    @Inject(method = "setBlockEntity",at = @At("RETURN"))
    public void onRemoveBlockEntityUnlock(BlockEntity p_151524_, CallbackInfo ci){
        kalium$lock.unlock();
    }

    @Inject(method = "removeBlockEntity",at = @At("HEAD"))
    public void onRemoveBlockEntityLock(BlockPos p_46748_, CallbackInfo ci){
        kalium$lock.lock();
    }
    @Inject(method = "removeBlockEntity",at = @At("RETURN"))
    public void onRemoveBlockEntityUnlock(BlockPos p_46748_, CallbackInfo ci){
        kalium$lock.unlock();
    }

    /**
     * @author superredrock
     * @reason allow other thread to tick
     */
    @Nullable
    @Overwrite
    public BlockEntity getBlockEntity(@NotNull BlockPos p_46716_) {
        if (this.isOutsideBuildHeight(p_46716_)) {
            return null;
        } else {
            return this.isClientSide ? null : this.getChunkAt(p_46716_).getBlockEntity(p_46716_, LevelChunk.EntityCreationType.IMMEDIATE);
        }
    }

    //TODO:
    // class : LevelChunk, BlockSta
    // method: markAndNotifyBlock, onBlockStateChange, getHeight, getBlockState, getFluidState



}
