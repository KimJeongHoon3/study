package com.biz.netty.test.junittest;

public class Money implements Expression{
    public int amount;
    private String currency;

    public Money(int amount, String currency) {
        this.amount=amount;
        this.currency=currency;
    }

    public static Money dollar(int amount){
        return new Money(amount,"USD");
    }

    public static Money franc(int amount){
        return new Money(amount,"CHF");
    }

    @Override
    public boolean equals(Object obj) {
        Money money=(Money) obj;
        return this.amount==money.amount && money.currency().equals(this.currency());
    }


    public Expression times(int multiplier){    //factory method pattern + value object pattern -> 오버라이드가 필요한 메소드..
        return new Money(amount*multiplier,currency);
    }

    public String currency() {
        return currency;
    }

    public Expression plus(Expression addend) {
        return new Sum(this,addend);
    }

    @Override
    public Money reduce(Bank bank, String to) {
        //여기서 환율을 계산하는 끔찍한 짓을 하지말것.. 그래서 bank 변수를 받을 필요가있음!!
        int rate=bank.rate(currency,to);
        return new Money(amount/rate,to);
    }
}
