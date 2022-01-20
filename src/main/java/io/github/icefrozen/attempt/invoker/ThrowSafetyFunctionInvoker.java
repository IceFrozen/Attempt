package io.github.icefrozen.attempt.invoker;

public interface ThrowSafetyFunctionInvoker<T> {

     static <T> T invoke(ThrowSafetyFunctionInvoker<T> invoker) throws Exception {
        if(invoker instanceof ThrowSafetyFunctionInvokerRunner) {
             ((ThrowSafetyFunctionInvokerRunner<Void>) invoker).run();
        } else if(invoker instanceof ThrowSafetyFunctionInvokerSupplier) {
            return ((ThrowSafetyFunctionInvokerSupplier<T>) invoker).get();
        }
        return null;
    }
}
