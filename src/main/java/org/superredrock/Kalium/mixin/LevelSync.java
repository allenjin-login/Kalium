package org.superredrock.Kalium.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.locks.ReentrantLock;


@Mixin(Level.class)
public abstract class LevelSync {
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

}
