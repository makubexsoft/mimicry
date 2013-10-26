package org.mimicry.handler;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.mimicry.cep.Event;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamListener;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.stack.EventHandlerBase;
import org.mimicry.engine.streams.StdErrStream;
import org.mimicry.engine.streams.StdInStream;
import org.mimicry.engine.streams.StdOutStream;
import org.mimicry.ext.stdio.events.ConsoleStderrEvent;
import org.mimicry.ext.stdio.events.ConsoleStdinEvent;
import org.mimicry.ext.stdio.events.ConsoleStdoutEvent;

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
