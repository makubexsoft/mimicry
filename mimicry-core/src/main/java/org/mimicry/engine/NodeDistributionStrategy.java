package org.mimicry.engine;

import java.util.Set;

public interface NodeDistributionStrategy
{
    public Node createNode(Set<Session> sessions, NodeParameters params);
}
