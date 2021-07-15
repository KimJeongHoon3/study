package com.biz.netty.test.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MyMergeSortTest {
    MyMergeSort mergeSort=new MyMergeSort();

    @Test
    void mergeTest(){
//        int[] src=new int[]{5,2};
//        int[] src=new int[]{2,5,1,7};
//        int[] src=new int[]{1,2,5,7};
//        int[] src=new int[]{5,7,1,2};
//        int[] src=new int[]{2,5,8,1,7};
        int[] src=new int[]{2,7,13,23,30,1,19,21,22};
        int[] dest=new int[src.length];
        int start=0;
        int end=src.length-1;
        int mid=(start+end)/2;
        mergeSort.merge(src,start,mid,end,dest);

        for(int i:dest){
            System.out.println(i);
        }
    }

    @Test
    void mergeSortTest(){
        int[] target=new int[]{2,5,1,7,300,10,22,419,20,88,99,33};
//        int[] target=new int[]{2,5,1,7,300,10,22,419,20,88,99,33,44};
        mergeSort.sortArr(target);

        for(int i:target){
            System.out.println(i);
        }
    }

    @Test
    void mergeSortTest_짝수개(){
        int[] target=new int[]{2,5,1,7,300,10,22,419,20,88,99,33};
        int[] targetCopy= Arrays.copyOf(target,target.length);
        mergeSort.sortArr(target);

        checkMergeSort(target,targetCopy);

    }

    @Test
    void mergeSortTest_홀수개(){
        int[] target=new int[]{2,5,1,7,300,10,22,419,20,88,99,33,44};
        int[] targetCopy= Arrays.copyOf(target,target.length);
        mergeSort.sortArr(target);

        checkMergeSort(target,targetCopy);
    }

    @Test
    void mergeSortTest_이미정렬(){
        int[] target=new int[]{1,3,6,7,10,22,29};
        int[] targetCopy= Arrays.copyOf(target,target.length);
        mergeSort.sortArr(target);

        checkMergeSort(target,targetCopy);
    }

    private void checkMergeSort(int[] target, int[] targetCopy) {
        Arrays.sort(targetCopy);

        assertArrayEquals(target,targetCopy);
    }

}