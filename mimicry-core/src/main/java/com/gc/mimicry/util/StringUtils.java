package com.gc.mimicry.util;

public class StringUtils
{
    public static String toFirstLower(String str)
    {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
