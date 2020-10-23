package com.biz.netty.test.mutex;

import java.util.concurrent.locks.ReentrantLock;

public class MutexTest {
    ReentrantLock reentrantLock=new ReentrantLock(true);
    int num=0;

    public static void main(String[] args) throws InterruptedException {
        MutexTest mutexTest=new MutexTest();
        for(int i=0;i<50000;i++){
            new Thread(mutexTest.new PlusThread()).start();
            new Thread(mutexTest.new MinusThread()).start();
        }
        Thread.sleep(10_000);

        System.out.println("num : "+mutexTest.num);
    }

    class PlusThread implements Runnable{

        @Override
        public void run() {
            reentrantLock.lock();
            num++;
            reentrantLock.unlock();
            System.out.println("num ++ : "+num);
        }
    }

    class MinusThread implements Runnable{

        @Override
        public void run() {
            reentrantLock.lock();
            num--;
            reentrantLock.unlock();
            System.out.println("num -- : "+num);
        }
    }
}
