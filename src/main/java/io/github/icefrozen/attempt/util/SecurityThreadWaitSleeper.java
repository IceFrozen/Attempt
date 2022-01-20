package io.github.icefrozen.attempt.util;

/**
 * 安全睡眠工具类
 *
 * @author Jason Lee
 */
public class SecurityThreadWaitSleeper {

    public static void  sleep(long backOffPeriod) {
        try {
            Thread.sleep(backOffPeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
