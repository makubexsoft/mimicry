package com.gc.mimicry.bridge.aspects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.ext.stdio.events.ConsoleStderrEvent;
import com.gc.mimicry.ext.stdio.events.ConsoleStdoutEvent;

public class EventGeneratingPrintStream extends PrintStream
{
	private Stream stream;
	
	public EventGeneratingPrintStream(Stream stream)
    {
        super(new ByteArrayOutputStream());
        this.stream = stream;
    }
	
	private void send(String text)
	{
		send( text.getBytes() );
	}
	
	private void send(byte[] data)
	{
		if(stream == Stream.STDOUT)
		{
			ConsoleStdoutEvent event = createEvent( ConsoleStdoutEvent.class );
			event.setData( data );
			SimulatorBridge.getEventBridge().dispatchEventToStack( event );
		}
		else
		{
			ConsoleStderrEvent event = createEvent( ConsoleStderrEvent.class );
			event.setData( data );
			SimulatorBridge.getEventBridge().dispatchEventToStack( event );
		}
	}
	 
	private <T extends Event> T createEvent(Class<T> eventClass)
	{
		EventFactory eventFactory = ManagedThread.currentThread().getEventFactory();
		return eventFactory.createEvent(eventClass, SimulatorBridge.getApplicationId(), null);
	}

    @Override
    public PrintStream append(char c)
    {
    	send("" + c);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq)
    {
    	send("" + csq);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end)
    {
    	send("" + csq.subSequence(start, end));
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args)
    {
    	send(String.format(l, format, args));
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args)
    {
    	send(String.format(format, args));
        return this;
    }

    @Override
    public void print(boolean b)
    {
    	send("" + b);
    }

    @Override
    public void print(char c)
    {
    	send("" + c);
    }

    @Override
    public void print(char[] s)
    {
    	send(new String(s));
    }

    @Override
    public void print(double d)
    {
    	send("" + d);
    }

    @Override
    public void print(float f)
    {
    	send("" + f);
    }

    @Override
    public void print(int i)
    {
    	send("" + i);
    }

    @Override
    public void print(long l)
    {
    	send("" + l);
    }

    @Override
    public void print(Object obj)
    {
    	send(obj.toString());
    }

    @Override
    public void print(String s)
    {
    	send(s);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args)
    {
    	send(String.format(l, format, args));
        return this;
    }

    @Override
    public PrintStream printf(String format, Object... args)
    {
    	send(String.format(format, args));
        return this;
    }

    @Override
    public void println()
    {
    	send("\n");
    }

    @Override
    public void println(boolean x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(char[] x)
    {
    	send(new String(x) + "\n");
    }

    @Override
    public void println(double x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(char x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(float x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(int x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(long x)
    {
    	send("" + x+"\n");
    }

    @Override
    public void println(Object x)
    {
    	send(x.toString()+"\n");
    }

    @Override
    public void println(String x)
    {
    	send(x+"\n");
    }

    @Override
    public void write(byte[] b) throws IOException
    {
    	send(b);
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
    	send(Arrays.copyOfRange(buf, off, off + len));
    }

    @Override
    public void write(int b)
    {
    	send("" + b);
    }
    
    public static enum Stream
    {
    	STDOUT, STDERR
    }
}
