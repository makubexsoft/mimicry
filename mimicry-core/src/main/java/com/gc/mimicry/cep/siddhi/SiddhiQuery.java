package com.gc.mimicry.cep.siddhi;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.query.output.QueryCallback;

import com.gc.mimicry.cep.Query;
import com.gc.mimicry.cep.QueryListener;
import com.google.common.base.Preconditions;

public class SiddhiQuery implements Closeable, Query
{
    private final String query;
    private final SiddhiManager siddhi;
    private String queryRef;
    private QueryCallback callback;
    private final List<QueryListener> listener;

    public SiddhiQuery(SiddhiManager siddhi, String query)
    {
        Preconditions.checkNotNull(siddhi);
        Preconditions.checkNotNull(query);

        this.siddhi = siddhi;
        this.query = query;

        listener = new CopyOnWriteArrayList<QueryListener>();
        queryRef = siddhi.addQuery(query);
    }

    @Override
    public synchronized void addQueryListener(QueryListener l)
    {
        if (callback == null)
        {
            callback = new InnerCallback();
            siddhi.addCallback(queryRef, callback);
        }
        listener.add(l);
    }

    @Override
    public void removeQueryListener(QueryListener l)
    {
        listener.remove(l);
    }

    @Override
    public void close()
    {
        if (queryRef != null)
        {
            siddhi.removeQuery(queryRef);
            queryRef = null;
        }
    }

    @Override
    public String toString()
    {
        return query;
    }

    private class InnerCallback extends QueryCallback
    {
        @Override
        public void receive(long timestamp, org.wso2.siddhi.core.event.Event[] inEvents,
                org.wso2.siddhi.core.event.Event[] outEvents)
        {
            com.gc.mimicry.cep.Event[] wrappedInEvents = Util.wrap(inEvents);
            com.gc.mimicry.cep.Event[] wrappedOutEvents = Util.wrap(outEvents);
            for (QueryListener l : listener)
            {
                l.receive(timestamp, wrappedInEvents, wrappedOutEvents);
            }
        }
    }
}
