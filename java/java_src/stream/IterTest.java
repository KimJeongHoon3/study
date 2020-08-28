package com.biz.netty.test.stream;

import com.biz.netty.test.ramda.RamdaTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class IterTest {
    public static void main(String[] args) {
        List<String> list= Arrays.asList("가","나","다");

        Stream<String> stringStream=list.stream();
        stringStream.forEach(System.out::println);

        List<Student> studentList=Arrays.asList(new Student("김",100),new Student("이",80));
        double avg=studentList.stream()
                .mapToInt(Student::getScore)
                .average()
                .getAsDouble();
        System.out.println(avg);


    }
}


class Student{
    String name;
    int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}