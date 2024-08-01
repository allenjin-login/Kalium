package org.superredrock.Kalium.mixin;


import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public class MineCraftServerMixin {

//    @Inject(method = "stopServer",at = @At("HEAD"))
//    public void onServerStop(CallbackInfo ci){
//        PoolManager.stop();
//    }
}
