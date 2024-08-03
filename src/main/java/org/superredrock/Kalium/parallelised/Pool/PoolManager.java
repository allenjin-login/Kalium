package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import org.superredrock.Kalium.Kalium;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.BiFunction;

public class PoolManager implements Closeable {
    private final ConcurrentHashMap<TickerPool, Level> ActivePool = new ConcurrentHashMap<>();
    private final int maxThread;
    private int threadCount;
    private boolean closed = false;

    public PoolManager(int maxThread) {
        this.maxThread = maxThread;
        this.threadCount = maxThread;
    }

    public int allocate(){
        if ((this.threadCount > this.maxThread) || this.threadCount < 0){
            throw new IllegalStateException(" Wrong Allocate");
        }
        if (this.threadCount > 4){
            this.threadCount -= 4;
            return 4;
        }else {
            int count = this.threadCount;
            this.threadCount = 0;
            return count;
        }
    }

    public BlockPool createBlockPool(Level level){
        if (this.closed){
            throw new IllegalStateException("The Manager has been closed");
        }
        int allocateThread = this.allocate();
        if (allocateThread <= 0){
            allocateThread = 1;
        }
        BlockPool newBlockPool = new BlockPool(allocateThread,level);
        this.register(newBlockPool,level);
        Kalium.LOGGER.debug("New Block Pool [Threads:{}] has been created" ,allocateThread);
        return newBlockPool;
    }

    public <T extends TickerPool> T fork(int threads, Level level, BiFunction<Integer,Level,T> builder){
        //TODO: wait for improve
        T pool = builder.apply(threads,level);
        this.register(pool,level);
        return pool;
    }

    public void register(TickerPool pool,Level dimension){
        ActivePool.put(pool,dimension);
    }



    @Override
    public void close() {
        ActivePool.forEachKey(16,p ->p.isShutdown() ? null : p, ScheduledThreadPoolExecutor::shutdownNow);
        ActivePool.clear();
        this.closed = true;

    }

    public static final PoolManager mainPool = new PoolManager(32);

}
