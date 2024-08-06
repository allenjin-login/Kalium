package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.superredrock.Kalium.Kalium;
import org.superredrock.Kalium.NameUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TickerPool extends ScheduledThreadPoolExecutor implements ThreadFactory {
    private final String name;
    protected final Level OwnedLevel;
    protected boolean ticking;
    protected final AtomicInteger activeTask = new AtomicInteger(0);

    public TickerPool(int corePoolSize, String name, Level ownedLevel) {
        super(corePoolSize);
        this.OwnedLevel = ownedLevel;
        this.name = name;
        this.setThreadFactory(this);
        this.setRemoveOnCancelPolicy(true);
    }

    public abstract ThreadGroup defaultGroup();

    public String getName() {return name;}

    public void tick(){}

    public int getActive(){
        return this.activeTask.get();
    }


    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread ticker = new Thread(this.defaultGroup(),r);
        ticker.setName("Ticker " + NameUtils.getId());
        return ticker;
    }

    @Override
    protected void terminated() {
        super.terminated();
        activeTask.set(0);
        Kalium.LOGGER.debug("Pool: {} closed on Level:{}",this.name,this.OwnedLevel);
    }
}
