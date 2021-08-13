package com.biz.netty.test.forkjoin;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {
    long from;
    long to;

    public SumTask(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    protected Long compute() {
        System.out.println("sumtask : "+from+","+to+" | "+Thread.currentThread().getName());
        long size=to-from+1;
        if(size<5){
            return sum();
        }

        long half=(from+to)/2;
        SumTask leftSum=new SumTask(from,half);
        SumTask rightSum=new SumTask(half+1,to);

        leftSum.fork();

        return rightSum.compute()+leftSum.join();
    }

    private Long sum() {
        long sum=0L;
        for(long i=from;i<=to;i++){
            sum+=i;
        }

        return sum;
    }
}
