package stubs;

import java.net.Socket;

public class SimpleSocketApp
{
    public static void main(String argv[]) throws Exception
    {
        Socket s = null;
        try
        {
            s = new Socket(argv[0], Integer.parseInt(argv[1]));
        }
        finally
        {
            if (s != null)
            {
                s.close();
            }
        }
    }
}
