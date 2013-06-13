package com.gc.mimicry.core.messaging.p2p;

import com.gc.mimicry.core.messaging.Topic;

public class DefaultTopic implements Topic
{

	private String	name;

	DefaultTopic(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
