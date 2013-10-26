package org.mimicry.ui.groovy;

import groovy.lang.Binding;

import java.util.HashSet;
import java.util.Set;

public class BuiltInBinding extends Binding
{
	private Set<String>	finalVariables;

	public BuiltInBinding()
	{
		finalVariables = new HashSet<String>();
	}

	public void defineBuiltInVariable( String name, Object value )
	{
		setVariable( name, value );
		finalVariables.add( name );
	}

	@Override
	public void setVariable( String name, Object value )
	{
		if ( finalVariables.contains( name ) )
		{
			throw new RuntimeException( "Can't assign to built-in variable '" + name + "'." );
		}
		super.setVariable( name, value );
	}
}