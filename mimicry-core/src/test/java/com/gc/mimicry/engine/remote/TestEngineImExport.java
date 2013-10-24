package com.gc.mimicry.engine.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.cep.CEPEngineFactory;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngineFactory;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.local.LocalEngine;

public class TestEngineImExport
{
    private LocalEngine localEngine;
    private ImportedEngine importedEngine;

    @Before
    public void setUp() throws IOException
    {
        ApplicationRepository appRepo = new LocalApplicationRepository();
        File workspace = new File("/tmp/mimicry");
        CEPEngineFactory factory = new SiddhiCEPEngineFactory();
        localEngine = new LocalEngine(appRepo, workspace, factory);

        EngineExporter.exportEngine(localEngine);
        importedEngine = EngineImporter.importEngine(localEngine.getEngineInfo(), InetAddress.getLocalHost(), factory);
    }

    @Test
    public void testExportImportWorks()
    {
        assertNotNull(importedEngine);

        assertEquals(0, localEngine.listSessions().size());
        assertEquals(0, importedEngine.listSessions().size());
    }

    @Test
    public void testCanCreateSession()
    {
        UUID sessionId = UUID.randomUUID();
        importedEngine.createSession(sessionId, new SimulationParameters());

        assertEquals(1, localEngine.listSessions().size());
        assertEquals(sessionId, localEngine.listSessions().iterator().next());
    }
}
