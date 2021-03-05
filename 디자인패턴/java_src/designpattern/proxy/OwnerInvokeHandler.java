package com.biz.netty.test.designpattern.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class OwnerInvokeHandler implements InvocationHandler {
    PersonBean personBean;

    public OwnerInvokeHandler(PersonBean personBean){
        this.personBean=personBean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name=method.getName();
        try{
            if(name.startsWith("get")){
                method.invoke(personBean,args);
            }else if(name.startsWith("setName")){
                method.invoke(personBean,args);
            }else if(name.equals("setGender")){
                method.invoke(personBean,args);
            }else if(name.equals("setInterests")){
                method.invoke(personBean,args);
            }else{ //setHotOrNotRating
                throw new IllegalAccessException();
            }
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }



        return null;
    }
}
