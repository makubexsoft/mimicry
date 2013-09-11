package test.com.gc.mimicry.bridge.threading;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

import com.gc.mimicry.bridge.threading.CheckpointBasedScheduler;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.EventBroker;
import com.gc.mimicry.engine.SimpleEventBroker;
import com.gc.mimicry.engine.nodes.Node;
import com.gc.mimicry.engine.nodes.NodeConfiguration;
import com.gc.mimicry.engine.nodes.NodeManager;
import com.gc.mimicry.engine.timing.SystemClock;
import com.gc.mimicry.ext.stdio.ConsoleInputStream;

public class TestThreadScheduler
{
	/**
	 * Tests whether thread 1 is always executed before thread 2 and the number is incremented atomically 
	 * using the {@link CheckpointBasedScheduler}.
	 * 
	 * @throws Exception
	 */
    @Test
    public void testCheckpoints() throws Exception
    {
        ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
        EventBroker broker = new SimpleEventBroker();
        SystemClock clock = new SystemClock();
        NodeManager nodeMgr = new NodeManager(config, broker, clock);
        Node node = nodeMgr.createNode(new NodeConfiguration("myNode"));

        ClassLoader loader = ApplicationClassLoader.create(config, ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(clock);
        ctx.setEventBridge(node.getEventBridge());
        Thread.currentThread().setContextClassLoader(loader);
        Application app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                final Object monitor = "My named Monitor";
                final IntContainer container = new IntContainer();

                Runnable target = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < 300; ++i)
                        {
                            synchronized (monitor)
                            {
                                System.out.println(Thread.currentThread().getName() + ":" + container.value++);
                            }
                        }
                    }
                };
				Thread t1 = new Thread(target, "1");
                Thread t2 = new Thread(target, "2");

                t1.start();
                t2.start();
                
                try
				{
                	// TODO: currently the main thread is not reported as RUNNING
                	// to the thread scheduler which is why this doesn't deadlock.
                	// We need to define the scheduling behavior for join operations.
					t1.join();
					t2.join();
				}
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}

                System.err.println(container.value);
            }
        });

        ConsoleInputStream stderr = ConsoleInputStream.attachStderr(broker, app.getId());
        BufferedReader errReader = new BufferedReader(new InputStreamReader(stderr));
        
        ConsoleInputStream stdout = ConsoleInputStream.attachStdout(broker, app.getId());
        BufferedReader outReader = new BufferedReader(new InputStreamReader(stdout));

        // TODO: this invocation of start should also be reported to the scheduler.
        app.start(); 
        app.getTerminationFuture().await( 3000 );
        
        // Test thread 1 is always executed before thread 2 and the number is incremented atomically
        for(int i = 0; i < 600; i+=2)
        {
        	String line;
        	String[] split;
        	
        	line = outReader.readLine();
        	split = line.split( "\\:" );
        	assertEquals( "1", split[0] );
        	assertEquals( "" + i, split[1] );

        	line = outReader.readLine();
            split = line.split( "\\:" );
            assertEquals( "2", split[0] );
            assertEquals( "" + (i + 1), split[1] );
        }
        assertEquals("600", errReader.readLine());
    }
}

class IntContainer{
    public int value;
}
