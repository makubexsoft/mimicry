package com.gc.mimicry.cluster;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

/**
 * The node information contains the unique id of the node plus additional information such as system architecture,
 * server ip+port, etc.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class NodeInfo implements Serializable
{
    private static final long serialVersionUID = 6702125978161597535L;
    private final UUID nodeId;
    private InetAddress ipAddress;
    private final int controlPort;
    private final int dataPort;
    private String dnsName;
    private final String architecture;
    private final String operatingSystem;
    private final String osVersion;
    private final String javaVersion;
    private final int numberCores;

    NodeInfo(int controlPort, int dataPort)
    {
        this.controlPort = controlPort;
        this.dataPort = dataPort;
        nodeId = UUID.randomUUID();
        architecture = System.getProperty("os.arch");
        operatingSystem = System.getProperty("os.name");
        osVersion = System.getProperty("os.version");
        javaVersion = System.getProperty("java.version");
        numberCores = Runtime.getRuntime().availableProcessors();
    }

    void setIpAddress(InetAddress ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public UUID getNodeId()
    {
        return nodeId;
    }

    public int getNumberCores()
    {
        return numberCores;
    }

    public InetAddress getIpAddress()
    {
        return ipAddress;
    }

    public int getControlPort()
    {
        return controlPort;
    }

    public int getDataPort()
    {
        return dataPort;
    }

    public String getDnsName()
    {
        return dnsName;
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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("NodeInfo[uuid=");
        builder.append(nodeId);
        builder.append(", ip=");
        builder.append(ipAddress);
        builder.append(", controlPort=");
        builder.append(controlPort);
        builder.append(", dataPort=");
        builder.append(dataPort);
        builder.append(", dns=");
        builder.append(dnsName);
        builder.append(", arch=");
        builder.append(architecture);
        builder.append(", os=");
        builder.append(operatingSystem);
        builder.append(", osVersion=");
        builder.append(osVersion);
        builder.append(", javaVersion=");
        builder.append(javaVersion);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        NodeInfo other = (NodeInfo) obj;
        if (nodeId == null)
        {
            if (other.nodeId != null)
            {
                return false;
            }
        }
        else if (!nodeId.equals(other.nodeId))
        {
            return false;
        }
        return true;
    }
}
