package com.biz.netty.test.designpattern.composite;

import java.util.Iterator;

public abstract class MenuComponent {
    protected String getName(){
        throw new UnsupportedOperationException();
    }
    protected String getDescription(){
        throw new UnsupportedOperationException();
    }
    protected int getPrice(){
        throw new UnsupportedOperationException();
    }
    protected boolean isVegetarian(){
        throw new UnsupportedOperationException();
    }
    protected void print(){
        throw new UnsupportedOperationException();
    }
    protected void add(MenuComponent menuComponent){
        throw new UnsupportedOperationException();
    }
    protected void remove(MenuComponent menuComponent){
        throw new UnsupportedOperationException();
    }
    protected MenuComponent getChild(int index){
        throw new UnsupportedOperationException();
    }

    protected Iterator<MenuComponent> getIterator(){
        return new NullIterator();
    }
}
