package com.gc.mimicry.engine;

public class Applications
{

    public static Application create(ApplicationContext ctx, EntryPoint entryPoint) throws ClassNotFoundException,
            NoSuchMethodException, SecurityException
    {
        return new Application(ctx, entryPoint);
    }

    public static Application create(ApplicationContext ctx, String mainClass)
    {
        return null;
    }
}
