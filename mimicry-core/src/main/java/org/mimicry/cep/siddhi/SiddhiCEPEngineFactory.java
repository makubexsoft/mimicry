package org.mimicry.cep.siddhi;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.CEPEngineFactory;

public class SiddhiCEPEngineFactory implements CEPEngineFactory
{
    @Override
    public CEPEngine create(String sessionId)
    {
        System.setProperty("hazelcast.logging.type", "slf4j");
        return new SiddhiCEPEngine(sessionId);
    }
}
