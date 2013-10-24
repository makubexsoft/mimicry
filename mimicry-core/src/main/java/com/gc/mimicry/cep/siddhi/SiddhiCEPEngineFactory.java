package com.gc.mimicry.cep.siddhi;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.CEPEngineFactory;

public class SiddhiCEPEngineFactory implements CEPEngineFactory
{
    @Override
    public CEPEngine create(String sessionId)
    {
        System.setProperty("hazelcast.logging.type", "slf4j");
        return new SiddhiCEPEngine(sessionId);
    }
}
