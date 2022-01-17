## Introduction

**Attempt** is a lightweight component provides declarative retry support for applications,  not only but a polling strategy.  With Attempt, you can easily poll for something with retry functionality. Non-spring and lightweight applications are friendly for a fewer dependencies.




## Quick Start
This section provides a quick introduction to getting started with Attempt. It includes a static method call example, and an object call example.

###  An Object Call example

+ First, define the base class
```java
public class User {
    private int id;
    private String name;
    private Integer age;
    // the set get method and constructor are ignored
}

public class UserService {
    public User queryUser (int id) {
        return new User(id, "test" + id, 1);
    }
}
```


+ Second, build Attempt

```java
UserService userService = new UserService();
// Building a Retry Policy
AttemptBuilder.Retry<UserService> userRetry = new AttemptBuilder.Retry<UserService>(userService);
// Generate the retry proxy Object
UserService userServiceAttempt = userRetry.build();
// invoke the method and get the result
User user = userServiceAttempt.queryUser(1);
```
Since there are no exceptions in queryUser, this code returns immediately, no different from a direct call.

+ Retry if an exception occurs.

Now we throw an RuntimeException in the queryUser method and call it again.
```java
public class UserService {
    public int count = 0;
    public User queryUser (int id) {
        count ++;
        throw new RuntimeException("queryUser error");
    }

    public static void main(String[] args) {
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
}
```
As we see, when we execute the userServiceAttempt's queryUser method, the userServiceAttempt will be automatically retried three times. After that, the exception has been thrown. 
AttemptBuilder can make the method in a proxy object to retries when the exception has been thrown automatically? How do we assign retry to static methods?

## a static method call example

```java
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

    public static void main(String[] args) {
        UserService userService = new UserService();
        try {
            AttemptBuilder.retry(() -> UserService.queryUserStatic(1)).exec(); 
        } catch (RuntimeException e) {
           // ... staticCount > 3 then throw exception
        }
    }
}

```


## Backoff Policies

When retrying after a transient failure, it often helps to wait a bit before trying again, because (usually) the failure is caused by some problem that can be resolved only by waiting. If a RetryCallback fails, the RetryTemplate can pause execution according to the BackoffPolicy. The following listing shows the definition of the BackoffPolicy interface:




