package com.gc.mimicry.core.event;

import com.gc.mimicry.core.messaging.Message;

public class SendMessageEvent implements Event
{

	private Message	msg;
	private String	destination;

	public SendMessageEvent(Message msg, String destination)
	{
		this.msg = msg;
		this.destination = destination;
	}

	public boolean isResponseTo( Event request )
	{
		return false;
	}

	public Message getMsg()
	{
		return msg;
	}

	public String getDestination()
	{
		return destination;
	}
}
