package io.github.icefrozen.attempt.testBean;

public class IDemo2 {

    private int a = 10;

    private int b = 10;

    public static int c = 0;

    public IDemo2(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public IDemo2() {
    }

    static int getstatic() {
        c++;
        return c;
    }

    static int getC() {
        c++;
        System.out.println(IDemo2.c);
        return c;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
