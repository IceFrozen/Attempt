package io.github.icefrozen.attempt;

import io.github.icefrozen.attempt.listeners.AttemptStrategyListener;
import io.github.icefrozen.attempt.listeners.AttemptTimeoutListener;
import io.github.icefrozen.attempt.strategy.AttemptStrategy;
import io.github.icefrozen.attempt.util.BeanUtils;

import java.lang.reflect.Constructor;

/**
 * Executor builder
 *
 * @author Jaosn Lee
 */
public class AttemptBuilderFactory {
    public static AttemptExecutor generateExecutor(BaseAttemptPropertyViewer<?> viewer) {
        AttemptExecutor attemptExecutor;
        try {
            if (viewer.context() == null) {
                viewer.context(BeanUtils.instantiateClass(viewer.getContextClass()));
            }
            if (viewer.backOff() == null) {
                viewer.backOff(BeanUtils.instantiateClass(viewer.getBackOffClass()));
            }
            if (viewer.name() == null) {
                viewer.name(viewer.strategyClass().getName() + "[" + viewer.retryMax() + "]@");
            }
            if (viewer.strategy() == null) {
                Constructor<? extends AttemptStrategy> constructor = BeanUtils.accessibleConstructor(viewer.strategyClass(), String.class, BaseAttemptPropertyViewer.class);
                viewer.strategy(BeanUtils.instantiateClass(constructor, viewer.name(), viewer));
                if (viewer.exceptionRecord().isEmpty()) {
                    viewer.registerExceptionRetryTime(Exception.class, viewer.retryMax() <= 0 ? 3 : viewer.retryMax());
                }
            }
            viewer.addListener(new AttemptStrategyListener());
            if (viewer.timeout() > 0) {
                viewer.addListener(new AttemptTimeoutListener());
            }
            Constructor<AttemptExecutor> constructor = BeanUtils.accessibleConstructor(AttemptExecutor.class, AttemptContext.class, AttemptStrategy.class);
            attemptExecutor = BeanUtils.instantiateClass(constructor, viewer.context(), viewer.strategy());
            attemptExecutor.setDefaultValue(viewer.defaultValue());
            attemptExecutor.setListeners(viewer.getListeners());
        } catch (Exception e) {
            throw new AttemptException(e.getCause(), "RetryBuilder builder exception:{}", e.getMessage());
        }
        return attemptExecutor;

    }
}
