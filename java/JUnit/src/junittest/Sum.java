package com.biz.netty.test.junittest;

public class Sum implements Expression {
    public Expression augend;
    public Expression addend;

    public Sum(Expression augend, Expression addend) {
        this.augend = augend;
        this.addend = addend;
    }

    public Money reduce(Bank bank, String to) {
        int augendAmount=bank.reduce(augend,to).amount;
        int addendAmount=bank.reduce(addend,to).amount;
        return new Money(augendAmount+addendAmount,to);
    }

    @Override
    public Expression plus(Expression addend) {
        return new Sum(this,addend); //this... 까지 생각이 안난다......
    }

    @Override
    public Expression times(int multiplier) {
        return new Sum(augend.times(multiplier),addend.times(multiplier));
    }
}
