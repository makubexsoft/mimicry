package com.gc.mimicry.engine.remote;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.gc.mimicry.engine.EngineInfo;

public class EngineImporter
{
    private EngineImporter()
    {
    }

    public static ImportedEngine importEngine(EngineInfo engineInfo, InetAddress nodeAddress) throws RuntimeException
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(nodeAddress.getHostAddress());
            RemoteEngine remoteEngine = (RemoteEngine) registry.lookup("mimicryEngine");
            return new ImportedEngine(remoteEngine);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to export engine via RMI.", e);
        }
    }
}
