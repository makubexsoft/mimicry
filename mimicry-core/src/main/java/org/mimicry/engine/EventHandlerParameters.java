package org.mimicry.engine;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class EventHandlerParameters
{
    private String className;
    private Map<String, String> configuration;

    public String getClassName()
    {
        return className;
    }

    public EventHandlerParameters(String className)
    {
        configuration = new HashMap<String, String>();
        this.className = className;
    }

    public void setConfiguration(Map<String, String> configuration)
    {
        Preconditions.checkNotNull(configuration);
        this.configuration = configuration;
    }

    public Map<String, String> getConfiguration()
    {
        return configuration;
    }

    @Override
    public String toString()
    {
        return "EventHandlerConfiguration [className=" + className + ", configuration=" + configuration + "]";
    }
}
