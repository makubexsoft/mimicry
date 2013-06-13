package com.gc.mimicry.core.runtime;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

public class Application implements Closeable
{
	private final UUID				id;
	private final Node				node;
	private final ApplicationBridge	bridge;
	private final Future<?>			terminationFuture;
	private Clock					clock;

	Application(Node node, ApplicationBridge bridge)
	{
		Preconditions.checkNotNull( node );
		Preconditions.checkNotNull( bridge );

		this.node = node;
		this.bridge = bridge;
		id = UUID.randomUUID();
		clock = new RealtimeClock( 0 );

		bridge.setApplicationId( id );
		bridge.setClock( clock );
		bridge.setEventBridge( new EventBridge() );

		terminationFuture = bridge.getShutdownFuture();
	}

	public Future<?> getTerminationFuture()
	{
		return terminationFuture;
	}

	public Clock getClock()
	{
		return clock;
	}

	public void setClock( Clock clock )
	{
		Preconditions.checkNotNull( clock );
		this.clock = clock;
	}

	public Node getNode()
	{
		return node;
	}

	public void start()
	{
		bridge.startApplication();
	}

	public Future<?> stop()
	{
		bridge.shutdownApplication();
		return terminationFuture;
	}

	public boolean isActive()
	{
		return true;
	}

	public void setActive( boolean active )
	{

	}

	public UUID getId()
	{
		return id;
	}

	@Override
	public void close()
	{
		stop();
	}
}
