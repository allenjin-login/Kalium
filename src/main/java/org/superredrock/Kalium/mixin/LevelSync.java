package org.superredrock.Kalium.mixin;


import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(Level.class)
public abstract class LevelSync implements LevelAccessor {



//
//    @Inject(method = "setBlockAndUpdate",at = @At("HEAD"))
//    public void onSetBlockAndUpdateLock(BlockPos p_46598_, BlockState p_46599_, CallbackInfoReturnable<Boolean> cir){
//        kalium$lock.lock();
//    }
//    @Inject(method = "setBlockAndUpdate",at = @At("RETURN"))
//    public void onSetBlockAndUpdateUnlock(BlockPos p_46598_, BlockState p_46599_, CallbackInfoReturnable<Boolean> cir){
//        kalium$lock.unlock();
//    }
//


    //TODO:
    // class : BlockStatus
    // method: markAndNotifyBlock, onBlockStateChange, getHeight, getBlockState, getFluidState


}
