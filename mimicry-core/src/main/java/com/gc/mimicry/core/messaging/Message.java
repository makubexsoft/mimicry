package com.gc.mimicry.core.messaging;

import java.io.Serializable;

/**
 * Base class for all messages.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Message implements Serializable
{
    private static final long serialVersionUID = 5710346841904675911L;
    private MessageType type;

    /**
     * Creates a message of type {@link MessageType#CUSTOM}.
     */
    public Message()
    {
        this(MessageType.CUSTOM);
    }

    /**
     * Creates a message of the given type.
     * 
     * @param type
     *            The desired message type.
     */
    protected Message(MessageType type)
    {
        this.type = type;
    }

    /**
     * Returns the type of the message.
     * 
     * @return The type of the message.
     */
    public MessageType getType()
    {
        return type;
    }
}
