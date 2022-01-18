package com.github.IceFrozen.attempt.exception;

@SuppressWarnings("all")
public class SneakyExceptionUtil {

    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return SneakyExceptionUtil.sneakyThrow0(t);
    }
    public static Throwable originExceptionUnWrapper(Throwable t) {
        while (t instanceof OriginExceptionWrapper) {
            t = ((OriginExceptionWrapper) t).getOriginException();
        }
        return t;
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw  (T) t;
    }
}
