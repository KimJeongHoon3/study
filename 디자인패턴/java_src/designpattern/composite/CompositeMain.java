package com.biz.netty.test.designpattern.composite;

public class CompositeMain {
    public static void main(String[] args) {
        MenuComponent root=new Menu("allMenu","allMenu");
        MenuComponent dinner=new Menu("DINNER RESTAURANT","DINNER");
        MenuComponent cafe=new Menu("CAFE","CAFE");
        MenuComponent lunch=new Menu("LUNCH RESTAURANT","LUNCH");
        root.add(dinner);
        root.add(cafe);
        root.add(lunch);

        dinner.add(new ConcreteMenu("meat","meat",1000,false));
        dinner.add(new ConcreteMenu("soy","soy",12000,true));
        cafe.add(new ConcreteMenu("milktea","meat",1000,false));
        cafe.add(new ConcreteMenu("americano","meat",1000,false));
        cafe.add(new ConcreteMenu("latte","meat",1000,false));
        MenuComponent dessert=new Menu("dessert","dessert");
        dessert.add(new ConcreteMenu("cake","cake",100,false));
        dessert.add(new ConcreteMenu("carrot","carrot",100,true));
        cafe.add(dessert);
        lunch.add(new ConcreteMenu("볶음밥","볶음밥",1000,false));


        Waitress waitress=new Waitress(root);
        waitress.printMenuAll();


    }
}
