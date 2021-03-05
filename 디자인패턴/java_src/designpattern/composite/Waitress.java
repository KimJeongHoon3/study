package com.biz.netty.test.designpattern.composite;

import java.util.Iterator;

public class Waitress {

    private MenuComponent menuComponent;

    public Waitress(MenuComponent menuComponent){
        this.menuComponent = menuComponent;
    }

    public void printMenuAll(){
//        menuComponent.print();
        Iterator<MenuComponent> iter=menuComponent.getIterator();
        while(iter.hasNext()){
            System.out.println(iter.next().getName());
        }
    }
}
