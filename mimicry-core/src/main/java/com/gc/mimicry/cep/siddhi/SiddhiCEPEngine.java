package com.gc.mimicry.cep.siddhi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import com.gc.mimicry.cep.Attribute;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.StreamDescription;
import com.gc.mimicry.cep.Type;

public class SiddhiCEPEngine implements CEPEngine
{
    private final SiddhiManager siddhi;
    private final Map<String, SiddhiStream> streams;

    public SiddhiCEPEngine()
    {
        siddhi = new SLF4JBasedSiddhiManager();
        streams = new ConcurrentHashMap<String, SiddhiStream>();
    }

    public SiddhiCEPEngine(String sessionId)
    {
        siddhi = new SLF4JBasedSiddhiManager(sessionId);
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
    public SiddhiStream defineStream(StreamDescription streamDescription)
    {
        InputHandler handler = siddhi.defineStream(createInternalDescription(streamDescription));
        if (handler == null)
        {
            return null;
        }
        SiddhiStream stream = new SiddhiStream(handler, siddhi);
        streams.put(stream.getName(), stream);
        return stream;
    }

    private String createInternalDescription(StreamDescription streamDescription)
    {
        StringBuilder buffer = new StringBuilder("define stream ");
        buffer.append(streamDescription.getName());
        buffer.append(" (");
        List<Attribute> attributes = streamDescription.getAttributes();
        for (int i = 0; i < attributes.size(); ++i)
        {
            if (i > 0)
            {
                buffer.append(", ");
            }
            buffer.append(attributes.get(i).getName());
            buffer.append(" ");
            buffer.append(typeToString(attributes.get(i).getType()));
        }
        buffer.append(" )");
        return buffer.toString();
    }

    private String typeToString(Type type)
    {
        switch (type)
        {
            case BOOLEAN:
                return "bool";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case INT:
                return "int";
            case LONG:
                return "long";
            case STRING:
                return "string";
        }
        return null;
    }
}
