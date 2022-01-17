package com.github.IceFrozen.attempt.testBean;

import java.util.function.Consumer;

public class UserDaoImpl implements UserDao {
    private Consumer<String> checkListAllParameter = s -> {
    };
    private Consumer<String> checkListByIdParameter = s -> {
    };
    private Consumer<String> checkInsertParameter = s -> {
    };
    private Consumer<String> checkDeleteParameter = s -> {
    };

    @Override
    public void checkListAllParameter(Consumer<String> checkListAllParameter) {
        this.checkListAllParameter = checkListAllParameter;
    }

    @Override
    public void checkListByIdParameter(Consumer<String> checkListByIdParameter) {
        this.checkListByIdParameter = checkListByIdParameter;
    }

    @Override
    public void checkInsertParameter(Consumer<String> checkInsertParameter) {
        this.checkInsertParameter = checkInsertParameter;
    }

    @Override
    public void checkDeleteParameter(Consumer<String> checkDeleteParameter) {
        this.checkDeleteParameter = checkDeleteParameter;
    }

    @Override
    public String listAll(String s) {
        checkListAllParameter.accept(s);
        System.out.println("开始调用原始listAll方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始listAll方法");
        return "listAll的返回值";
    }

    @Override
    public String listById(String s) {
        checkListByIdParameter.accept(s);
        System.out.println("开始调用原始listById方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始listById方法");
        return "listById的返回值";
    }

    @Override
    public String insert(String s) {
        checkInsertParameter.accept(s);
        System.out.println("开始调用原始insert方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始insert方法");
        return "insert的返回值";
    }

    @Override
    public String delete(String s) {
        checkDeleteParameter.accept(s);
        System.out.println("开始调用原始delete方法");
        System.out.println("原始方法接收到的参数：" + s);
        System.out.println("结束调用原始delete方法");
        return "delete的返回值";
    }
}
