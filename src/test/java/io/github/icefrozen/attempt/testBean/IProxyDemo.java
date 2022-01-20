package io.github.icefrozen.attempt.testBean;

public interface IProxyDemo {
    default String proxyMethod(Object ...args) {
        return "default method invoke";
    }
}
