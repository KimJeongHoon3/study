package com.biz.netty.test.designpattern.singleton;

import java.util.ArrayList;

/**
 * 위험버전 1
 * */
public class Singleton {
    private static Singleton singleton;

    private Singleton(){ }

    public static Singleton getInstance(){

        if(singleton!=null){ // 멀티스레드에서 두개 이상 생성될수있음..
            singleton=new Singleton();
        }

        return singleton;
    }

    public static void main(String[] args) {
        Singleton5 singleton5=Singleton5.getInstance();
        System.out.println(singleton5.hashCode());
        Singleton5 singleton5_1=Singleton5.getInstance();
        System.out.println(singleton5_1.hashCode());
        Singleton5 singleton5_2=Singleton5.getInstance();
        System.out.println(singleton5_2.hashCode());

        for(int i=0;i<500;i++){
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Singleton5 singleton5=Singleton5.getInstance();
                    System.out.println(singleton5.hashCode());
                }
            });
            t.setDaemon(false);
            t.start();
        }

    }
}

/**
 *
 * 가능하나 느린 버전
 * */
class Singleton2 {
    private static Singleton2 singleton2;

    private Singleton2(){ }

    public synchronized static Singleton2 getInstance(){ //문제는 없는데 100배정도 느려진다함(synchronized 때문)
        if(singleton2!=null){
            singleton2=new Singleton2();
        }

        return singleton2;
    }
}

/**
 *
 * 지연된 생성 불가.. 안써도 메모리에 올라가있어야하는게 단점
 * */
class Singleton3 {
    private volatile static Singleton3 singleton3=new Singleton3(); //volatile을 쓰는이유는 해당 객체를 가져다쓸때 캐시된 상태의 값만 가져다쓴다면 다른곳에서 변화된 값을 확인할수없기때문에 메모리에서 항상 확인하도록 volatile 씀

    private Singleton3(){ }

    public static Singleton3 getInstance(){
        return singleton3;
    }
}

/**
 * DCL(double checking locking) + volatile
 *
 * */
class Singleton4 {
    private volatile static Singleton4 singleton4; //volatile을 적용시켰기 때문에 오브젝트 생성/메인메모리에 배치까지 바로 업데이트가 되어 재배치 문제가 해소된다.

    private Singleton4(){ }

    public static Singleton4 getInstance(){
        if(singleton4==null){
            synchronized (Singleton4.class){
                if(singleton4==null){
                    singleton4=new Singleton4(); //변수에 객체를 생성하는 과정은 "1)메모리 공간 확보" "2)변수에 메모리 공간 링크" "3)해당메모리에 오브젝트생성" 의 순서로 이루어진다. 그렇기때문에 2번까지만 진행되었을때 singleton4는 null은 아니기때문에 객체를 반환해주는데, 지금 반환받는 객체는 정상이 아닌놈이다.. 이것이 상당히 로우레벨에서 에러가 발생하게되는것이다. 이를 해결하기 위해서 volatile을 사용하는것이며, volatile은 1~3번까지의 행동을 보장해준다..
                }
            }
        }
        return singleton4;
    }
}


/**
 * 가장 이상적인 싱글톤
 * * */
class Singleton5 {
    private Singleton5(){};

    private static class SingletonHolder{
        public static final Singleton5 singleton5=new Singleton5();
    }

    public static Singleton5 getInstance(){
        return SingletonHolder.singleton5;
    }
}

