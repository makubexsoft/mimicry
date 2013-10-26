package org.mimicry;

import java.util.Set;
import java.util.UUID;

public interface Engine
{
    public EngineInfo getEngineInfo();

    public Session createSession(UUID sessionId, SimulationParameters params);

    public Set<UUID> listSessions();

    public void destroySession(UUID sessionId);

    public Session findSession(UUID sessionId);

    public void deployApplication(String name, byte[] content);
}
