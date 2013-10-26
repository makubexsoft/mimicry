package org.mimicry.bridge.weaving;

public aspect SocketAspect
{
    public pointcut socketCtor() : 
        call(java.net.Socket.new(..)) && 
        !within(org.mimicry..*) && 
        !within(java..*);

    Object around() : socketCtor()
    {
        return null;
    }
}