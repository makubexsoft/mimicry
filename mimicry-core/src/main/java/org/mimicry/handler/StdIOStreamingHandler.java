package org.mimicry.handler;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.mimicry.cep.Event;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamListener;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventHandlerBase;
import org.mimicry.events.stdio.ConsoleStderrEvent;
import org.mimicry.events.stdio.ConsoleStdinEvent;
import org.mimicry.events.stdio.ConsoleStdoutEvent;
import org.mimicry.streams.StdErrStream;
import org.mimicry.streams.StdInStream;
import org.mimicry.streams.StdOutStream;

public class StdIOStreamingHandler extends EventHandlerBase implements StreamListener
{
    @Override
    protected void initHandler()
    {
        super.initHandler();
        StdInStream.get(getEventEngine()).addStreamListener(this);
    }

    @Override
    public void handleDownstream(ApplicationEvent evt)
    {
        if (evt instanceof ConsoleStdoutEvent)
        {
            ConsoleStdoutEvent out = (ConsoleStdoutEvent) evt;
            Stream stream = StdOutStream.get(getEventEngine());
            stream.send(getClock().currentMillis(), evt.getApplication().toString(), new String(out.getData()));
        }
        else if (evt instanceof ConsoleStderrEvent)
        {
            ConsoleStderrEvent err = (ConsoleStderrEvent) evt;
            Stream stream = StdErrStream.get(getEventEngine());
            stream.send(getClock().currentMillis(), evt.getApplication().toString(), new String(err.getData()));
        }
        else
        {
            super.handleDownstream(evt);
        }
    }

    @Override
    public void receive(Event[] events)
    {
        for (Event e : events)
        {
            UUID appId = UUID.fromString((String) e.getField(1));
            byte[] input = Base64.decodeBase64((String) e.getField(2));

            dispatchInputToApplication(appId, input);
        }
    }

    private void dispatchInputToApplication(UUID applicationId, byte[] input)
    {
        ConsoleStdinEvent event = getEventFactory().createEvent(ConsoleStdinEvent.class, applicationId);
        event.setData(input);
        sendUpstream(event);
    }
}
