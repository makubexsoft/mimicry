package com.gc.mimicry.cep;

public interface Query
{

    public abstract void addQueryListener(QueryListener l);

    public abstract void removeQueryListener(QueryListener l);

    public abstract void close();

}