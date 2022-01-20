package io.github.icefrozen.attempt.proxy;

/**
 * 返回值拦截器
 */
public interface ReturnValueInterceptor {
    /**
     * 拦截
     *
     * @param returnValue 原始返回值
     * @return 增强后的返回值
     */
    Object intercept(Object returnValue);
}
