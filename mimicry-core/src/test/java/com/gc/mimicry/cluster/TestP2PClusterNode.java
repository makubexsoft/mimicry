package com.gc.mimicry.cluster;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.gc.mimicry.cluster.messaging.Message;

// TODO: currently cluster support not in focus of development
@Ignore
public class TestP2PClusterNode
{

    private static final int DISCOVERY_TIMEOUT_MILLIS = 1500;
    private static final int MESSAGE_DELIVERY_TIMEOUT = 3000;

    private ClusterListener listener;
    private ClusterNode nodeA;
    private ClusterNode nodeB;

    @Before
    public void setUp() throws InterruptedException
    {
        listener = mock(ClusterListener.class);
        nodeA = new P2PClusterNode();
        nodeA.addClusterListener(listener);
        nodeB = new P2PClusterNode();
    }

    @After
    public void tearDown()
    {
        nodeB.close();
        nodeA.close();
    }

    @Test
    public void testDiscoverWithinTimeout()
    {
        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeB.getNodeInfo()));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendMessageWithinTimeout()
    {
        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeB.getNodeInfo()));

        Message msg = new Message();
        nodeB.send(msg, nodeA.getNodeInfo().getNodeId());

        verify(listener, timeout(MESSAGE_DELIVERY_TIMEOUT))
                .messageReceived(any(Message.class), eq(nodeB.getNodeInfo()));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testBroadcastMessageWithinTimeout()
    {
        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeB.getNodeInfo()));

        Message msg = new Message();
        nodeB.broadcast(msg);

        verify(listener, timeout(MESSAGE_DELIVERY_TIMEOUT))
                .messageReceived(any(Message.class), eq(nodeB.getNodeInfo()));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testNodeLeft()
    {
        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeB.getNodeInfo()));

        NodeInfo nodeId = nodeB.getNodeInfo();
        nodeB.close();

        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeLeft(nodeId);
    }

    @Test
    public void testSendRoutedMessageWithinTimeout() throws InterruptedException
    {
        verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeB.getNodeInfo()));

        ClusterListener listenerB = mock(ClusterListener.class);
        nodeB.addClusterListener(listenerB);

        ClusterNode nodeC = new P2PClusterNode();
        try
        {
            verify(listener, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeC.getNodeInfo()));
            verify(listenerB, timeout(DISCOVERY_TIMEOUT_MILLIS)).nodeJoined(eq(nodeC.getNodeInfo()));

            Message msg = new Message();
            nodeC.send(msg, nodeA.getNodeInfo().getNodeId());

            verify(listener, timeout(MESSAGE_DELIVERY_TIMEOUT)).messageReceived(any(Message.class),
                    eq(nodeC.getNodeInfo()));
        }
        finally
        {
            nodeC.close();
        }
    }
}
