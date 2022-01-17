package com.github.IceFrozen.attempt.invoker;

import com.github.IceFrozen.attempt.*;
import com.github.IceFrozen.attempt.listeners.InvokeListener;
import com.github.IceFrozen.attempt.proxy.TargetMethod;

import java.util.function.Supplier;

public class AttemptInvoker<T> extends BaseAttemptPropertyViewer<AttemptInvoker<T>> implements Invoker<T>, InvokeListener {
    private Supplier<T> action;
    Attempt<Supplier<T>> attempt;
    // 结果缓存器
    AttemptResultContainer container;

    public AttemptInvoker(Supplier<T> action) {
        this.action = action;
        super.retryMax(1);
        container = new AttemptResultContainer(super.retryMax() == -1 ? 1000 : super.retryMax());
    }

    public T exec() {
        try {
            Attempt<Supplier<T>> attempt = this.getAttempt();
            return attempt.getProxyObject().get();
        } catch (Exception e) {
            if (this.defaultValue() != null) {
                return (T) this.defaultValue().getRetValue();
            }
            throw e;
        }
    }

    public T exec(T defaultValue) {
        this.defaultValue(defaultValue);
        return exec();
    }

    public Attempt<Supplier<T>> getAttempt() {
        if (this.attempt == null) {
            this.addListener(this);
            AttemptExecutor executor = AttemptBuilderFactory.generateExecutor(this);
            attempt = new Attempt<>(executor, action);
        }
        return this.attempt;
    }

    @Override
    public void invokeAfterMethod(AttemptExecutor executor, AttemptContext context, TargetMethod method, AttemptResult record) {
        this.container.record(record);
    }

    public AttemptResult getResult() {
        this.exec();
        return this.container.getResult();
    }

    public AttemptResultContainer getContainers() {
        return this.container;
    }
}
