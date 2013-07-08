package com.gc.mimicry;

import com.beust.jcommander.Parameter;

/**
 * Container class for the command line arguments.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Arguments
{
	@Parameter(names = "-scriptPath", description = "Path to the simulation scripts.")
	public String	scriptPath	= PropertyHelper.getValue( PropertyHelper.MIMICRY_SCRIPT_PATH, "." );

	@Parameter(names = "-mainScript", description = "Name of the main script file.", required = true)
	public String	mainScript;
}
