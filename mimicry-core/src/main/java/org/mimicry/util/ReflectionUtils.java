package org.mimicry.util;

import java.lang.reflect.Constructor;

public class ReflectionUtils
{

    public static boolean areMatchingBoxTypes(Class<?> one, Class<?> two)
    {
        if ((Integer.class.equals(one) && int.class.equals(two))
                || (Integer.class.equals(two) && int.class.equals(one)))
        {
            return true;
        }
        if ((Long.class.equals(one) && long.class.equals(two)) || (Long.class.equals(two) && long.class.equals(one)))
        {
            return true;
        }
        if ((Double.class.equals(one) && double.class.equals(two))
                || (Double.class.equals(two) && double.class.equals(one)))
        {
            return true;
        }
        if ((Float.class.equals(one) && float.class.equals(two))
                || (Float.class.equals(two) && float.class.equals(one)))
        {
            return true;
        }
        if ((Short.class.equals(one) && short.class.equals(two))
                || (Short.class.equals(two) && short.class.equals(one)))
        {
            return true;
        }
        if ((Byte.class.equals(one) && byte.class.equals(two)) || (Byte.class.equals(two) && byte.class.equals(one)))
        {
            return true;
        }
        if ((Boolean.class.equals(one) && boolean.class.equals(two))
                || (Boolean.class.equals(two) && boolean.class.equals(one)))
        {
            return true;
        }
        if ((Character.class.equals(one) && char.class.equals(two))
                || (Character.class.equals(two) && char.class.equals(one)))
        {
            return true;
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> Constructor<T> getPossibleConstructor(Class<T> clazz, Class[] parameterTypes)
    {
        Constructor[] ctors = clazz.getConstructors();
        for (Constructor ctor : ctors)
        {
            Class[] ctorParamTypes = ctor.getParameterTypes();

            if (ctorParamTypes.length != parameterTypes.length)
            {
                continue;
            }

            boolean found = true;
            for (int i = 0; i < ctorParamTypes.length; ++i)
            {
                if (!ctorParamTypes[i].isAssignableFrom(parameterTypes[i])
                        && !areMatchingBoxTypes(ctorParamTypes[i], parameterTypes[i]))
                {
                    found = false;
                    break;
                }
            }
            if (found)
            {
                return ctor;
            }
        }
        return null;
    }

    public static Class<?>[] getValueTypes(Object[] params)
    {
        Class<?>[] pars = new Class[params.length];
        int i = 0;
        for (Object obj : params)
        {
            pars[i++] = obj.getClass();
        }
        return pars;
    }

    public static Class<?>[] getParameterTypes(Object[] params)
    {
        Class<?>[] pars = new Class[params.length];
        int i = 0;
        for (Object obj : params)
        {
            Object[] varargs = (Object[]) obj;
            pars[i++] = varargs[0].getClass();
        }
        return pars;
    }

    public static Object[] getParameterValues(Object[] params)
    {
        Object[] args = new Object[params.length];
        int i = 0;
        for (Object obj : params)
        {
            Object[] varargs = (Object[]) obj;
            args[i] = varargs[0];
        }
        return args;
    }
}
