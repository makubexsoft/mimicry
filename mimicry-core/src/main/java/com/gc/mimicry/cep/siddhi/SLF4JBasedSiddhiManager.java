package com.gc.mimicry.cep.siddhi;

import java.lang.reflect.Field;

import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.util.GlobalIndexGenerator;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class SLF4JBasedSiddhiManager extends SiddhiManager
{
    public SLF4JBasedSiddhiManager()
    {
    }

    public SLF4JBasedSiddhiManager(String sessionId)
    {
        super(new SiddhiConfiguration());

        try
        {
            Field contextField = SiddhiManager.class.getDeclaredField("siddhiContext");
            contextField.setAccessible(true);
            SiddhiContext ctx = (SiddhiContext) contextField.get(this);

            HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(sessionId);
            if (hazelcastInstance == null)
            {
                Config hazelcastConf = new Config();
                hazelcastConf.setProperty("hazelcast.logging.type", "slf4j");
                hazelcastConf.getGroupConfig().setName(sessionId);
                hazelcastConf.setInstanceName(sessionId);
                hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConf);
            }
            ctx.setHazelcastInstance(hazelcastInstance);
            ctx.setGlobalIndexGenerator(new GlobalIndexGenerator(ctx));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load Siddhi.", e);
        }
    }
}
