package com.github.IceFrozen.example;

import io.github.icefrozen.attempt.AttemptBuilder;

public class UserService {
    public int count = 0;
    public static int staticCount = 0;
    public User queryUser (int id) {
        count ++;
        throw new RuntimeException("queryUser error");
    }

    public static User queryUserStatic (int id) {
        staticCount ++;
        throw new RuntimeException("queryUser error");
    }

    public static void example1(String[] args) {
        UserService userService = new UserService();
        AttemptBuilder.Retry<UserService> userRetry = new AttemptBuilder.Retry<UserService>(userService);
        UserService userServiceAttempt = userRetry.build();
        try {
            User user = userServiceAttempt.queryUser(1);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());     // queryUser error
            // original object here
            System.out.println(userService.count);      // The original method has been called three times
        }
    }

    public static void example2(String[] args) {
        UserService userService = new UserService();
        try {
            AttemptBuilder.retry(() -> UserService.queryUserStatic(1)).exec();  //  count = 3
        } catch (RuntimeException e) {
            // ... count > 3 then throw exception
        }
    }
}
