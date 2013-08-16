package com.gc.mimicry.bridge.aspects;

import java.io.PrintStream;

import com.gc.mimicry.bridge.LoggingPrintStream;
import com.gc.mimicry.bridge.SimulatorBridge;

public aspect LoggingAspect {

	private static PrintStream out;
	private static PrintStream err;

	public pointcut getOutStream() : get(PrintStream System.out) && !within(com.gc.mimicry..*);

	public pointcut getErrStream() : get(PrintStream System.err) && !within(com.gc.mimicry..*);

	public pointcut setOutStream() : call(void System.setOut(..)) && !within(com.gc.mimicry..*);

	public pointcut setErrStream() : call(void System.setErr(..)) && !within(com.gc.mimicry..*);

	Object around() : getOutStream() {
		if (out == null) {
			out = new EventGeneratingPrintStream();
		}
		return out;
	}

	Object around() : getErrStream() {
		if (err == null) {
			err = new EventGeneratingPrintStream();
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

