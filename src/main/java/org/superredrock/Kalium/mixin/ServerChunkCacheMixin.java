package org.superredrock.Kalium.mixin;


import net.minecraft.world.level.chunk.ChunkSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.server.level.ServerChunkCache.class)
public abstract class ServerChunkCacheMixin extends ChunkSource {
    //TODO: new sync feature
}
