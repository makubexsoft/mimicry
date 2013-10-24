package com.gc.mimicry.cep;


public interface CEPEngine
{

    public Query addQuery(String query);

    public Stream getStream(String name);

    public Stream defineStream(String streamDescription);

}