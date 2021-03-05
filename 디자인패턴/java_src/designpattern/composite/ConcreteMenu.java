package com.biz.netty.test.designpattern.composite;

public class ConcreteMenu extends MenuComponent {

    private final String name;
    private final String description;
    private final int price;
    private boolean isVegetarian;

    public ConcreteMenu(String name, String description, int price, boolean isVegetarian){
        this.name=name;
        this.description=description;
        this.price=price;
        this.isVegetarian = isVegetarian;
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
    protected int getPrice() {
        return price;
    }

    @Override
    protected boolean isVegetarian() {
        return isVegetarian;
    }

    @Override
    protected void print() {
        System.out.println("ConcreteMenu : "+name+" | "+description+" | "+price+" | "+isVegetarian);
        System.out.println();
    }


}
