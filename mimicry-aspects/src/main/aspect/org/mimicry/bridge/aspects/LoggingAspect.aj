package org.mimicry.bridge.aspects;

import java.io.PrintStream;

import org.mimicry.bridge.aspects.EventGeneratingPrintStream.Stream;

public aspect LoggingAspect {

	private static PrintStream out;
	private static PrintStream err;

	public pointcut getOutStream() : get(PrintStream System.out) && !within(org.mimicry..*);

	public pointcut getErrStream() : get(PrintStream System.err) && !within(org.mimicry..*);

	public pointcut setOutStream() : call(void System.setOut(..)) && !within(org.mimicry..*);

	public pointcut setErrStream() : call(void System.setErr(..)) && !within(org.mimicry..*);

	Object around() : getOutStream() {
		if (out == null) {
			out = new EventGeneratingPrintStream(Stream.STDOUT);
		}
		return out;
	}

	Object around() : getErrStream() {
		if (err == null) {
			err = new EventGeneratingPrintStream(Stream.STDERR);
		}
		return err;
	}

	void around() : setOutStream()
	{
		// suppress
	}

	void around() : setErrStream()
	{
		// suppress
	}
}

