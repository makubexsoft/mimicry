package com.gc.mimicry.core.event;

import java.util.UUID;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.runtime.ApplicationManager;
import com.gc.mimicry.core.timing.Clock;
import com.google.common.base.Preconditions;

/**
 * A node represents a logical machine on which simulated applications can be run. An instance of a node only exists
 * within a certain simulation session.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Node extends BaseResourceManager
{
    private final UUID id;
    private final String name;
    private final ApplicationManager appMgr;
    private final EventStack eventStack;
    private final EventBridge eventBridge;
    private final Clock clock;

    Node(ClassLoadingContext context, String name, EventBroker eventBroker, Clock clock)
    {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(clock);

        this.name = name;
        this.clock = clock;

        eventBridge = new EventBridge();
        appMgr = new ApplicationManager(context, this);
        eventStack = new EventStack(this, eventBroker, eventBridge);
        id = UUID.randomUUID();

        attachResource(appMgr);
    }

    public UUID getId()
    {
        return id;
    }

    public Clock getClock()
    {
        return clock;
    }

    public EventBridge getEventBridge()
    {
        return eventBridge;
    }

    public EventStack getEventStack()
    {
        return eventStack;
    }

    public String getName()
    {
        return name;
    }

    public ApplicationManager getApplicationManager()
    {
        return appMgr;
    }
}