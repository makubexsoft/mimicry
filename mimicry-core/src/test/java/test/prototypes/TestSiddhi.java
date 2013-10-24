package test.prototypes;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

public class TestSiddhi
{

    public static void main(String[] args) throws InterruptedException
    {
        SiddhiConfiguration configuration = new SiddhiConfiguration();
        SiddhiManager siddhiManager = new SiddhiManager(configuration);

        InputHandler handler = siddhiManager.defineStream("define stream Time ( ts long )");
        siddhiManager.defineStream("define stream cseEventStream ( symbol string, price float, volume int )");
        siddhiManager.defineStream("define stream twiterStream ( symbol string, count int )");

        siddhiManager.addQuery("from Time#window.externalTime(ts, 5 sec) insert into Foo");

        String sql = "from e1 = cseEventStream [ price >= 50 and volume > 100 ] , e2 = twiterStream [count > 10 ] "
                + "insert into StockQuote e1.price as price, e1.symbol as symbol, e2.count as count ;";

        String queryReference = siddhiManager.addQuery(sql);
        System.out.println(queryReference);
        siddhiManager.addCallback(queryReference, new QueryCallback()
        {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents)
            {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
            }
        });

        queryReference = siddhiManager.addQuery(sql);
        System.out.println(queryReference);
        siddhiManager.addCallback("StockQuote", new StreamCallback()
        {

            @Override
            public void receive(Event[] arg0)
            {
                EventPrinter.print(arg0);
            }
        });

        InputHandler cseStreamHandler = siddhiManager.getInputHandler("cseEventStream");
        InputHandler twitterStreamHandler = siddhiManager.getInputHandler("twiterStream");

        System.out.println("timing...");
        for (int i = 0; i < 10; i++)
        {
            handler.send(new Object[] { System.currentTimeMillis() });
            Thread.sleep(1000);
        }

        cseStreamHandler.send(new Object[] { "IBM", 75.6f, 105 });
        cseStreamHandler.send(new Object[] { "GOOG", 51f, 101 });
        cseStreamHandler.send(new Object[] { "IBM", 76.6f, 111 });
        Thread.sleep(500);
        twitterStreamHandler.send(new Object[] { "IBM", 20 });
        Thread.sleep(500);
        cseStreamHandler.send(new Object[] { "WSO2", 45.6f, 100 });
        Thread.sleep(500);
        twitterStreamHandler.send(new Object[] { "GOOG", 20 });
        Thread.sleep(500);
    }
}
