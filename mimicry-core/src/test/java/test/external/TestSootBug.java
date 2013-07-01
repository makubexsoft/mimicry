package test.external;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import soot.G;
import soot.Scene;

public class TestSootBug
{
    @Before
    public void setUp()
    {
        G.reset();
    }

    @Test(expected = RuntimeException.class)
    public void testClassDoesNotExist()
    {
        String className = "DoesNotExist";

        Scene.v().loadClassAndSupport(className);
    }

    @Test(expected = RuntimeException.class)
    public void testBug()
    {
        String className = "DoesNotExist";

        try
        {
            Scene.v().loadClassAndSupport(className);
            fail();
        }
        catch (RuntimeException e)
        {
        }

        // should also throw RuntimeException
        Scene.v().loadClassAndSupport(className);
    }
}
