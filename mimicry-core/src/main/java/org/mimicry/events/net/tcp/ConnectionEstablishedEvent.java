package org.mimicry.events.net.tcp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


/**
 * This event indicates a newly established TCP/IP connection.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConnectionEstablishedEvent extends ApplicationEvent
{

    /**
     * Returns the address of the peer that initiated the connection.
     * 
     * @return
     */
    public InetSocketAddress getClientAddress();

    public void setClientAddress(InetSocketAddress value);

    /**
     * Returns the address of the peer that accepted the incoming connection.
     * 
     * @return
     */
    public InetSocketAddress getServerAddress();

    public void setServerAddress(InetSocketAddress value);

}
