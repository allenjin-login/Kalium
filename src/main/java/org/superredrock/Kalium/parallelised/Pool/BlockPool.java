package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.superredrock.Kalium.Kalium;
import org.superredrock.Kalium.parallelised.BlockTicker;


import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BlockPool extends TickerPool {

    private final ConcurrentHashMap<BlockTicker, ScheduledFuture<?>> workQueue = new ConcurrentHashMap<>();
    private final ArrayList<TickingBlockEntity> freshBlocks = new ArrayList<>();
    private final ArrayList<TickingBlockEntity> pendingFreshBlocks = new ArrayList<>();
    private final ArrayList<TickingBlockEntity> pendingBlocks = new ArrayList<>();



    public BlockPool(int corePoolSize, Level ownedLevel) {
        super(corePoolSize,"Block Pool",ownedLevel);
    }


    @Override
    protected void terminated() {
        super.terminated();
        workQueue.forEachValue(16, v -> v.cancel(true));
        workQueue.clear();
    }


    public void release(){
        AtomicLong count = new AtomicLong(0);
        workQueue.forEachKey(64, k -> k.isClean() ? k : null,
                blockTicker -> {
                    workQueue.get(blockTicker).cancel(true);
                    workQueue.remove(blockTicker);
                    count.incrementAndGet();
                });
        count.get();
    }

    public void addTicker(TickingBlockEntity tickingBlock){
        if (this.ticking){
            this.pendingBlocks.add(tickingBlock);
        }else {
            register(tickingBlock);
        }
    }

    public void addFleshBlockTicker(Collection<TickingBlockEntity> fleshBlock){
        if (this.ticking) {
            this.pendingFreshBlocks.addAll(fleshBlock);
        } else {
            this.freshBlocks.addAll(fleshBlock);
        }
    }

    public void register(TickingBlockEntity tickingBlock){
        if (!tickingBlock.isRemoved()){
            BlockTicker ticker = new BlockTicker(this.OwnedLevel,tickingBlock);
            ScheduledFuture<?> tickTask = scheduleAtFixedRate(ticker,50 ,50, TimeUnit.MILLISECONDS);
            Kalium.LOGGER.info("new block register type:{}",tickingBlock.getType());
            workQueue.put(ticker,tickTask);
        }
    }


    @Override
    public void onTick() {
        if (!this.pendingFreshBlocks.isEmpty()) {
            this.freshBlocks.addAll(this.pendingFreshBlocks);
            this.pendingFreshBlocks.clear();
        }
        this.ticking = true;
        if (!this.freshBlocks.isEmpty()) {
            this.freshBlocks.forEach(tickingBlock -> {
                IForgeBlockEntity entity = (IForgeBlockEntity) tickingBlock;
                entity.onLoad();
            });
            this.freshBlocks.clear();
        }
        if (!this.pendingBlocks.isEmpty()){
            for (TickingBlockEntity block : this.pendingBlocks){
                this.register(block);
            }
            this.pendingBlocks.clear();
        }
        release();
        this.ticking = false;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r,"Block Ticker " + ThreadLocalRandom.current().nextInt());
    }
}
