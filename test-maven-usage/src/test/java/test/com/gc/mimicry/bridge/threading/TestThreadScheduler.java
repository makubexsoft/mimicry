package test.com.gc.mimicry.bridge.threading;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.netsim.junit.MimicryTestRunner;

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

//@RunWith(MimicryTestRunner.class)
public class TestThreadScheduler
{
	private  ApplicationContext ctx;
	private  EventBroker broker;
	private  BufferedReader errReader;
	private  BufferedReader outReader;
	
	@Before
	public void setUp() throws Exception
	{
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
        broker = new SimpleEventBroker();
        SystemClock clock = new SystemClock();
        NodeManager nodeMgr = new NodeManager(config, broker, clock);
        Node node = nodeMgr.createNode(new NodeConfiguration("myNode"));

        ClassLoader loader = ApplicationClassLoader.create(config, TestThreadScheduler.class.getClassLoader());
         ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(clock);
        ctx.setEventBridge(node.getEventBridge());
        Thread.currentThread().setContextClassLoader(loader);
	}
	
	/**
	 * Tests whether thread 1 is always executed before thread 2 and the number is incremented atomically 
	 * using the {@link CheckpointBasedScheduler}.
	 * 
	 * @throws Exception
	 */
    @Test
    public void testCheckpointsWithSyncBlocks() throws Exception
    {
    	runTest(new TestRunnable()
        {
    		private int counter;
    		
            @Override
            public void run()
            {
                for (int i = 0; i < 300; ++i)
                {
                    synchronized (this)
                    {
                        System.out.println(Thread.currentThread().getName() + ":" + counter++);
                    }
                }
            }

			@Override
			public int getCounter() {
				return counter;
			}
        });
    }
    
    /**
     * Tests whether thread 1 is always executed before thread 2 and the number is incremented atomically 
     * using the {@link CheckpointBasedScheduler}.
     * 
     * @throws Exception
     */
    @Test
    public void testCheckpointsWithSyncMethods() throws Exception
    {
        runTest(new TestRunnable()
                {
        			private int counter;
        		
                	 @Override
                     public void run()
                     {
                         for (int i = 0; i < 300; ++i)
                         {
                            count();
                         }
                     }    
                	 
                     private synchronized void count()
                     {
                         System.out.println(Thread.currentThread().getName() + ":" + counter++);
                     }

					@Override
					public int getCounter() {
						return counter;
					}
                });
    }
    
    @Test
    public void testCheckpointsWithStaticSyncMethods() throws Exception
    {
    	runTest(new StaticMethodRunnable());
    }
    
    private void runTest(final TestRunnable target) throws Exception
    {
    	Application app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
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

                System.err.println(target.getCounter());
            }
        });

        createStreams(app.getId());
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
    
    private void createStreams(UUID appId)
	{
		ConsoleInputStream stderr = ConsoleInputStream.attachStderr(broker, appId);
        errReader = new BufferedReader(new InputStreamReader(stderr));
        
        ConsoleInputStream stdout = ConsoleInputStream.attachStdout(broker, appId);
        outReader = new BufferedReader(new InputStreamReader(stdout));
	}
}

interface TestRunnable extends Runnable
{
	public int getCounter();
}

class StaticMethodRunnable implements TestRunnable
{
	public static int value;
	
    @Override
    public void run()
    {
        for (int i = 0; i < 300; ++i)
        {
           count();
        }
    }
    
    private static synchronized void count()
    {
    	System.out.println(Thread.currentThread().getName() + ":" + value++);
    }

	@Override
	public int getCounter() {
		// TODO Auto-generated method stub
		return StaticMethodRunnable.value;
	}
}

class IntContainer{
    public int value;
}
