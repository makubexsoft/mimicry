package com.gc.mimicry.cluster;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.LifeCycleAwareChannelHandler;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class TimingChannelHandler extends SimpleChannelHandler implements LifeCycleAwareChannelHandler
{

    private final ChannelGroup allChannels;
    private final int periodInMillis;
    private String timerName;

    public TimingChannelHandler(ScheduledExecutorService scheduler, int periodInMillis, String timerName)
    {
        allChannels = new DefaultChannelGroup();
        this.periodInMillis = periodInMillis;
        this.timerName = timerName;
        scheduler.scheduleAtFixedRate(new TimeJob(), 0, periodInMillis, TimeUnit.MILLISECONDS);
    }

    public void beforeAdd(ChannelHandlerContext ctx) throws Exception
    {
    }

    public void afterAdd(ChannelHandlerContext ctx) throws Exception
    {
        allChannels.add(ctx.getChannel());
    }

    public void beforeRemove(ChannelHandlerContext ctx) throws Exception
    {
    }

    public void afterRemove(ChannelHandlerContext ctx) throws Exception
    {
    }

    private class TimeJob implements Runnable
    {

        public void run()
        {
            for (Channel ch : allChannels)
            {
                ch.getPipeline().sendDownstream(new TimingChannelEvent(ch, periodInMillis, timerName));
            }
        }
    }
}