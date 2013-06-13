package com.gc.mimicry.bridge.aspects.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public aspect NetworkingAspect 
{
	// ------------------------------------------------------------------------------------------
	// NetworkInterface
	// ------------------------------------------------------------------------------------------
	public pointcut networkInterfaceByAddress( InetAddress address ) : 
					!within(com.gc.mimicry..*) && 
					call(NetworkInterface NetworkInterface.getByInetAddress(InetAddress) ) &&
					args(address);

	public pointcut networkInterfaceByName(String name) :
					!within(com.gc.mimicry..*) && 
					call(NetworkInterface NetworkInterface.getByName(String)) &&
					args(name);
	
	public pointcut getNetworkInterfaces() :
					!within(com.gc.mimicry..*) && 
					call(Enumeration NetworkInterface.getNetworkInterfaces());
	
	// ------------------------------------------------------------------------------------------
	// InetAddress - DNS
	// ------------------------------------------------------------------------------------------
	public pointcut getAllByName(String host) : 
					!within(com.gc.mimicry..*) && 
					call(InetAddress[] InetAddress.getAllByName(String)) && 
					args(host); 
	
	public pointcut getByAddress(byte[] addr) : 
					!within(com.gc.mimicry..*) && 
					call(InetAddress InetAddress.getByAddress(byte[])) && 
					args(addr); 
	
	public pointcut getByAddress2(String host, byte[] addr) : 
					!within(com.gc.mimicry..*) && 
					call(InetAddress InetAddress.getByAddress(String, byte[])) && 
					args(host, addr);
	
	public pointcut getByName(String host) : 
					!within(com.gc.mimicry..*) && 
					call(InetAddress InetAddress.getByName(String)) &&
					args(host);
	
	public pointcut getLocalHost() :
					!within(com.gc.mimicry..*) && 
					call(InetAddress InetAddress.getLocalHost()); 
	
	// ------------------------------------------------------------------------------------------
	// InetAddress - ICMP
	// ------------------------------------------------------------------------------------------
	public pointcut isReachable(InetAddress addr, int timeout) : 
					call(boolean InetAddress+.isReachable(int)) && 
					args(timeout) && 
					target(addr); 
	
	public pointcut isReachable2(InetAddress addr, NetworkInterface netif, int ttl, int timeout) : 
					call(boolean InetAddress+.isReachable(NetworkInterface, int, int)) &&
					args(netif, ttl, timeout) &&
					target(addr); 
	
	// ------------------------------------------------------------------------------------------
	// InetAddress - RDNS
	// ------------------------------------------------------------------------------------------
	public pointcut getCanonicalHostName(InetAddress addr) : 
					call(String InetAddress+.getCanonicalHostName()) &&
					target(addr);
	
	public pointcut getHostName(InetAddress addr) : 
					call(String InetAddress+.getHostName()) &&
					target(addr); 
	
}
