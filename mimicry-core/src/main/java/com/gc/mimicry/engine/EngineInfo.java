package com.gc.mimicry.engine;

import java.io.Serializable;

/**
 * This data structure contains information about an engine such as Architecture, CPU-Cores, Java-Version, etc.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EngineInfo implements Serializable
{
    private static final long serialVersionUID = 2959345922566697210L;
    private String architecture;
    private String operatingSystem;
    private String osVersion;
    private String javaVersion;
    private int numberCores;

    private EngineInfo()
    {
    }

    public static EngineInfo fromLocalJVM()
    {
        EngineInfo info = new EngineInfo();
        info.architecture = System.getProperty("os.arch");
        info.operatingSystem = System.getProperty("os.name");
        info.osVersion = System.getProperty("os.version");
        info.javaVersion = System.getProperty("java.version");
        info.numberCores = Runtime.getRuntime().availableProcessors();
        return info;
    }

    public String getArchitecture()
    {
        return architecture;
    }

    public String getOperatingSystem()
    {
        return operatingSystem;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public String getJavaVersion()
    {
        return javaVersion;
    }

    public int getNumberCores()
    {
        return numberCores;
    }
}
