package com.gc.mimicry.bridge.aspects;

import java.io.File;

/**
 * Aspect for assuring to recognize all sub-processes spawned by the simulated
 * application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect IsolationAspect
{
	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut runtimeExec( String command ) : 
					!within(com.gc.mimicry..*) && 
					call(Process Runtime.exec(String)) &&
					args(command);

	public pointcut runtimeExec2( String[] cmdarray ) :
					!within(com.gc.mimicry..*) && 
					call(Process Runtime.exec(String[])) &&
		 			args(cmdarray);

	public pointcut runtimeExec3( String[] cmdarray, String[] envp ) :
					!within(com.gc.mimicry..*) && 
		 			call(Process Runtime.exec(String[], String[])) &&
		 			args(cmdarray, envp);

	public pointcut runtimeExec4( String[] cmdarray, String[] envp, File dir ) :
					!within(com.gc.mimicry..*) && 
		 			call(Process Runtime.exec(String[], String[], File)) &&
		 			args(cmdarray, envp, dir);

	public pointcut runtimeExec5( String command, String[] envp ) :
					!within(com.gc.mimicry..*) && 
		 			call(Process Runtime.exec(String, String[]) ) &&
		 			args(command, envp);

	public pointcut runtimeExec6( String command, String[] envp, File dir ) :
					!within(com.gc.mimicry..*) && 
		 			call(Process Runtime.exec(String, String[], File)) &&
		 			args(command, envp, dir);

	public pointcut processBuilderStart( ProcessBuilder builder ) :
					!within(com.gc.mimicry..*) && 
		 			call(Process ProcessBuilder.start()) &&
		 			target(builder);

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
}
