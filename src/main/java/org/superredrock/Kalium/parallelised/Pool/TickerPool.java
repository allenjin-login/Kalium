package org.superredrock.Kalium.parallelised.Pool;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.superredrock.Kalium.Kalium;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;

public abstract class TickerPool extends ScheduledThreadPoolExecutor implements ThreadFactory {
    private final String name;
    protected final Level OwnedLevel;
    protected boolean ticking;

    public TickerPool(int corePoolSize, String name, Level ownedLevel) {
        super(corePoolSize);
        this.OwnedLevel = ownedLevel;
        this.name = name;
        this.setThreadFactory(this);
    }


    public String getName() {
        return name;
    }

    public void onTick(){}


    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread ticker = new Thread(r);
        ticker.setName("Ticker " + ThreadLocalRandom.current().nextInt());
        return ticker;
    }

    @Override
    protected void terminated() {
        super.terminated();
        Kalium.LOGGER.debug("Pool: {} closed on Level:{}",this.name,this.OwnedLevel);
    }
}
