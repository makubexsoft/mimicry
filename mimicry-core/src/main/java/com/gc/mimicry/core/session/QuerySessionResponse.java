package com.gc.mimicry.core.session;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;

public class QuerySessionResponse extends Message
{
	private static final long	serialVersionUID	= 7032570840446801916L;
	private final UUID			requestId;
	private final SessionInfo	sessionInfo;

	public QuerySessionResponse(UUID requestId, SessionInfo sessionInfo)
	{
		this.requestId = requestId;
		this.sessionInfo = sessionInfo;
	}

	public UUID getRequestId()
	{
		return requestId;
	}

	public SessionInfo getSessionInfo()
	{
		return sessionInfo;
	}
}
