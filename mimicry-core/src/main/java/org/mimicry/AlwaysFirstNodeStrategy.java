package org.mimicry;

import java.util.Set;

public class AlwaysFirstNodeStrategy implements NodeDistributionStrategy
{
    @Override
    public Node createNode(Set<Session> sessions, NodeParameters params)
    {
        return sessions.iterator().next().createNode(params);
    }
}
