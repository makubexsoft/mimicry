package org.mimicry.engine.remote;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.mimicry.cep.CEPEngineFactory;
import org.mimicry.engine.EngineInfo;


public class EngineImporter
{
    private EngineImporter()
    {
    }

    public static ImportedEngine importEngine(EngineInfo engineInfo, InetAddress nodeAddress,
            CEPEngineFactory engineFactory) throws RuntimeException
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(nodeAddress.getHostAddress());
            RemoteEngine remoteEngine = (RemoteEngine) registry.lookup("mimicryEngine");
            return new ImportedEngine(remoteEngine, engineFactory);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to export engine via RMI.", e);
        }
    }
}
