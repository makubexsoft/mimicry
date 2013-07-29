package com.gc.mimicry.cluster.session.controller;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.cluster.messaging.MessageReceiver;
import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.gc.mimicry.cluster.messaging.Publisher;
import com.gc.mimicry.cluster.messaging.Subscriber;
import com.gc.mimicry.cluster.messaging.Topic;
import com.gc.mimicry.cluster.messaging.TopicSession;
import com.gc.mimicry.cluster.session.QuerySessionRequest;
import com.gc.mimicry.cluster.session.QuerySessionResponse;
import com.gc.mimicry.cluster.session.SessionConstants;
import com.gc.mimicry.cluster.session.SessionCreatedMessage;
import com.gc.mimicry.cluster.session.SessionInfo;
import com.google.common.base.Preconditions;

public class SessionBrowser implements Closeable, MessageReceiver
{

    private final CopyOnWriteArrayList<SessionBrowserListener> listener;
    private final List<SessionInfo> sessions;
    private final Publisher publisher;
    private final Subscriber subscriber;
    private final TopicSession session;

    public SessionBrowser(MessagingSystem messaging)
    {
        Preconditions.checkNotNull(messaging);
        sessions = new ArrayList<SessionInfo>();
        listener = new CopyOnWriteArrayList<SessionBrowserListener>();
        Topic topic = messaging.lookupTopic(SessionConstants.SESSION_MGNT_TOPIC_NAME);
        session = messaging.createTopicSession(topic);
        publisher = session.createPublisher();
        subscriber = session.createSubscriber();
        subscriber.setMessageReceiver(this);
    }

    public List<SessionInfo> getSessions()
    {
        return sessions;
    }

    public void querySessions()
    {
        publisher.send(new QuerySessionRequest());
    }

    public void messageReceived(Topic topic, Message msg)
    {
        if (msg instanceof QuerySessionResponse)
        {
            QuerySessionResponse response = (QuerySessionResponse) msg;
            SessionInfo sessionInfo = response.getSessionInfo();
            addSessionIfNotExists(sessionInfo);
        }
        else if (msg instanceof SessionCreatedMessage)
        {
            SessionCreatedMessage m = (SessionCreatedMessage) msg;
            SessionInfo info = new SessionInfo(m.getSessionId(), m.getParticipants());
            addSessionIfNotExists(info);
        }
    }

    private void addSessionIfNotExists(SessionInfo sessionInfo)
    {
        if (!sessions.contains(sessionInfo))
        {
            int index = sessions.size();
            sessions.add(sessionInfo);
            fireSessionAdded(index, sessionInfo);
        }
    }

    private void fireSessionAdded(int index, SessionInfo info)
    {
        for (SessionBrowserListener l : listener)
        {
            l.sessionAdded(index, this, info);
        }
    }

    public void addSessionBrowserListener(SessionBrowserListener l)
    {
        listener.add(l);
    }

    public void removeSessionBrowserListener(SessionBrowserListener l)
    {
        listener.remove(l);
    }

    public void close()
    {
        publisher.close();
        subscriber.close();
        session.close();
    }
}
