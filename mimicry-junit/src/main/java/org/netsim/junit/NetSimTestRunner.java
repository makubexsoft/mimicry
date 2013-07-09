package org.netsim.junit;

import java.net.URLClassLoader;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class NetSimTestRunner extends BlockJUnit4ClassRunner {

	public NetSimTestRunner(Class<?> clazz) throws InitializationError {
		super( getFromTestClassloader( clazz ) );
	}
	
	@Override
	public void run( RunNotifier notifier )
	{
		// TODO Auto-generated method stub
		super.run( notifier );
	}

	private static Class<?> getFromTestClassloader( Class<?> clazz ) throws InitializationError {
		try {
			ClassLoader testClassLoader = new URLClassLoader(null);
			return Class.forName( clazz.getName(), true, testClassLoader );
		} catch ( ClassNotFoundException e ) {
			throw new InitializationError( e );
		}
	}
}