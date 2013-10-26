package org.mimicry.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Collections
{
    public static <T> List<T> mergeToList(Collection<T> a, Collection<T> b)
    {
        ArrayList<T> list = new ArrayList<T>(a);
        list.addAll(b);
        return list;
    }

    public static <T> List<T> mergeToList(T[] a, T[] b)
    {
        ArrayList<T> list = new ArrayList<T>(Arrays.asList(a));
        list.addAll(Arrays.asList(b));
        return list;
    }

    public static <T> List<T> mergeToList(Collection<T> a, T[] b)
    {
        ArrayList<T> list = new ArrayList<T>(a);
        list.addAll(Arrays.asList(b));
        return list;
    }

    public static <T> List<T> mergeToList(T[] a, Collection<T> b)
    {
        ArrayList<T> list = new ArrayList<T>(Arrays.asList(a));
        list.addAll(b);
        return list;
    }
}
