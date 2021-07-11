package com.toby.spring;

public class GenericSample<T extends GenericSuper,V> {
    T t;
    V v;
    public void test1(T t){
        t.superTest1();
    }

    public void setT(T t){
        this.t=t;
    }

    public <V> void setAnotherGeneric(V v){
        //this.v=v; //메소드레벨로 선언된 제네릭 타입과 클래스 레벨로 선언된 제네릭은 다르다!
    }


}
