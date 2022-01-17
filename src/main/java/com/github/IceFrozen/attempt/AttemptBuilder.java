package com.github.IceFrozen.attempt;

import com.github.IceFrozen.attempt.invoker.PollingAttemptInvoker;
import com.github.IceFrozen.attempt.invoker.RetryAttemptInvoker;
import com.github.IceFrozen.attempt.strategy.PollingAttemptStrategy;
import com.github.IceFrozen.attempt.strategy.RetryAttemptStrategy;

import java.util.function.Supplier;

/**
 * AttemptBuilder builds the builder of Attempt
 *
 * @author Jaosn Lee
 */
public class AttemptBuilder {

    public static class Polling<T> extends
            BaseAttemptPropertyViewer<Polling<T>> {
        private final T originInstance;
        private Attempt<T> attempt;

        public Polling(T t) {
            super.strategy(PollingAttemptStrategy.class);
            super.retryMax(-1);
            originInstance = t;
        }

        public T build() {
            if (this.endPointTry() == null) {
                throw new AttemptException("Polling must set endpoint!");
            }

            if (!PollingAttemptStrategy.class.isAssignableFrom(this.strategyClass())) {
                throw new AttemptException("strategyClass must be PollingStrategy!");
            }

            if (this.attempt == null) {
                AttemptExecutor executor = AttemptBuilderFactory
                        .generateExecutor(this);
                attempt = new Attempt<>(executor, originInstance);
            }
            return attempt.getProxyObject();
        }
    }


    /**
     * 构建Retry对象 对象
     *
     * @param <T> 返回值
     */
    public static class Retry<T> extends
            BaseAttemptPropertyViewer<Retry<T>> {
        private final T originInstance;
        private Attempt<T> attempt;

        public Retry(T t) {
            originInstance = t;
        }

        public Attempt<T> attempt() {
            if (this.attempt == null) {
                this.build();
            }
            return this.attempt;
        }

        public T build() {
            if (this.attempt == null) {
                if (!RetryAttemptStrategy.class.isAssignableFrom(this.strategyClass())) {
                    throw new AttemptException("strategyClass must be PollingStrategy!");
                }
                AttemptExecutor executor = AttemptBuilderFactory
                        .generateExecutor(this);
                attempt = new Attempt<>(executor, originInstance);
            }
            return attempt.getProxyObject();
        }
    }


    //TODO
    public static <T> RetryAttemptInvoker<T> retry(Supplier<T> supplier) {
        return new RetryAttemptInvoker<>(supplier);
    }

    public static <Void> RetryAttemptInvoker<Void> retry(Runnable runnable) {
        return new RetryAttemptInvoker<>(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> PollingAttemptInvoker<T> polling(Supplier<T> supplier) {
        return new PollingAttemptInvoker<>(supplier);
    }

    public static <T> PollingAttemptInvoker<T> polling(Runnable runnable) {
        return new PollingAttemptInvoker<>(() -> {
            runnable.run();
            return null;
        });
    }
}