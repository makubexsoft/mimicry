package org.mimicry.engine.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.mimicry.engine.EngineInfo;
import org.mimicry.engine.local.LocalEngine;


public class EngineAdvertiser
{
    public static final long DEFAULT_ADVERTISMENT_DELAY = 500;
    private volatile long advertismentMillis;
    private final Thread thread;
    private final LocalEngine engine;
    private volatile boolean running;
    private final String multicastAddress = "239.1.2.3";
    private final int port = 18000;

    public EngineAdvertiser(LocalEngine engine)
    {
        this(engine, DEFAULT_ADVERTISMENT_DELAY);
    }

    public EngineAdvertiser(LocalEngine engine, long advertismentMillis)
    {
        this.advertismentMillis = advertismentMillis;
        running = true;
        this.engine = engine;
        thread = new Thread(new Advertiser(), "EngineAdvertiser");
        thread.setDaemon(true);
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

    private class Advertiser implements Runnable
    {
        private MulticastSocket socket;

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
                advertise();
                try
                {
                    Thread.sleep(advertismentMillis);
                }
                catch (InterruptedException e)
                {
                }
            }
        }

        private void advertise()
        {
            try
            {
                byte[] info = serializeInfo(engine.getEngineInfo());
                DatagramPacket packet = new DatagramPacket(info, info.length);
                packet.setPort(port);
                packet.setAddress(InetAddress.getByName(multicastAddress));
                socket.send(packet);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        private byte[] serializeInfo(EngineInfo info) throws IOException
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(info);
            oout.close();
            return out.toByteArray();
        }
    }
}
