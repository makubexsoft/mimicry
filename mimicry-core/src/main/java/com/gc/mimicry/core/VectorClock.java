package com.gc.mimicry.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VectorClock<T extends Serializable> implements Serializable, Cloneable
{
	private static final long	serialVersionUID	= -9090773677196317434L;
	private Map<T, Long>		vector;

	public VectorClock()
	{
		vector = new HashMap<T, Long>();
	}

	public VectorClock(VectorClock<T> v)
	{
		vector = new HashMap<T, Long>( v.vector );
	}

	@Override
	public VectorClock<T> clone()
	{
		return new VectorClock<T>( this );
	}

	public void tick( T processId )
	{
		if ( vector.containsKey( processId ) )
		{
			vector.put( processId, get( processId ) + 1 );
		}
		else
		{
			vector.put( processId, 1l );
		}
	}

	public Long get( T processId )
	{
		Long lResult = vector.get( processId );

		if ( lResult == null )
			lResult = 0l;

		return lResult;
	}

	public boolean happenedBefore( VectorClock<T> v2 )
	{
		return getRelation( this, v2 ) == Happened.BEFORE;
	}

	public boolean happenedAfter( VectorClock<T> v2 )
	{
		return getRelation( this, v2 ) == Happened.AFTER;
	}

	public boolean happenedConcurrentlyTo( VectorClock<T> v2 )
	{
		return getRelation( this, v2 ) == Happened.CONCURRENTLY;
	}

	public static <S extends Serializable> VectorClock<S> merge( VectorClock<S> v1, VectorClock<S> v2 )
	{
		VectorClock<S> result = new VectorClock<S>( v1 );

		for ( S processId : v2.vector.keySet() )
		{
			if ( !result.vector.containsKey( processId ) || result.get( processId ) < v2.get( processId ) )
			{
				result.vector.put( processId, v2.get( processId ) );
			}
		}

		return result;
	}

	public static <S extends Serializable> Happened getRelation( VectorClock<S> v1, VectorClock<S> v2 )
	{
		boolean isEqual = true;
		boolean isGreater = true;
		boolean isSmaller = true;

		for ( S processId : v1.vector.keySet() )
		{
			if ( v2.vector.containsKey( processId ) )
			{
				if ( v1.get( processId ) < v2.get( processId ) )
				{
					isEqual = false;
					isGreater = false;
				}
				if ( v1.get( processId ) > v2.get( processId ) )
				{
					isEqual = false;
					isSmaller = false;
				}
			}
			else if ( v1.get( processId ) != 0 )
			{
				isEqual = false;
				isSmaller = false;
			}
		}

		for ( S processId : v2.vector.keySet() )
		{
			if ( !v1.vector.containsKey( processId ) && (v2.get( processId ) != 0) )
			{
				isEqual = false;
				isGreater = false;
			}
		}

		if ( isEqual )
		{
			return Happened.BEFORE;
		}
		else if ( isGreater && !isSmaller )
		{
			return Happened.AFTER;
		}
		else if ( isSmaller && !isGreater )
		{
			return Happened.BEFORE;
		}
		else
		{
			return Happened.CONCURRENTLY;
		}
	}

	public static enum Happened
	{
		BEFORE, AFTER, CONCURRENTLY
	}
}