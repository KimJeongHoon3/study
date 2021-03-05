package com.biz.netty.test.designpattern.factory;

public abstract class PizzaStore {
    public Pizza orderPizza(int type){
        Pizza pizza=createPizza(type);
        pizza.prepare();
        pizza.bake();
        pizza.cut();
        pizza.box();
        return pizza;
    }

    /**
     * - "팩토리 메소드"
     * - 해당함수는 보통 추상클래스(현재클래스) 에서 사용됨!
     * */
    protected abstract Pizza createPizza(int type);
}
