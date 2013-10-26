package org.mimicry.bridge.weaving;

public interface ByteCodeEnhancer
{
    public byte[] enhance(String className, byte[] byteCode);
}
