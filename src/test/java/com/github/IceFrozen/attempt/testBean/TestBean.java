package com.github.IceFrozen.attempt.testBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TestBean {
    public String name;
    public Date date;
    public LocalDateTime localDateTime;
    public int age;
    public BigDecimal money;
    public boolean man;
    public ArrayList<String> trait;
    public HashMap<String, String> cards;

    public static TestBean newPerson() {
        TestBean person = new TestBean();
        person.name = "张三";
        person.date = new Date();
        person.localDateTime = LocalDateTime.now();
        person.age = 100;
        person.money = BigDecimal.valueOf(500.21);
        person.man = true;
        person.trait = new ArrayList<>();
        person.trait.add("淡然");
        person.trait.add("温和");
        person.cards = new HashMap<>();
        person.cards.put("身份证", "4a6d456as");
        person.cards.put("建行卡", "649874545");
        return person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public boolean isMan() {
        return man;
    }

    public void setMan(boolean man) {
        this.man = man;
    }

    public ArrayList<String> getTrait() {
        return trait;
    }

    public void setTrait(ArrayList<String> trait) {
        this.trait = trait;
    }

    public HashMap<String, String> getCards() {
        return cards;
    }

    public void setCards(HashMap<String, String> cards) {
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", localDateTime=" + localDateTime +
                ", age=" + age +
                ", money=" + money +
                ", man=" + man +
                ", trait=" + trait +
                ", cards=" + cards +
                '}';
    }
}
