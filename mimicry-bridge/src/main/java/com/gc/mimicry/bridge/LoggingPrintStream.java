package com.gc.mimicry.bridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingPrintStream extends PrintStream {
	private final Logger logger;

	public LoggingPrintStream(String loggerName) {
		super(new ByteArrayOutputStream());
		logger = LoggerFactory.getLogger(loggerName);
	}

	@Override
	public PrintStream append(char c) {
		logger.info( "" + c);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq) {
		logger.info( "" + csq);
		return this;
	}

	@Override
	public PrintStream append(CharSequence csq, int start, int end) {
		logger.info( "" + csq.subSequence(start, end));
		return this;
	}

	@Override
	public PrintStream format(Locale l, String format, Object... args) {
		logger.info( String.format(l, format, args));
		return this;
	}

	@Override
	public PrintStream format(String format, Object... args) {
		logger.info( String.format(format, args));
		return this;
	}

	@Override
	public void print(boolean b) {
		logger.info( "" + b);
	}

	@Override
	public void print(char c) {
		logger.info( "" + c);
	}

	@Override
	public void print(char[] s) {
		logger.info( new String(s));
	}

	@Override
	public void print(double d) {
		logger.info( "" + d);
	}

	@Override
	public void print(float f) {
		logger.info( "" + f);
	}

	@Override
	public void print(int i) {
		logger.info( "" + i);
	}

	@Override
	public void print(long l) {
		logger.info( "" + l);
	}

	@Override
	public void print(Object obj) {
		logger.info( obj.toString());
	}

	@Override
	public void print(String s) {
		logger.info( s);
	}

	@Override
	public PrintStream printf(Locale l, String format, Object... args) {
		logger.info( String.format(l, format, args));
		return this;
	}

	@Override
	public PrintStream printf(String format, Object... args) {
		logger.info( String.format(format, args));
		return this;
	}

	@Override
	public void println() {
	}

	@Override
	public void println(boolean x) {
		logger.info( "" + x);
	}

	@Override
	public void println(char[] x) {
		logger.info( new String(x));
	}

	@Override
	public void println(double x) {
		logger.info( "" + x);
	}

	@Override
	public void println(char x) {
		logger.info( "" + x);
	}

	@Override
	public void println(float x) {
		logger.info( "" + x);
	}

	@Override
	public void println(int x) {
		logger.info( "" + x);
	}

	@Override
	public void println(long x) {
		logger.info( "" + x);
	}

	@Override
	public void println(Object x) {
		logger.info( x.toString());
	}

	@Override
	public void println(String x) {
		logger.info(  x);
	}

	@Override
	public void write(byte[] b) throws IOException {
		logger.info( Arrays.asList(b).toString());
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		logger.info( Arrays.asList(Arrays.copyOfRange(buf,off, off + len)).toString());
	}

	@Override
	public void write(int b) {
		logger.info( "" + b);
	}
}
