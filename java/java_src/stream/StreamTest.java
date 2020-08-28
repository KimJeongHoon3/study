package com.biz.netty.test.stream;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamTest {
    public static void main(String[] args) {
        List<String> list= Arrays.asList("10,20,50,","200,30,100");

        int a=list.stream()
                .flatMapToInt(str -> {
                    String[] nums=str.split(",");
                    int[] intArr=new int[nums.length];
                    for(int i=0;i<nums.length;i++){
                        intArr[i]=Integer.parseInt(nums[i].trim());
                    }

                    return Arrays.stream(intArr);

                })
                .max()
                .getAsInt();

        System.out.println("result : "+a);
        System.out.println();

        /**
         *  sort
         * */
        list.stream()
                .flatMapToInt(str -> {
                    String[] nums=str.split(",");
                    int[] intArr=new int[nums.length];
                    for(int i=0;i<nums.length;i++){
                        intArr[i]=Integer.parseInt(nums[i].trim());
                    }

                    return Arrays.stream(intArr);

                })
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(System.out::println);

        System.out.println("---------------------");

        /**
         * custom reduce
         * */
        int customReduceSum1=list.stream()
                .flatMapToInt(str -> {
                    String[] nums=str.split(",");
                    int[] intArr=new int[nums.length];
                    for(int i=0;i<nums.length;i++){
                        intArr[i]=Integer.parseInt(nums[i].trim());
                    }

                    return Arrays.stream(intArr);

                }).boxed()
                .reduce((b,c)->b+c)
                .get();

        int customReduceSum2=list.stream()
                .flatMapToInt(str -> {
                    String[] nums=str.split(",");
                    int[] intArr=new int[nums.length];
                    for(int i=0;i<nums.length;i++){
                        intArr[i]=Integer.parseInt(nums[i].trim());
                    }

                    return Arrays.stream(intArr);

                }).boxed()
                .reduce(0,(b,c) -> b+c); //default 값을 0으로..

        System.out.println("customReduceSum1 : "+customReduceSum1);
        System.out.println("customReduceSum2 : "+customReduceSum2);

        System.out.println("----------------------");

        List<Student> stuList=Arrays.asList(new Student("jh",90),new Student("hm",100),new Student("hi",100),new Student("kaka",95));
        stuList.stream()
                .mapToInt(Student::getScore)
                .forEach(System.out::println);

        System.out.println("-----------------------");

        /**
         * collect
         * */
        HashSet<Student> set=stuList.stream()
                .filter(stu -> stu.getName().startsWith("h"))
                .collect(Collectors.toCollection(HashSet::new));

        set.stream()
                .forEach(s-> System.out.println(s.getName()+","+s.getScore()));

        System.out.println("-----------------------");

        /**
         * collect 사용자정의컨테이너
         * */
        stuList.stream()
                .filter(stu -> stu.getScore()>90)
                .collect(HighStudent::new,HighStudent::accumulate,HighStudent::combine)
                .getList()
                .stream()
                .forEach(s-> System.out.println(s.getName()+","+s.getScore()));


        System.out.println("--------------------------");
        /**
         * collect 요소를 그룹핑해서 수집
         *
         * */
        Map<Integer,List<String>> map=stuList.stream()
                .collect(Collectors.groupingBy(Student::getScore,Collectors.mapping(Student::getName,Collectors.toList())));
        for(Map.Entry<Integer,List<String>> entry:map.entrySet()){
            int key=entry.getKey();
            List<String> value=entry.getValue();
            for(String str:value){
                System.out.println("key : "+key +", value : "+str);
            }
        }

        System.out.println("--------------------------");

        /**
         * collect 그룹후 매핑 집계
         * */
        Map<Integer,Double> map2=stuList.stream()
                .collect(Collectors.groupingBy(Student::getScore,Collectors.averagingDouble(Student::getScore)));

        double map2Val=map2.get(90);
        double map2Val2=map2.get(100);
        System.out.println(map2Val+","+map2Val2);

       Map<Integer,String> mapByName=stuList.stream()
               .collect(Collectors.groupingBy(
                       Student::getScore,
                       Collectors.mapping(Student::getName,Collectors.joining(","))
               ));

       for(Map.Entry<Integer,String> entry:mapByName.entrySet()){
            int key=entry.getKey();
            String value=entry.getValue();
            System.out.println("key : "+key +", value : "+value);
       }

        System.out.println("------------------");

        /**
         * collect 사용자정의컨테이너(병렬처리)
         * */
        HighStudent highList=stuList.parallelStream()
                .filter(stu -> stu.getScore()>80)
                .collect(HighStudent::new,HighStudent::accumulate,HighStudent::combine);
        //accumulate, 생성자호출은 4번,, combine은 3번.. 1번 2번 combine(1번으로), 3번 4번 combine(3번으로), 1번 3번 Combine(1번으로) => total combine은 3번처리 최종적으로 1번이 combine되었음

        highList.getList().stream().forEach(s-> System.out.println(s.getName()+","+s.getScore()));
//                .getList()
//                .stream()
//                .forEach(s-> System.out.println(s.getName()+","+s.getScore()));

    }
}

class HighStudent{
    List<Student> students;

    public HighStudent() {
        students=new ArrayList<>();
        System.out.println("["+Thread.currentThread().getName()+"] HighStudent()");
    }

    public void accumulate(Student student){
        students.add(student);
        System.out.println("["+Thread.currentThread().getName()+"] accumulate()");
    }

    public void combine(HighStudent other){
        students.addAll(other.getList());
        System.out.println("["+Thread.currentThread().getName()+"] combine()");
    }

    public List<Student> getList(){
        return students;
    }
}
