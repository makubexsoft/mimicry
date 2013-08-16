package com.gc.mimicry.bridge.weaving;

public aspect SocketAspect
{
    public pointcut socketCtor() : 
        call(java.net.Socket.new(..)) && 
        !within(com.gc.mimicry..*) && 
        !within(java..*);

    Object around() : socketCtor()
    {
        return null;
    }
}