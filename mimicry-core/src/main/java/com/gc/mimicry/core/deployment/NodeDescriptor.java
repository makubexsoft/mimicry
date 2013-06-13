package com.gc.mimicry.core.deployment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeDescriptor implements Serializable
{

	private static final long	serialVersionUID	= 7308790723979464412L;
	private String				nodeName;
	private List<String>		eventStack;

	public NodeDescriptor()
	{
		this( null );
	}

	public NodeDescriptor(String name)
	{
		eventStack = new ArrayList<String>();
	}

	public String getNodeName()
	{
		return nodeName;
	}

	public void setNodeName( String nodeName )
	{
		this.nodeName = nodeName;
	}

	public List<String> getEventStack()
	{
		return eventStack;
	}
}
