package com.gc.mimicry.engine;

import java.util.List;
import java.util.UUID;

public interface Engine
{
    public EngineInfo getEngineInfo();

    public Session createSession(UUID sessionId, SimulationParameters params);

    public List<SessionInfo> listSessions();

    public void destroySession(UUID sessionId);

    public Session findSession(UUID sessionId);

    public void deployApplication(String name, byte[] content);
}
