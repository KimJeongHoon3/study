package com.biz.netty.test.junittest;


import java.util.HashMap;

public class Bank {
    HashMap<Pair,Integer> rates=new HashMap<>();
    public Money reduce(Expression source, String to) {
        return source.reduce(this,to); //expression이 Sum 타입이랑 Money 타입이랑 동작하는게 다름..

//        return Money.dollar(10); //처음에 만들때는 "가짜"로 만드는것이 필요
    }

    public int rate(String currency, String to) {
        if(currency.equals(to)) return 1;
        Integer rate=rates.get(new Pair(currency,to));
        return rate;
    }

    public void addRate(String from, String to, int rate) {
        rates.put(new Pair(from,to),rate);
    }
}
