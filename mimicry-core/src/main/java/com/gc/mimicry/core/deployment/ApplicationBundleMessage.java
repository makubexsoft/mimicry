package com.gc.mimicry.core.deployment;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageType;
import com.google.common.base.Preconditions;

/**
 * This {@link MessageType#DATA} message contains an {@link ApplicationDescriptor} and the binary data of the
 * application bundle. It's used to deploy an application to a remote node.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationBundleMessage extends Message
{

    private static final long serialVersionUID = 7809135854832623344L;
    private ApplicationDescriptor descriptor;
    private byte[] data;

    public ApplicationBundleMessage(ApplicationDescriptor desc, byte[] data)
    {
        super(MessageType.DATA);
        Preconditions.checkNotNull(desc);
        Preconditions.checkNotNull(data);
        this.descriptor = desc;
        this.data = data;
    }

    public ApplicationDescriptor getDescriptor()
    {
        return descriptor;
    }

    public byte[] getData()
    {
        return data;
    }
}
