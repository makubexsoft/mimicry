package org.mimicry.engine.event;

import java.util.UUID;

import org.mimicry.util.VectorClock;


/**
 * An identity represents a unique actor that is able to spawn events. Identities are used to populate logical clocks.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Identity
{
    private final VectorClock<UUID> clock;
    private final UUID id;
    private final String name;

    private Identity(String name)
    {
        id = UUID.randomUUID();
        this.name = name;
        clock = new VectorClock<UUID>();
    }

    public static Identity create()
    {
        return new Identity("unnamed");
    }

    public static Identity create(String name)
    {
        return new Identity(name);
    }

    public VectorClock<UUID> getClock()
    {
        return clock;
    }

    public UUID getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Identity [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Identity other = (Identity) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        return true;
    }
}
