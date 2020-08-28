package com.biz.netty.test.ramda;

import java.util.function.ToIntBiFunction;

public class ArgumentMethodReferencesExample {
    public static void main(String[] args) {
        ToIntBiFunction<String,String> function; //두개 매개변수(string,string> => return 값 int로

        function=(a,b) -> a.compareToIgnoreCase(b);

        System.out.println(function.applyAsInt("기","가"));

        function=String::compareToIgnoreCase;

        System.out.println(function.applyAsInt("가","가"));

    }
}
