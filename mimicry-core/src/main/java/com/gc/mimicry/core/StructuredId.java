package com.gc.mimicry.core;

import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;

/**
 * A structured id used to assign unique ids based on happened-before relationships. In order to create a related sub
 * key invoke the {@link #createSubId()} method.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class StructuredId
{

    private StructuredId parent;
    private String id;
    private AtomicInteger numSubIds;

    public StructuredId()
    {
        id = "1";
        numSubIds = new AtomicInteger();
    }

    private StructuredId(StructuredId parent, String id)
    {
        Preconditions.checkNotNull(id);
        this.parent = parent;
        this.id = id;
        numSubIds = new AtomicInteger();
    }

    /**
     * Creates a new sub key where its parent is set to this id. The created sub id consists of the id of this node plus
     * the number of current existing sub nodes. Thereby each sub id is unique while preserving the relationship between
     * the ids.
     * 
     * <pre>
     * this(id="1", children=0)
     * subid-0(id="1.1", children=0); this(id="1", children=1)
     * subid-1(id="1.2", children=0); this(id="1", children=2)
     * subid-1-0(id="1.2.1", children=0); subid-1(id="1.2", children=1); this(id="1", children=2)
     * </pre>
     * 
     * @return
     */
    public StructuredId createSubId()
    {
        return new StructuredId(this, id + "." + numSubIds.incrementAndGet());
    }

    public String toString()
    {
        return id;
    }

    /**
     * Returns the parent id this id has been derived from.
     * 
     * @return
     */
    public StructuredId getParent()
    {
        return parent;
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
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StructuredId other = (StructuredId) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
}
