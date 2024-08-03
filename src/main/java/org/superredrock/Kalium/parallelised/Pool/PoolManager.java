package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class PoolManager implements Closeable {
    private final ConcurrentHashMap<Level,List<TickerPool>> ActivePool = new ConcurrentHashMap<>();
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
        return pool;
    }

    public <T extends TickerPool> T fork(Level level, @NotNull BiFunction<Integer,Level,T> builder){
        int allocateThread = this.allocate();
        if (allocateThread <= 0){
           allocateThread = 1;
        }
        return this.fork(allocateThread,level,builder);
    }



    public void register(TickerPool pool,Level dimension){
        this.ActivePool.putIfAbsent(dimension,new ArrayList<>());
        List<TickerPool> insertList = this.ActivePool.get(dimension);
        if (!insertList.contains(pool)){
            insertList.add(pool);
        }
    }



    @Override
    public void close() {
        this.ActivePool.forEachValue(Integer.MAX_VALUE,P -> P.isEmpty() ? null : P,
                l ->{
            for (TickerPool pool : l){
                if (!pool.isShutdown()){
                    pool.shutdownNow();
                }
            }
        }
        );
        this.ActivePool.clear();
        this.closed = true;

    }
    public static final PoolManager mainPool = new PoolManager(32);

}
