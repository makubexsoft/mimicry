package com.gc.mimicry.cep;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class StreamDescription
{
    private final String name;
    private final List<Attribute> attributes;

    public StreamDescription(String name)
    {
        Preconditions.checkNotNull(name);
        this.name = name;

        attributes = new ArrayList<Attribute>();
    }

    public void addField(String name, Type type)
    {
        attributes.add(new Attribute(name, type));
    }

    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public String getName()
    {
        return name;
    }
}
