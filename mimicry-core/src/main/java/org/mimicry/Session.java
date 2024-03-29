package org.mimicry;

import java.io.Closeable;

public interface Session extends Closeable
{
    public Node createNode(NodeParameters params);

    @Override
    public void close();
}
