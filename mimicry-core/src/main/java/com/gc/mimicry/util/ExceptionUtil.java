package com.gc.mimicry.util;

public class ExceptionUtil
{
    public static void throwUnchecked(final Throwable ex)
    {
        ExceptionUtil.<RuntimeException> throwsUnchecked(ex);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwsUnchecked(Throwable toThrow) throws T
    {
        throw (T) toThrow;
    }
}
