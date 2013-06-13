package com.gc.mimicry.core.messaging;

/**
 * Interface of a message receiver that can be attached to a {@link Subscriber}.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface MessageReceiver
{

	/**
	 * Invoked when a message has been received on the given {@link Topic}.
	 * 
	 * @param topic
	 *            The topic on which the message has been received.
	 * @param msg
	 *            The received message.
	 */
	public void messageReceived( Topic topic, Message msg );
}
