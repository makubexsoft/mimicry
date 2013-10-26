package org.mimicry.util;

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

    public static boolean exceptionWasCausedByThreadDeath(Throwable th)
    {
        if (th == null)
        {
            return false;
        }
        if (th instanceof ThreadDeath)
        {
            return true;
        }
        return exceptionWasCausedByThreadDeath(th.getCause());
    }
}
