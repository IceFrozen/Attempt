/*
 * MIT License
 *
 * Copyright (c) 2022 Jason Lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.icefrozen.attempt.testBean;

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
