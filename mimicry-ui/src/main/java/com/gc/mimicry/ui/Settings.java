package com.gc.mimicry.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings
{
    private static final File SETTINGS_FILE = new File("settings.properties");
    public static String WORKSPACE_PATH = "workspace";
    public static String REPOSITORY_PATH = "repository";
    private static Properties properties = new Properties();

    static
    {
        setDefaults();
    }

    private Settings()
    {
    }

    public static void load()
    {
        try
        {
            properties = new Properties();
            properties.load(new FileReader(SETTINGS_FILE));
        }
        catch (IOException e)
        {
        }
        setDefaults();
    }

    public static void save() throws IOException
    {
        properties.store(new FileOutputStream(SETTINGS_FILE), "");
    }

    public static void setValue(String key, String value)
    {
        properties.setProperty(key, value);
    }

    public static String getValue(String key)
    {
        return properties.getProperty(key);
    }

    public static String getValue(String key, String defaultValue)
    {
        String value = properties.getProperty(key);
        if (value == null)
        {
            return defaultValue;
        }
        return value;
    }

    private static void setDefaults()
    {
        if (properties.getProperty(WORKSPACE_PATH) == null)
        {
            properties.setProperty(WORKSPACE_PATH, "./workspace");
        }
    }
}
