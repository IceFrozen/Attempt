package com.github.IceFrozen.attempt.testBean;

import java.util.function.Consumer;

public interface UserDao {
    void checkListAllParameter(Consumer<String> checkListAllParameter);

    void checkListByIdParameter(Consumer<String> checkListByIdParameter);

    void checkInsertParameter(Consumer<String> checkInsertParameter);

    void checkDeleteParameter(Consumer<String> checkDeleteParameter);

    String listAll(String s);

    String listById(String s);

    String insert(String s);

    String delete(String s);
}
