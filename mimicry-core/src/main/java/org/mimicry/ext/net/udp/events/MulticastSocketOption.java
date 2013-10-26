package org.mimicry.ext.net.udp.events;

public enum MulticastSocketOption
{
	/**
	 * Value is of type integer.
	 */
	TTL,
	/**
	 * The IP_MULTICAST_IF option.
	 * Value is of type InetAddress.
	 */
	IFC_BY_INET_ADDRESS,
	/**
	 * The IP_MULTICAST_IF2 option.
	 * Value is of type NetworkInterface.
	 */
	IFC_BY_NETWORK_INTERFACE,
	/**
	 * Value is of type boolean.
	 */
	IP_MULTICAST_LOOP
}
