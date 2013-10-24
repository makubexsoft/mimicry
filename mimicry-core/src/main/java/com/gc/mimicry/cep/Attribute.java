package com.gc.mimicry.cep;

public class Attribute
{
    private final String name;
    private final Type type;

    public Attribute(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }
}