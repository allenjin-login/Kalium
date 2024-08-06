package org.superredrock.Kalium.mixin;


import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.superredrock.Kalium.NameUtils;
import org.superredrock.Kalium.parallelised.Pool.PoolManager;

@Mixin(MinecraftServer.class)
public class MineCraftServerMixin {
    @Unique
    private final PoolManager kalium$poolManager = PoolManager.init();

    @Inject(method = "close",at = @At("HEAD"))
    public void onClose(CallbackInfo ci){
        kalium$poolManager.close();
        NameUtils.releaseAll();
    }
}
