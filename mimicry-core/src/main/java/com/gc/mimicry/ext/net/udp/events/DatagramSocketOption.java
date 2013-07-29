package com.gc.mimicry.ext.net.udp.events;

import java.net.DatagramSocket;

/**
 * Socket options for {@link DatagramSocket}s.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public enum DatagramSocketOption
{
	SO_TIMEOUT, SEND_BUFFER_SIZE, RECEIVE_BUFFER_SIZE, REUSE_ADDRESS, BROADCAST, TRAFFIC_CLASS
}
