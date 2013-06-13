package com.gc.mimicry.util;

import java.util.ArrayList;
import java.util.List;

public class Collections
{
	public static <T> List<T> merge( List<T> a, List<T> b )
	{
		ArrayList<T> list = new ArrayList<T>( a );
		list.addAll( b );
		return list;
	}
}
