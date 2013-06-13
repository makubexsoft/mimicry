package com.gc.mimicry.core.messaging.p2p;

import com.gc.mimicry.core.messaging.Message;

public class MessageEnvelope extends Message
{

	private String	topicName;
	private Message	content;

	public MessageEnvelope(Message content, String topicName)
	{
		super( content.getType() );
		this.content = content;
		this.topicName = topicName;
	}

	public Message getContent()
	{
		return content;
	}

	public String getTopicName()
	{
		return topicName;
	}
}
