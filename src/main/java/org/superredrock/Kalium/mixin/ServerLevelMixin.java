package org.superredrock.Kalium.mixin;


import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.superredrock.Kalium.Kalium;
import org.superredrock.Kalium.parallelised.Pool.BlockPool;

import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    @Shadow public abstract void tick(BooleanSupplier p_8794_);

    @Unique
    private BlockPool blockPool = new BlockPool(4,this);;

    protected ServerLevelMixin(WritableLevelData p_220352_, ResourceKey<Level> p_220353_, Holder<DimensionType> p_220354_, Supplier<ProfilerFiller> p_220355_, boolean p_220356_, boolean p_220357_, long p_220358_, int p_220359_) {
        super(p_220352_, p_220353_, p_220354_, p_220355_, p_220356_, p_220357_, p_220358_, p_220359_);

        Kalium.LOGGER.info("Block Ticker prepared in Level:{}",this);
    }


    @Override
    public void addBlockEntityTicker(@NotNull TickingBlockEntity p_151526_) {
        super.addBlockEntityTicker(p_151526_);
        this.blockPool.addTicker(p_151526_);
    }


    @Override
    public void addFreshBlockEntities(@NotNull Collection<BlockEntity> beList) {
        super.addFreshBlockEntities(beList);
    }

    @Override
    protected void tickBlockEntities() {
        super.tickBlockEntities();
    }

    @Inject(method = "tick",at = @At("HEAD"))
    public void tick(BooleanSupplier p_8794_, CallbackInfo ci){
        this.blockPool.onTick();
    }


    @Inject(method = "close",at = @At("RETURN"))
    public void onClose(CallbackInfo ci){
        this.blockPool.shutdownNow();
        this.blockPool = null;
    }




}
