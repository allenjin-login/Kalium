package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.superredrock.Kalium.Kalium;
import org.superredrock.Kalium.parallelised.BlockTicker;


import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BlockPool extends TickerPool {

    private final ConcurrentHashMap<BlockTicker, ScheduledFuture<?>> workQueue = new ConcurrentHashMap<>();
    private final ArrayList<TickingBlockEntity> pendingFreshBlocks = new ArrayList<>();

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

    public void register(TickingBlockEntity tickingBlock){
        if (!tickingBlock.isRemoved()){
            BlockTicker ticker = new BlockTicker(this.OwnedLevel,tickingBlock);
            ScheduledFuture<?> tickTask = scheduleAtFixedRate(ticker,50 ,50, TimeUnit.MILLISECONDS);
            Kalium.LOGGER.info("new block register type:{}",tickingBlock.getType());
            workQueue.put(ticker,tickTask);
        }
    }

    public void registerFleshBlock(Collection<TickingBlockEntity> fleshBlock){
        if (this.ticking) {
            this.pendingFreshBlocks.addAll(fleshBlock);
        } else {
            for (TickingBlockEntity tickingBlockEntity : fleshBlock){
                register(tickingBlockEntity);
            }
        }
    }


    @Override
    public void onTick() {

        this.ticking = true;
        release();
        this.ticking = false;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r,"Block Ticker "+ ThreadLocalRandom.current().nextInt());
    }
}
