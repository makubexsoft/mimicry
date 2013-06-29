package com.gc.mimicry.core.event;

import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;
import com.google.common.base.Preconditions;

public class EventHandlerBase implements EventHandler {

	@Override
	public void handleDownstream(EventHandlerContext ctx, Event evt) {
		ctx.sendDownstream(evt);
	}

	@Override
	public void handleUpstream(EventHandlerContext ctx, Event evt) {
		ctx.sendUpstream(evt);
	}

	@Override
	public void init(Scheduler scheduler, Clock clock) {
		Preconditions.checkNotNull(scheduler);
		this.scheduler = scheduler;
	}

	@Override
	public Scheduler getScheduler() {
		return scheduler;
	}

	private Scheduler scheduler;

}
