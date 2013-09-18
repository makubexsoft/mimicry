package com.gc.mimicry.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gc.mimicry.engine.stack.EventHandlerParameters;
import com.google.common.base.Preconditions;

public class NodeParameters implements Serializable
{
    private static final long serialVersionUID = 7308790723979464412L;
    private String nodeName;
    private final List<EventHandlerParameters> eventStack;

    public NodeParameters(String name)
    {
        Preconditions.checkNotNull(name);
        this.nodeName = name;
        eventStack = new ArrayList<EventHandlerParameters>();
    }

    public NodeParameters(String name, EventHandlerParameters[] stack)
    {
        Preconditions.checkNotNull(name);
        this.nodeName = name;
        eventStack = new ArrayList<EventHandlerParameters>();
        eventStack.addAll(Arrays.asList(stack));
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public List<EventHandlerParameters> getEventStack()
    {
        return eventStack;
    }

    @Override
    public String toString()
    {
        return "NodeConfiguration [nodeName=" + nodeName + ", eventStack=" + eventStack + "]";
    }
}
