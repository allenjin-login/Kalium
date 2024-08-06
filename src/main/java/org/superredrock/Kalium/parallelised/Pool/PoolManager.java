package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.superredrock.Kalium.Kalium;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class PoolManager implements Closeable {
    private static PoolManager defaultPool = new PoolManager(32);
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

    public <T extends TickerPool> T fork(int threads, Level level, @NotNull BiFunction<Integer,Level,T> builder){
        //TODO: wait for improve
        if (this.closed){
            throw new IllegalStateException("The Manager has been closed");
        }
        T pool = builder.apply(threads,level);
        this.register(pool,level);
        Kalium.LOGGER.debug("Allocate new pool[name: {}] with {} threads",pool.getName(),threads);
        return pool;
    }

    public <T extends TickerPool> T fork(Level level, @NotNull BiFunction<Integer,Level,T> builder){
        int allocateThread = this.allocate();
        if (allocateThread <= 0){
           allocateThread = 1;
        }
        return this.fork(allocateThread,level,builder);
    }
    private final ConcurrentHashMap<Level,List<TickerPool>> managedPools = new ConcurrentHashMap<>();

    public static PoolManager init(){
        if (defaultPool == null || defaultPool.closed){
            defaultPool = new PoolManager(32);
        }
        return defaultPool;
    }

    public void register(TickerPool pool,Level dimension){
        this.managedPools.putIfAbsent(dimension,new ArrayList<>());
        List<TickerPool> insertList = this.managedPools.get(dimension);
        if (!insertList.contains(pool)){
            insertList.add(pool);
        }
    }

    @Override
    public void close() {
        this.managedPools.forEachValue(Integer.MAX_VALUE, P -> P.isEmpty() ? null : P,
                l ->{
            for (TickerPool pool : l){
                if (!pool.isShutdown()){
                    pool.shutdownNow();
                }
            }
        }
        );
        this.managedPools.clear();
        this.closed = true;
    }

}
