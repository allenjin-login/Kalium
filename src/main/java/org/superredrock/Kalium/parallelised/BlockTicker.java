package org.superredrock.Kalium.parallelised;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;

import java.util.Objects;

public class BlockTicker implements Runnable{

    private final Level PoolLevel;
    private final TickingBlockEntity targetBlock;
    private boolean clean = false;

    public BlockTicker(Level poolLevel, TickingBlockEntity targetBlock) {
        this.PoolLevel = poolLevel;
        this.targetBlock = targetBlock;
    }

    @Override
    public void run() {
        if (!clean){
            if (targetBlock.isRemoved()){
                this.clean = true;
            }else if (PoolLevel.shouldTickBlocksAt(targetBlock.getPos())){
                this.targetBlock.tick();
            }
        }
    }

    public TickingBlockEntity getTargetBlock() {
        return targetBlock;
    }

    public boolean isClean() {
        return clean;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.targetBlock);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockTicker o1){
            return (o1.targetBlock == this.targetBlock);
        }else {
            return false;
        }
    }
}
