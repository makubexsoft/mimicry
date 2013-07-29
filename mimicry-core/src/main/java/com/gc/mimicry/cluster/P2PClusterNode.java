package com.gc.mimicry.cluster;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioDatagramChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.cluster.messaging.MessageType;

public class P2PClusterNode implements ClusterNode, LocalNode
{

    private static final String DISCOVERY_TIMER_NAME;
    private static final int DISCOVERY_PERIOD_MILLIS;
    private static final Logger logger;
    private static final int DEFAULT_DISCOVERY_PORT;
    static
    {
        logger = LoggerFactory.getLogger(P2PClusterNode.class);
        DEFAULT_DISCOVERY_PORT = 5000;
        DISCOVERY_TIMER_NAME = "discoveryTimer";
        DISCOVERY_PERIOD_MILLIS = 1000;
    }

    private final CopyOnWriteArrayList<ClusterListener> listener;
    private final NodeInfo localNode;

    private Channel discoveryChannel;
    private Channel controlServerChannel;
    private Channel dataServerChannel;
    private final ChannelGroup controlChannels;
    private final ChannelGroup dataChannels;

    private final ScheduledExecutorService scheduler;
    private final int discoveryPort;

    private ConnectionlessBootstrap discoveryBootstrap;
    private ClientBootstrap controlClientBootstrap;
    private ServerBootstrap controlServerBootstrap;
    private ClientBootstrap dataClientBootstrap;
    private ServerBootstrap dataServerBootstrap;

    private final Object connectionLock = new Object();
    private final Map<UUID, Channel> connectedControlChannels;
    private final Map<UUID, Channel> connectedDataChannels;

    private final Map<Channel, NodeInfo> channelToNodes;
    private final Map<NodeInfo, Channel> pendingConnections;
    private final Map<Channel, Integer> channelPriorities;

    public P2PClusterNode()
    {
        this(DEFAULT_DISCOVERY_PORT);
    }

    public P2PClusterNode(int discoveryPort)
    {
        this.discoveryPort = discoveryPort;
        listener = new CopyOnWriteArrayList<ClusterListener>();
        controlChannels = new DefaultChannelGroup("controlChannels");
        dataChannels = new DefaultChannelGroup("dataChannels");
        scheduler = new ScheduledThreadPoolExecutor(1);
        connectedControlChannels = new HashMap<UUID, Channel>();
        channelPriorities = new HashMap<Channel, Integer>();
        pendingConnections = new HashMap<NodeInfo, Channel>();
        channelToNodes = new HashMap<Channel, NodeInfo>();
        connectedDataChannels = new HashMap<UUID, Channel>();

        createServerChannel();
        createClientBootstrap();
        createDataServerChannel();
        createDataClientBootstrap();

        localNode = new NodeInfo(((InetSocketAddress) controlServerChannel.getLocalAddress()).getPort(),
                ((InetSocketAddress) dataServerChannel.getLocalAddress()).getPort());
        logger.info("Local node info: " + localNode);

        createDiscoveryChannel();
    }

    private void createDataClientBootstrap()
    {
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        dataClientBootstrap = new ClientBootstrap(factory);

        dataClientBootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {
            @Override
            public ChannelPipeline getPipeline()
            {
                return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new MessageReceiver());
            }
        });

        dataClientBootstrap.setOption("tcpNoDelay", true);
        dataClientBootstrap.setOption("keepAlive", true);
    }

    private void createDataServerChannel()
    {
        ChannelFactory factory;
        factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        dataServerBootstrap = new ServerBootstrap(factory);
        dataServerBootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {
            @Override
            public ChannelPipeline getPipeline()
            {
                return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new MessageReceiver(),
                        new SimpleChannelHandler()
                        {
                            @Override
                            public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
                            {
                                if (e.getMessage() instanceof NodeInfo)
                                {
                                    connectedDataChannels.put(((NodeInfo) e.getMessage()).getNodeId(), ctx.getChannel());
                                    dataChannels.add(ctx.getChannel());
                                    logger.info("Data channel " + ctx.getChannel() + " confirmed.");
                                    ctx.getPipeline().remove(this);
                                }
                                else
                                {
                                    super.messageReceived(ctx, e);
                                }
                            };
                        });
            }
        });

        dataServerBootstrap.setOption("child.tcpNoDelay", true);
        dataServerBootstrap.setOption("child.keepAlive", true);

        dataServerChannel = dataServerBootstrap.bind(new InetSocketAddress(0));
    }

    private void createClientBootstrap()
    {
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        controlClientBootstrap = new ClientBootstrap(factory);

        controlClientBootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {
            @Override
            public ChannelPipeline getPipeline()
            {
                return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new SimpleChannelHandler()
                {
                    @Override
                    public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) throws Exception
                    {
                        if (e.getMessage() instanceof ConfirmedChannelMessage)
                        {
                            final ConfirmedChannelMessage msg = (ConfirmedChannelMessage) e.getMessage();
                            logger.info("Channel " + ctx.getChannel() + " has been confirmed.");
                            msg.node.setIpAddress(((InetSocketAddress) e.getRemoteAddress()).getAddress());

                            ChannelFuture future = dataClientBootstrap.connect(new InetSocketAddress(
                                    ((InetSocketAddress) e.getRemoteAddress()).getAddress(), msg.node.getDataPort()));
                            future.addListener(new ChannelFutureListener()
                            {

                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception
                                {
                                    if (future.isSuccess())
                                    {
                                        connectedDataChannels.put(msg.node.getNodeId(), future.getChannel());
                                        dataChannels.add(future.getChannel());
                                        Channels.write(future.getChannel(), localNode);
                                        logger.info("Data channel " + ctx.getChannel() + " created.");
                                        nodeAddedToCluster(msg.node, ctx.getChannel());
                                    }
                                }
                            });
                            ctx.getPipeline().remove(this);
                        }
                        else
                        {
                            super.messageReceived(ctx, e);
                        }
                    };
                }, new MessageReceiver());
            }
        });

        controlClientBootstrap.setOption("tcpNoDelay", true);
        controlClientBootstrap.setOption("keepAlive", true);
    }

    private void createDiscoveryChannel()
    {

        discoveryBootstrap = new ConnectionlessBootstrap(new OioDatagramChannelFactory(Executors.newCachedThreadPool()));
        discoveryBootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {

            @Override
            public ChannelPipeline getPipeline() throws Exception
            {
                return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new DiscoveryHandler());
            }
        });
        discoveryBootstrap.setOption("loopbackModeDisabled", true);
        discoveryBootstrap.setOption("reuseAddress", true);
        discoveryBootstrap.setOption("broadcast", true);

        discoveryChannel = discoveryBootstrap.bind(new InetSocketAddress(discoveryPort));
        logger.info("DiscoveryChannel opened at " + discoveryChannel.getLocalAddress());
        discoveryChannel.getPipeline().addLast("timingHandler",
                new TimingChannelHandler(scheduler, DISCOVERY_PERIOD_MILLIS, DISCOVERY_TIMER_NAME));
    }

    private void createServerChannel()
    {
        ChannelFactory factory;
        factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        controlServerBootstrap = new ServerBootstrap(factory);
        controlServerBootstrap.setPipelineFactory(new ChannelPipelineFactory()
        {
            @Override
            public ChannelPipeline getPipeline()
            {
                return Channels.pipeline(new ObjectEncoder(), new ObjectDecoder(), new MessageReceiver(),
                        new WelcomeHandler(), new SimpleChannelHandler()
                        {

                            @Override
                            public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
                                    throws Exception
                            {

                                logger.info("Accepted TCP connection from " + e.getChannel().getRemoteAddress());
                            };
                        });
            }
        });

        controlServerBootstrap.setOption("child.tcpNoDelay", true);
        controlServerBootstrap.setOption("child.keepAlive", true);

        controlServerChannel = controlServerBootstrap.bind(new InetSocketAddress(0));
        logger.info("ServerChannel opened at " + controlServerChannel.getLocalAddress());
    }

    @Override
    public void close()
    {
        logger.info("Closing node " + localNode);
        discoveryChannel.close();
        controlServerChannel.close();
        controlChannels.close();
        dataChannels.close();
        dataServerChannel.close();
        dataClientBootstrap.releaseExternalResources();
        dataServerBootstrap.releaseExternalResources();
        controlClientBootstrap.releaseExternalResources();
        discoveryBootstrap.releaseExternalResources();
        controlServerBootstrap.releaseExternalResources();
    }

    @Override
    public void addClusterListener(ClusterListener l)
    {
        listener.add(l);
    }

    @Override
    public void removeClusterListener(ClusterListener l)
    {
        listener.remove(l);
    }

    protected void fireNodeJoined(NodeInfo node)
    {
        for (ClusterListener l : listener)
        {
            l.nodeJoined(node);
        }
    }

    protected void fireNodeLeft(NodeInfo node)
    {
        for (ClusterListener l : listener)
        {
            l.nodeLeft(node);
        }
    }

    protected void fireMessageReceived(NodeInfo sourceNode, Message msg)
    {
        for (ClusterListener l : listener)
        {
            l.messageReceived(msg, sourceNode);
        }
    }

    @Override
    public NodeInfo getNodeInfo()
    {
        return localNode;
    }

    @Override
    public void broadcast(Message msg)
    {
        if (msg.getType() == MessageType.DATA)
        {
            dataChannels.write(msg);
        }
        else
        {
            controlChannels.write(msg);
        }
    }

    @Override
    public void send(Message msg, UUID destinationNode)
    {
        Channel channel;
        if (msg.getType() == MessageType.DATA)
        {
            channel = connectedDataChannels.get(destinationNode);
        }
        else
        {
            channel = connectedControlChannels.get(destinationNode);
        }
        Channels.write(channel, msg);
    }

    private void discoveredNode(NodeInfo node)
    {
        logger.info("Discovered node " + node);
        synchronized (connectionLock)
        {
            if (!connectedControlChannels.containsKey(node) && !pendingConnections.containsKey(node))
            {
                connectTo(node);
            }
        }
    }

    private void connectTo(final NodeInfo node)
    {
        synchronized (connectionLock)
        {
            ChannelFuture future;
            future = controlClientBootstrap.connect(new InetSocketAddress(node.getIpAddress(), node.getControlPort()));
            pendingConnections.put(node, future.getChannel());
            future.addListener(new ChannelFutureListener()
            {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception
                {
                    if (future.isSuccess())
                    {
                        connectedTo(node, future.getChannel());
                    }
                    future.removeListener(this);
                }
            });
        }
    }

    private void connectedTo(final NodeInfo node, Channel channel)
    {
        logger.info("Connected to node " + node + " channel " + channel);
        sendWelcomeMessageTo(channel);
    }

    private void nodeAddedToCluster(final NodeInfo node, final Channel channel)
    {
        if (localNode.getIpAddress() == null)
        {
            localNode.setIpAddress(((InetSocketAddress) channel.getLocalAddress()).getAddress());
        }

        logger.info("Welcome node " + node + " to cluster at channel " + channel);
        synchronized (connectionLock)
        {
            connectedControlChannels.put(node.getNodeId(), channel);
            channelToNodes.put(channel, node);
        }
        controlChannels.add(channel);
        fireNodeJoined(node);
        channel.getCloseFuture().addListener(new ChannelFutureListener()
        {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                fireNodeLeft(node);
                future.removeListener(this);
                synchronized (connectionLock)
                {
                    connectedControlChannels.remove(node);
                    channelToNodes.remove(channel);
                }
            }
        });
    }

    private boolean welcomeNode(NodeInfo node, int priority, Channel channel)
    {
        logger.debug("Received WELCOME MESSAGE: " + priority);
        synchronized (connectionLock)
        {

            // Reject duplicate connection requests
            if (connectedControlChannels.containsKey(node))
            {
                channel.close();
                return true;
            }

            // Accept new Node since no pending request
            if (!pendingConnections.containsKey(node))
            {
                Channels.write(channel, new ConfirmedChannelMessage(localNode));
                nodeAddedToCluster(node, channel);
                return true;
            }

            Channel unconfirmedChannel = pendingConnections.get(node);
            Integer channelPriority = channelPriorities.remove(unconfirmedChannel);

            if (priority > channelPriority)
            {

                logger.info("Accepting incoming connection " + node + ". " + priority + " > " + channelPriority);
                Channels.write(channel, new ConfirmedChannelMessage(localNode));
                nodeAddedToCluster(node, channel);
                logger.info("Closing unconfirmed connection " + unconfirmedChannel);
                unconfirmedChannel.close();
                return true;

            }
            else if (priority < channelPriority)
            {

                logger.info("Closing incoming connection " + channel + " with lower priority " + priority);
                channel.close();
                return true;

            }
            else
            {

                WelcomeMessage welcome = sendWelcomeMessageTo(unconfirmedChannel);
                logger.debug("resending welcome message: " + welcome.priority + " on " + unconfirmedChannel);
                return false;
            }
        }
    }

    private WelcomeMessage sendWelcomeMessageTo(Channel existingChannel)
    {
        WelcomeMessage welcome = new WelcomeMessage(localNode);
        channelPriorities.put(existingChannel, welcome.priority);
        Channels.write(existingChannel, welcome);
        return welcome;
    }

    private class WelcomeHandler extends SimpleChannelHandler
    {
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
        {
            if (e.getMessage() instanceof WelcomeMessage)
            {
                WelcomeMessage welcome = (WelcomeMessage) e.getMessage();
                welcome.node.setIpAddress(((InetSocketAddress) e.getRemoteAddress()).getAddress());
                boolean done = welcomeNode(welcome.node, welcome.priority, e.getChannel());
                if (done)
                {
                    ctx.getPipeline().remove(this);
                }
            }
            else
            {
                super.messageReceived(ctx, e);
            }
        }
    }

    private static class WelcomeMessage implements Serializable
    {
        private static final long serialVersionUID = -7135221766312645797L;
        public NodeInfo node;
        public int priority;

        public WelcomeMessage(NodeInfo node)
        {
            this.node = node;
            priority = new Random(System.nanoTime()).nextInt();
        }
    }

    private static class ConfirmedChannelMessage implements Serializable
    {
        private static final long serialVersionUID = 3819837711889758693L;
        public NodeInfo node;

        public ConfirmedChannelMessage(NodeInfo node)
        {
            this.node = node;
        }
    }

    private class MessageReceiver extends SimpleChannelHandler
    {

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
        {
            if (e.getMessage() instanceof Message)
            {
                NodeInfo sourceNode = channelToNodes.get(ctx.getChannel());
                if (sourceNode == null)
                {
                    throw new NullPointerException("[" + getNodeInfo().getNodeId() + "] " + e.getMessage() + ";"
                            + ctx.getChannel());
                }
                fireMessageReceived(sourceNode, (Message) e.getMessage());
            }
            else
            {
                super.messageReceived(ctx, e);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
        {
            logger.debug("Closing connection due to exception.", e);
            e.getChannel().close();
        }
    }

    private class DiscoveryHandler extends SimpleChannelHandler
    {

        @Override
        public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception
        {
            if (e instanceof TimingChannelEvent)
            {
                logger.info("Broadcasting discovery message...");
                Channels.write(ctx.getChannel(), localNode, new InetSocketAddress("255.255.255.255", discoveryPort));
            }
            else
            {
                super.handleDownstream(ctx, e);
            }
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
        {
            Object msg = e.getMessage();
            if (msg instanceof NodeInfo)
            {
                NodeInfo node = (NodeInfo) msg;
                node.setIpAddress(((InetSocketAddress) e.getRemoteAddress()).getAddress());
                if (!node.equals(localNode))
                {
                    discoveredNode(node);
                }
            }
            else
            {
                super.messageReceived(ctx, e);
            }
        }
    }
}
