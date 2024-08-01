package org.superredrock.Kalium.mixin;


import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(LegacyRandomSource.class)
public class LegancyRandomSourceSyncMixin {
    @Unique
    private final ReentrantLock internalLock = new ReentrantLock(false);


    @Inject(method = {"next"},at = @At("HEAD"))
    public void onEnter(int p_188581_, CallbackInfoReturnable<Integer> cir){
        this.internalLock.lock();
    }


    @Inject(method = {"next"},at = @At("RETURN"))
    public void onExit(int p_188581_, CallbackInfoReturnable<Integer> cir){
        this.internalLock.unlock();
    }


}
