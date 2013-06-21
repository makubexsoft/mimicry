package com.gc.mimicry.bridge.aspects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.gc.mimicry.util.ReflectionUtils;

/**
 * Utility methods for writing simpler advices.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class AspectUtils
{
	public static <T> T invokeReflectiveConstructor( Class<T> clazz, Object[] varargs )
	{
		Class<?>[] types = ReflectionUtils.getParameterTypes( varargs );
		Object[] values = ReflectionUtils.getParameterValues( varargs );

		Constructor<T> ctor = ReflectionUtils.getPossibleConstructor( clazz, types );
		try
		{
			return ctor.newInstance( values );
		}
		catch ( InstantiationException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( IllegalAccessException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( IllegalArgumentException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( InvocationTargetException e )
		{
			throw new RuntimeException( "", e );
		}
	}

	public static <T> T invokeConstructor( Class<T> clazz, Object[] values )
	{
		Class<?>[] types = ReflectionUtils.getValueTypes( values );
		Constructor<T> ctor = ReflectionUtils.getPossibleConstructor( clazz, types );
		try
		{
			return ctor.newInstance( values );
		}
		catch ( InstantiationException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( IllegalAccessException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( IllegalArgumentException e )
		{
			throw new RuntimeException( "", e );
		}
		catch ( InvocationTargetException e )
		{
			throw new RuntimeException( "", e );
		}
	}
}
