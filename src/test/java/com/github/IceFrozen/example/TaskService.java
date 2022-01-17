package com.github.IceFrozen.example;

import com.github.IceFrozen.attempt.AttemptBuilder;
import com.github.IceFrozen.attempt.AttemptResult;
import com.github.IceFrozen.attempt.util.SecurityThreadWaitSleeper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskService {
    public List<Integer> history = new ArrayList<>();
    public Integer nowProgress = 0;             // process
    public Integer queryProgressCount = 0;      // queryProgress invoke time
    // 需要抛出错误的 Progress
    public List<Integer> errorThrowOrder = new ArrayList<>();



    public Integer queryProgress () {
        history.add(nowProgress);
        if(errorThrowOrder.contains(queryProgressCount)) {
            throw new RuntimeException("timeout exception:" + nowProgress);
        }
        SecurityThreadWaitSleeper.sleep(500);
        nowProgress +=10;
        queryProgressCount++;
        return nowProgress;
    }

    public static void main(String[] args) {
        TaskService taskService = new TaskService();
        // 2 3 3 count will
        taskService.errorThrowOrder = Stream.of(2, 3, 4).collect(Collectors.toList());

        // 构建重试策略
        AttemptBuilder.Polling<TaskService> taskServicePollBuilder = new AttemptBuilder.Polling<>(taskService);
        // 其他跟retry 类似的配置
        // 设置轮询停止条件
        TaskService taskServicePoll = taskServicePollBuilder.endPointTry(context -> {
            // 获取上次结果
            AttemptResult result = context.getLastResult();
            if (result.isSuccess()) {
                Integer progress = (Integer) result.getRetValue();
                return progress < 100;      //  progress < 100 poll continue
            }
            return false;
        })
                .maxPollCount(100)
                .retryMax(3)      // retry max
                .registerExceptionRetryTime(RuntimeException.class, 3)
                .build();

    }

}
