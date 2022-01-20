package io.github.icefrozen.attempt.testBean;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ProxyDemoImpl implements IProxyDemo {

    private int field1;
    private int field2;
    private String field3;
    private List<Integer> field4;
    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public List<Integer> getField4() {
        return field4;
    }

    public void setField4(List<Integer> field4) {
        this.field4 = field4;
    }

    @Override
    public String proxyMethod(Object... args) {
        String retVal = "ProxyDemoImpl ... args:" + StringUtils.join(args);
        System.out.println(retVal);
        return retVal;
    }


}
