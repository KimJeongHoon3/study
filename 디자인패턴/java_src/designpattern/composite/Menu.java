package com.biz.netty.test.designpattern.composite;

import java.util.ArrayList;
import java.util.Iterator;

public class Menu extends MenuComponent {
    String name;
    String description;
    ArrayList<MenuComponent> arrayList=new ArrayList<>();

    public Menu(String name, String description){
        this.name=name;
        this.description=description;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected String getDescription() {
        return description;
    }

    @Override
    protected void print() {
        Iterator<MenuComponent> iter=arrayList.iterator();
        System.out.println(name+" | "+description);
        System.out.println("--------------------");
        while(iter.hasNext()){
            iter.next().print();
        }
    }

    @Override
    protected void add(MenuComponent menuComponent) {
        arrayList.add(menuComponent);
    }

    @Override
    protected void remove(MenuComponent menuComponent) {
        arrayList.remove(menuComponent);
    }

    @Override
    protected MenuComponent getChild(int index) {
        return arrayList.get(index);
    }

    @Override
    protected Iterator<MenuComponent> getIterator() {
        return new CompositeIterator(arrayList.iterator());
    }
}
