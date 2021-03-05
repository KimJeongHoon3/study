package com.biz.netty.test.designpattern.builder;

public class TestBuilder {

    private String a;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private String a;

        public Builder(){
            System.out.println("builder실행");
        }

        public Builder a(String a){
            this.a=a;
            return this;
        }

        public TestBuilder build(){
            TestBuilder t=new TestBuilder();
            t.a=a;
            return t;
        }
    }
}

