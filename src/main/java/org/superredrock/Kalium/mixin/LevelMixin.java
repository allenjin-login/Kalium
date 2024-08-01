package org.superredrock.Kalium.mixin;


import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.List;


@Mixin(Level.class)
public abstract class LevelMixin {


    @Shadow public abstract ProfilerFiller getProfiler();

    @Shadow @Final private List<TickingBlockEntity> pendingBlockEntityTickers;

    @Shadow private boolean tickingBlockEntities;

    @Shadow @Final private ArrayList<BlockEntity> freshBlockEntities;

    @Shadow @Final private ArrayList<BlockEntity> pendingFreshBlockEntities;

    @Shadow @Final protected List<TickingBlockEntity> blockEntityTickers;

    /**
     * @author superredrock
     * @reason Reduce tick spend
     */
    @Overwrite
    protected void tickBlockEntities() {
        ProfilerFiller profilerfiller = this.getProfiler();
        profilerfiller.push("blockEntities");
        if (!this.pendingFreshBlockEntities.isEmpty()) {
            this.freshBlockEntities.addAll(this.pendingFreshBlockEntities);
            this.pendingFreshBlockEntities.clear();
        }
        this.tickingBlockEntities = true;
        if (!this.freshBlockEntities.isEmpty()) {
            this.freshBlockEntities.forEach(IForgeBlockEntity::onLoad);
            this.freshBlockEntities.clear();
        }

        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }

        this.tickingBlockEntities = false;
        profilerfiller.pop();
    }
}
