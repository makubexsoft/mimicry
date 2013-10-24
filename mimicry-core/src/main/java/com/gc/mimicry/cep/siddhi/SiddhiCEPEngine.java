package com.gc.mimicry.cep.siddhi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.stream.input.InputHandler;

import com.gc.mimicry.cep.CEPEngine;

public class SiddhiCEPEngine implements CEPEngine
{
    private final SiddhiManager siddhi;
    private final Map<String, SiddhiStream> streams;

    public SiddhiCEPEngine()
    {
        siddhi = new SiddhiManager();
        streams = new ConcurrentHashMap<String, SiddhiStream>();
    }

    public SiddhiCEPEngine(String sessionId)
    {
        SiddhiConfiguration cfg = new SiddhiConfiguration();
        cfg.setDistributedProcessing(true);
        cfg.setInstanceIdentifier(sessionId);

        siddhi = new SiddhiManager(cfg);
        streams = new ConcurrentHashMap<String, SiddhiStream>();
    }

    @Override
    public SiddhiQuery addQuery(String query)
    {
        return new SiddhiQuery(siddhi, query);
    }

    @Override
    public SiddhiStream getStream(String name)
    {
        SiddhiStream stream = streams.get(name);
        if (stream != null)
        {
            return stream;
        }

        InputHandler handler = siddhi.getInputHandler(name);
        if (handler == null)
        {
            return null;
        }
        stream = new SiddhiStream(handler, siddhi);
        streams.put(name, stream);
        return stream;
    }

    @Override
    public SiddhiStream defineStream(String streamDescription)
    {
        InputHandler handler = siddhi.defineStream(streamDescription);
        if (handler == null)
        {
            return null;
        }
        SiddhiStream stream = new SiddhiStream(handler, siddhi);
        streams.put(stream.getName(), stream);
        return stream;
    }
}
