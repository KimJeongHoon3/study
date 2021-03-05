package com.biz.netty.test.designpattern.builder;

public class BuilderTestMain {
    public static void main(String[] args) {
        TestBuilder testBuilder=new TestBuilder.Builder().a("ABC").build();
        TestBuilder testBuilder2=TestBuilder.builder().a("abc").build();
    }
}
