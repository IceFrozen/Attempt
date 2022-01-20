package com.github.IceFrozen.example;

import io.github.icefrozen.attempt.AttemptBuilder;
import io.github.icefrozen.attempt.AttemptResult;
import io.github.icefrozen.attempt.util.SecurityThreadWaitSleeper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskService {
    public List<Integer> history = new ArrayList<>();
    public Integer nowProgress = 0;             // process
    public Integer queryProgressStep = 0;      // queryProgress invoke time
    // 需要抛出错误的 Progress
    public List<Integer> errorThrowOrder = new ArrayList<>();

    public Integer queryProgress () {
        history.add(nowProgress);
        queryProgressStep++;
        if(errorThrowOrder.contains(queryProgressStep)) {
            throw new RuntimeException("timeout exception:" + nowProgress);
        }
        SecurityThreadWaitSleeper.sleep(500);
        nowProgress +=10;

        return nowProgress;
    }

    public static void example1(String[] args) {
        Integer retryCount = 3;
        TaskService taskService = new TaskService();
        // 2 3 3 count will throw RuntimeException
        taskService.errorThrowOrder = Stream.of(2, 3, 4).collect(Collectors.toList());
        // poll builder
        AttemptBuilder.Polling<TaskService> taskServicePollBuilder = new AttemptBuilder.Polling<>(taskService);
        // set end point
        TaskService taskServicePoll = taskServicePollBuilder.endPoint(context -> {
            // get last result
            AttemptResult result = context.getLastResult();
            if (result.isSuccess()) {
                Integer progress = result.getRetValue(Integer.class);
                return progress == 100;      //  progress < 100 poll continue
            }
            return false;
        })
         .maxPollCount(100)      // max poll times
         .registerExceptionRetryTime(RuntimeException.class, retryCount)
         .build();

        try {
            Integer integer = taskServicePoll.queryProgress();
        }catch (RuntimeException e) {
            System.out.println(taskService.queryProgressStep);//
            System.out.println(taskService.history);//
        }
    }

    public static void main(String[] args) {
        Integer retryCount = 3;
        TaskService taskService = new TaskService();
        // 2 3 3 count will throw RuntimeException
        taskService.errorThrowOrder = Stream.of(2, 3, 5, 6, 7).collect(Collectors.toList());
        // poll builder
        AttemptBuilder.Polling<TaskService> taskServicePollBuilder = new AttemptBuilder.Polling<>(taskService);
        // set end point
        TaskService taskServicePoll = taskServicePollBuilder.endPoint(context -> {
            // get last result
            AttemptResult result = context.getLastResult();
            if (result.isSuccess()) {
                Integer progress = result.getRetValue(Integer.class);
                return progress == 100;      //  progress < 100 poll continue
            }
            return false;
        })
                .maxPollCount(100)      // max poll times
                .registerExceptionRetryTime(RuntimeException.class, retryCount)
                .build();

        try {
            Integer integer = taskServicePoll.queryProgress();
        }catch (RuntimeException e) {
            System.out.println("queryProgressStep:" + taskService.queryProgressStep);//
            System.out.println("history:" + taskService.history);//
        }
    }

}
