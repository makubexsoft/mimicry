package com.gc.mimicry.bridge.aspects;

import java.io.PrintStream;

import com.gc.mimicry.bridge.LoggingPrintStream;
import com.gc.mimicry.bridge.SimulatorBridge;

public aspect LoggingAspect {

	private static PrintStream out;
	private static PrintStream err;

	public pointcut getOutStream() : get(PrintStream System.out);

	public pointcut getErrStream() : get(PrintStream System.err);

	public pointcut setOutStream() : call(void System.setOut(..));

	public pointcut setErrStream() : call(void System.setErr(..));

	Object around() : getOutStream() {
		if (out == null) {
			out = new LoggingPrintStream("com.gc.mimicry.application.out."
					+ SimulatorBridge.getApplicationId());
		}
		return out;
	}

	Object around() : getErrStream() {
		if (err == null) {
			err = new LoggingPrintStream("com.gc.mimicry.application.err."
					+ SimulatorBridge.getApplicationId());
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

