package com.gc.mimicry.core.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class NodeConfiguration implements Serializable
{
    private static final long serialVersionUID = 7308790723979464412L;
    private String nodeName;
    private List<EventHandlerConfiguration> eventStack;

    public NodeConfiguration(String name)
    {
        Preconditions.checkNotNull(name);
        this.nodeName = name;
        eventStack = new ArrayList<EventHandlerConfiguration>();
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    public List<EventHandlerConfiguration> getEventStack()
    {
        return eventStack;
    }

    @Override
    public String toString()
    {
        return "NodeConfiguration [nodeName=" + nodeName + ", eventStack=" + eventStack + "]";
    }
}
