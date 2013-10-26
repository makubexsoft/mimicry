package org.mimicry.cep;

public interface CEPEngine
{
    public Query addQuery(String query);

    public Stream getStream(String name);

    public Stream defineStream(StreamDescription streamDescription);
}