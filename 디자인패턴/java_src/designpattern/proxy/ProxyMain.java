package com.biz.netty.test.designpattern.proxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyMain {
    public static void main(String[] args) {
        PersonBean personBean=new PersonBeanImpl(); //REAL SUBJECT
        personBean.setInterests("농구");
        personBean.setGender("Male");
        personBean.setName("JH");

        //대리자역할!! 보호프록시!
        PersonBean personBeanWithProxy=(PersonBean)Proxy.newProxyInstance(personBean.getClass().getClassLoader(),personBean.getClass().getInterfaces(),new OwnerInvokeHandler(personBean));

        personBeanWithProxy.setName("HJ");
        personBeanWithProxy.setHotOrNotRating(10);

        System.out.println(Proxy.isProxyClass(personBean.getClass())); //proxy인지 확인
        System.out.println(Proxy.isProxyClass(personBeanWithProxy.getClass()));


        CopyOnWriteArrayList<String> copyOnWriteArrayList=new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add("abc");

    }
}
