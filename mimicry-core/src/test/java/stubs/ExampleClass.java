package stubs;

public class ExampleClass
{

    public void loopForever()
    {
        for (;;)
        {
            System.out.println("looping forever");
        }
    }

    public void loopTenTimes()
    {
        for (int k = 0; k < 10; k++)
        {
            System.out.println("loop " + k);
        }
    }
}
