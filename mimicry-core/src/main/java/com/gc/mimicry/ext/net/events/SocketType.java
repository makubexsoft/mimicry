package com.gc.mimicry.ext.net.events;

/**
 * The type of sockets.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public enum SocketType
{
	/**
	 * Raw IP socket without any layer 4 protocol being applied.
	 */
	RAW,
	/**
	 * IP socket with TCP (layer 4) protocol.
	 */
	TCP,
	/**
	 * IP socket with UDP (layer 4) protocol
	 */
	UDP
}
