package com.gc.mimicry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.engine.deployment.ApplicationBundle;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;

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
        ApplicationBundle descriptor = repo.findBundle("sample-app");

        assertNotNull(descriptor);
        assertEquals("sample-app", descriptor.getName());
        assertEquals("examples.Main", descriptor.getMainClass());
    }

    @Test
    public void testLoadInvalidDescriptor()
    {
        ApplicationBundle descriptor = repo.findBundle("sample");
        assertNull(descriptor);
    }

    @Test
    public void testLoadNonExistentFile()
    {
        ApplicationBundle descriptor = repo.findBundle("does-not-exist");
        assertNull(descriptor);
    }
}
