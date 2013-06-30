package com.gc.mimicry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.deployment.LocalApplicationRepository;

public class TestLocalApplicationRepository
{

    private LocalApplicationRepository repo;

    @Before
    public void setUp() throws IOException
    {
        repo = new LocalApplicationRepository(new File("src/test/resources"));
    }

    @Test
    public void testLoadDescriptor()
    {
        ApplicationDescriptor descriptor = repo.getApplicationDescriptor("sample-app");

        assertNotNull(descriptor);
        assertEquals("sample-app", descriptor.getName());
        assertEquals("examples.Main", descriptor.getMainClass());
        assertEquals("sample-app.jar", descriptor.getRunnableJarFile());
    }

    @Test
    public void testLoadInvalidDescriptor()
    {
        ApplicationDescriptor descriptor = repo.getApplicationDescriptor("sample");
        assertNull(descriptor);
    }

    @Test
    public void testLoadNonExistentFile()
    {
        ApplicationDescriptor descriptor = repo.getApplicationDescriptor("does-not-exist");
        assertNull(descriptor);
    }
}
