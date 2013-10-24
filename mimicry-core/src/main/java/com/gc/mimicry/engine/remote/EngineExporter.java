package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.gc.mimicry.engine.local.LocalEngine;

public class EngineExporter
{
    private EngineExporter()
    {
    }

    public static EngineAdvertiser exportEngine(LocalEngine engine) throws RuntimeException
    {
        try
        {
            ExportedEngine remoteEngine = new ExportedEngine(engine);
            Registry registry = getOrCreateRegistry();
            registry.bind("mimicryEngine", remoteEngine);
            return new EngineAdvertiser(engine);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to export engine via RMI.", e);
        }
    }

    private static Registry getOrCreateRegistry() throws RemoteException
    {
        Registry registry;
        try
        {
            registry = LocateRegistry.getRegistry();
        }
        catch (RemoteException e)
        {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        return registry;
    }
}
