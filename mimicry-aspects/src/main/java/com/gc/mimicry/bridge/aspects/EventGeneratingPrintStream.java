package com.gc.mimicry.bridge.aspects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.ext.stdio.events.ConsoleOutputEvent;

public class EventGeneratingPrintStream extends PrintStream
{
	public EventGeneratingPrintStream()
    {
        super(new ByteArrayOutputStream());
    }
	
	private void send(String text)
	{
		ConsoleOutputEvent evt = new ConsoleOutputEvent( text );
		evt.setSourceApp( SimulatorBridge.getApplicationId() );
		SimulatorBridge.getEventBridge().dispatchEventToStack( evt );
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
    	send(Arrays.asList(b).toString());
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
    	send(Arrays.asList(Arrays.copyOfRange(buf, off, off + len)).toString());
    }

    @Override
    public void write(int b)
    {
    	send("" + b);
    }
}
