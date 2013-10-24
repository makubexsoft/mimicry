package com.gc.mimicry.cep.siddhi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.cep.StreamListener;
import com.google.common.base.Preconditions;

public class SiddhiStream implements Stream
{
    private final InputHandler handler;
    private final List<StreamListener> listener;

    public SiddhiStream(InputHandler handler, SiddhiManager siddhi)
    {
        Preconditions.checkNotNull(handler);
        this.handler = handler;
        listener = new CopyOnWriteArrayList<StreamListener>();
        siddhi.addCallback(getName(), new InnerCallback());
    }

    @Override
    public void send(Object... values)
    {
        try
        {
            handler.send(values);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getName()
    {
        return handler.getStreamId();
    }

    @Override
    public void addStreamListener(StreamListener l)
    {
        listener.add(l);
    }

    @Override
    public void removeStreamListener(StreamListener l)
    {
        listener.remove(l);
    }

    @Override
    public String toString()
    {
        return "Stream[name='" + getName() + "']";
    }

    private class InnerCallback extends StreamCallback
    {
        @Override
        public void receive(Event[] events)
        {
            com.gc.mimicry.cep.Event[] wrappedEvents = Util.wrap(events);
            for (StreamListener l : listener)
            {
                l.receive(wrappedEvents);
            }
        }
    }
}
