package org.mimicry.remote;

import java.net.InetAddress;

import org.mimicry.EngineInfo;


/**
 * Implement this interface to get notified when an advertisement of a remote engine has been received.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface AdvertisementListener
{
    /**
     * An advertisement of a remote engine has been received. This method might be invoked multiple times with the same
     * parameters if the same advertisement was sent multiple times by the advertiser or was replicated by the
     * underlying network.
     * 
     * @param info
     *            The information of the engine.
     * @param nodeAddress
     *            The address of the network node that sent the advertisement.
     */
    public void advertisementReceived(EngineInfo info, InetAddress nodeAddress);
}
