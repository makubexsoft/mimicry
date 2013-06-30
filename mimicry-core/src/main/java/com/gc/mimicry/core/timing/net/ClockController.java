package com.gc.mimicry.core.timing.net;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageReceiver;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Subscriber;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.gc.mimicry.core.timing.ClockType;
import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;

public class ClockController extends BaseResourceManager implements MessageReceiver
{

    public ClockController(MessagingSystem messaging, UUID sessionId)
    {
        pendingActions = new HashMap<UUID, Future<?>>();
        Topic topic = messaging.lookupTopic(SESSION_LOCAL_CHANNEL_NAME);
        session = messaging.createTopicSession(topic);
        attachResource(session);
        subscriber = session.createSubscriber();
        attachResource(subscriber);
        publisher = session.createPublisher();
        attachResource(publisher);
        subscriber.setMessageReceiver(this);
    }

    public Future<?> installClock(ClockType clockType)
    {
        Future<?> future = new DefaultFuture();
        InstallClockMessage msg = new InstallClockMessage(clockType);
        synchronized (pendingActions)
        {
            pendingActions.put(msg.getId(), future);
        }
        publisher.send(msg);
        return future;
    }

    public Future<?> startClock(double multiplier)
    {
        return null;
    }

    public Future<?> stopClock()
    {
        return null;
    }

    public Future<?> sampleClock(long deltaMillis)
    {
        return null;
    }

    @Override
    public void messageReceived(Topic topic, Message msg)
    {
        // TODO Auto-generated method stub

    }

    private static final String SESSION_LOCAL_CHANNEL_NAME = "clock";
    private final TopicSession session;
    private final Subscriber subscriber;
    private final Publisher publisher;
    private final Map<UUID, Future<?>> pendingActions;
}
