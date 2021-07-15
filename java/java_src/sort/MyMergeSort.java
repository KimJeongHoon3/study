package com.biz.netty.test.sort;

public class MyMergeSort {
    public void sortArr(int[] src){
        doDivideAndMerge(src,0,src.length-1,new int[src.length]);
    }

    public void merge(int[] src, int start, int mid, int end, int[] temp) {
        int leftPointer=start;
        int leftCheckPoint=mid;
        int rightCheckPoint=end;
        int rightPointer=mid+1;
        int idx=start;

        while(rightPointer<=rightCheckPoint && leftPointer<=leftCheckPoint){
            if(src[leftPointer]>src[rightPointer]){
                temp[idx]=src[rightPointer];
                rightPointer++;
            }else{
                temp[idx]=src[leftPointer];
                leftPointer++;
            }
            idx++;
        }

        int s=0;
        int e=0;
        if(rightPointer>rightCheckPoint){
            s=leftPointer;
            e=leftCheckPoint;
        }else if(leftPointer>leftCheckPoint){
            s=rightPointer;
            e=rightCheckPoint;
        }

        for(int i=s;i<=e;i++){
            temp[idx]=src[i];
            idx++;
        }

        for(int i=start;i<=end;i++){
            src[i]=temp[i];
        }
    }

    /**
     * // TODO: 2021/07/14 bottom up 방식으로 개발해볼것..
     * */
    private void doDivideAndMerge(int[] src, int start, int end, int[] temp) {

        int mid=(start+end)/2;

        if(start==end){
            return;
        }

        doDivideAndMerge(src,start,mid,temp);
        doDivideAndMerge(src,mid+1,end,temp);

        merge(src,start,mid,end,temp);
    }

}
