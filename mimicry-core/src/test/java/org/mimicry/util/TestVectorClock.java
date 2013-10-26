package org.mimicry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mimicry.util.VectorClock;


public class TestVectorClock
{

	private VectorClock<String>	v1;
	private VectorClock<String>	v2;

	@Before
	public void setUp()
	{
		v1 = new VectorClock<String>();
		v2 = new VectorClock<String>();
	}

	@Test
	public void testSameHappenedBefore()
	{
		assertTrue( v1.happenedBefore( v2 ) );
		assertFalse( v1.happenedAfter( v2 ) );
		assertFalse( v1.happenedConcurrentlyTo( v2 ) );
	}

	@Test
	public void testAfter()
	{
		v1.tick( "a" );
		assertTrue( v1.happenedAfter( v2 ) );
	}

	@Test
	public void testBefore()
	{
		v2.tick( "a" );
		assertTrue( v1.happenedBefore( v2 ) );
	}

	@Test
	public void testConcurrently()
	{
		v1.tick( "a" );
		v2.tick( "b" );
		assertTrue( v1.happenedConcurrentlyTo( v2 ) );
		assertTrue( v2.happenedConcurrentlyTo( v1 ) );
	}

	@Test
	public void testMerge()
	{
		v1.tick( "a" );
		v2.tick( "b" );
		VectorClock<String> v3 = VectorClock.merge( v1, v2 );
		assertTrue( v3.happenedAfter( v1 ) );
		assertTrue( v3.happenedAfter( v2 ) );
	}

	@Test
	public void testClone()
	{
		v1.tick( "a" );
		v2 = v1.clone();
		v1.tick( "a" );
		assertTrue( v1.happenedAfter( v2 ) );
	}

	@Test
	public void testGet()
	{
		v1.tick( "a" );
		assertEquals( 1, v1.get( "a" ).longValue() );
		assertEquals( 0, v1.get( "b" ).longValue() );
	}
}
