package com.gc.mimicry.engine.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.engine.EngineInfo;

public class EngineFinder
{
    public static final long DEFAULT_FIND_DELAY = 500;
    private volatile long advertismentMillis;
    private final Thread thread;
    private final List<AdvertisementListener> listener;
    private volatile boolean running;
    private final String multicastAddress = "239.1.2.3";
    private final int port = 18000;

    public EngineFinder()
    {
        this(DEFAULT_FIND_DELAY);
    }

    public EngineFinder(long advertismentMillis)
    {
        this.advertismentMillis = advertismentMillis;
        running = true;
        listener = new CopyOnWriteArrayList<AdvertisementListener>();
        thread = new Thread(new Discoverer(), "EngineFinder");
        thread.setDaemon(true);
    }

    public void addAdvertismentListener(AdvertisementListener l)
    {
        listener.add(l);
    }

    public void removeAdvertismentListener(AdvertisementListener l)
    {
        listener.remove(l);
    }

    public void start()
    {
        thread.start();
    }

    public void stop()
    {
        running = false;
        thread.interrupt();
    }

    private class Discoverer implements Runnable
    {
        private MulticastSocket socket;
        private final byte[] buffer = new byte[4096];

        @Override
        public void run()
        {
            try
            {
                socket = new MulticastSocket(port);
                socket.joinGroup(InetAddress.getByName(multicastAddress));
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            while (running)
            {
                receivePacket();
                try
                {
                    Thread.sleep(advertismentMillis);
                }
                catch (InterruptedException e)
                {
                }
            }
        }

        private void receivePacket()
        {
            try
            {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream in = new ByteArrayInputStream(buffer, 0, packet.getLength());
                ObjectInputStream bin = new ObjectInputStream(in);
                EngineInfo info = (EngineInfo) bin.readObject();
                notifyListener(info, packet.getAddress());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        private void notifyListener(EngineInfo info, InetAddress nodeAddress)
        {
            for (AdvertisementListener l : listener)
            {
                l.advertisementReceived(info, nodeAddress);
            }
        }
    }
}
