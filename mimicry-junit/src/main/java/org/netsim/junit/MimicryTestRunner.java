package org.netsim.junit;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import com.gc.mimicry.util.ClassPathUtil;

public class MimicryTestRunner extends BlockJUnit4ClassRunner
{
	private Class<?>	testClass;

	public MimicryTestRunner(Class<?> clazz) throws InitializationError
	{
		super( Object.class );
		testClass = clazz;
	}

	@Override
	protected Object createTest() throws Exception
	{
		URLClassLoader loader = new URLClassLoader( ClassPathUtil.getSystemClassPath().toArray( new URL[0] ), null );
		try
		{
			return new TestClass( loader.loadClass( testClass.getName() ) ).getOnlyConstructor().newInstance();
		}
		finally
		{
			loader.close();
		}
	}
}