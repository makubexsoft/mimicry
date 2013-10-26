package org.mimicry;

import java.util.Set;

public interface NodeDistributionStrategy
{
    public Node createNode(Set<Session> sessions, NodeParameters params);
}
