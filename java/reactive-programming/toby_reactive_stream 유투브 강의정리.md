toby_reactive_stream 유투브 강의정리

- 토비의 봄 TV 4회
  - 외부에서 쓰는거면 ? super Type (하위제한)
  - 내부에서 쓰는거면 ? extend Type (상위제한)

  - capture
    - 타입추론시 사용하는 프로세스.. 컴파일러가 타입추론을 할수 없다고 생각되면 capture 어쩌구 에러뜸 

  - 람다 사용한다해서 클래스안만드는게아님! 일종의 익명클래스가 내부적으로 생성되는것!!
    - Get the enclosing class of a Java lambda expression - Stack Overflow - https://stackoverflow.com/questions/34589435/get-the-enclosing-class-of-a-java-lambda-expression
    - What is a Java 8 Lambda Expression Compiled to? - Stack Overflow - https://stackoverflow.com/questions/21858482/what-is-a-java-8-lambda-expression-compiled-to

        ```java
        public class LambdaTest {
            @Test
            void 람다클래스_확인(){
                tempMethod(()-> System.out.println("lambda1"));
                tempMethod(()-> System.out.println("lambda2"));
                tempMethod(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("anonymous");
                    }
                });
                tempMethod(new RunnableImpl());
                tempMethod(new External());
            }

            private void tempMethod(Runnable runnable){
                runnable.run();
                Class<? extends Runnable> aClass = runnable.getClass();
                System.out.println("class name : "+aClass.getName());
                System.out.println("class hashcode : "+aClass.hashCode());
                System.out.println("canonical class name : "+aClass.getCanonicalName());
                System.out.println("enclosing class name : "+aClass.getEnclosingClass());
                System.out.println();

            }

            static class RunnableImpl implements Runnable{

                @Override
                public void run() {
                    System.out.println("static class");
                }
            }
        }

        class External implements Runnable{

            @Override
            public void run() {
                System.out.println("external class");
            }
        }


            /**
            실행결과

            lambda1
            class name : com.study.reactiveprgramming.modernjava.LambdaTest$$Lambda$275/241984962
            class hashcode : 241984962
            canonical class name : com.study.reactiveprgramming.modernjava.LambdaTest$$Lambda$275/241984962
            enclosing class name : null

            lambda2
            class name : com.study.reactiveprgramming.modernjava.LambdaTest$$Lambda$276/2041008972
            class hashcode : 2041008972
            canonical class name : com.study.reactiveprgramming.modernjava.LambdaTest$$Lambda$276/2041008972
            enclosing class name : null

            anonymous
            class name : com.study.reactiveprgramming.modernjava.LambdaTest$1
            class hashcode : 719509796
            canonical class name : null
            enclosing class name : class com.study.reactiveprgramming.modernjava.LambdaTest

            static class
            class name : com.study.reactiveprgramming.modernjava.LambdaTest$RunnableImpl
            class hashcode : 1592828874
            canonical class name : com.study.reactiveprgramming.modernjava.LambdaTest.RunnableImpl
            enclosing class name : class com.study.reactiveprgramming.modernjava.LambdaTest

            external class
            class name : com.study.reactiveprgramming.modernjava.External
            class hashcode : 205862600
            canonical class name : com.study.reactiveprgramming.modernjava.External
            enclosing class name : null
            
            참고
            canonical class : 본래의, 원형의, 근본의 클래스.. 익명클래스는 null
            enclosing class : 애워싸고 있는 클래스.. 만약 null이면 본인을 애워싸는게없는, 독립된 클래스이기때문

            */
        ```

  - 람다에서 인터섹션 타입이라하여 추가적으로 인터페이스를 명시하여 해당 ***인터페이스의 기능***을 추가해줄수있다..
    - ex. 
      - (Function & hello & hi) s -> s 
      - 위와같이 사용할수있는데, 여기서 중요한것은 위의 3가지가 합쳐졌을때 어쨋든 추상메서드가 한개여야한다는것이다.. 이 말인즉, hello나 hi는 마커인터페이스(그냥 마커용.. 아무기능안하는)나 디폴트메서드로 정의가 되어있어야함
      - 이렇게 되어있을때 hello나 hi에서 정의된 메서드를 사용할수있다! 이런식으로 람다로 넘겨줄때 "&"와 "인터페이스" 를 추가하여 원하는 기능들을 새로이 추가할수있다!!
      - 근데 받는부분에서는 추가한 인터페이스에 맞추어 또 정의를 해주어야하니.. 약간불편..
        - 이에 대한 해결책은 콜백!
    ```java
        public class IntersectionType {
            interface DelegateTo<T>{ //이 인터페이스를 통해서 범용적으로 사용할수있음!!!@#!@# 만약 이런 대리자 인터페이스를 사용하지않으면 run에서 타입이 계속 바껴야함..
                T delegate();
            }

            interface Hello extends DelegateTo<String>{
                default void hello(){
                    System.out.println("Hello "+delegate());
                }
            }

            interface UpperCase extends DelegateTo<String>{
                default void uppercase(){
                    System.out.println(delegate().toUpperCase());
                }
            }

            public static void main(String[] args) {
                run((DelegateTo<String> & Hello & UpperCase)()->"hi",d -> { //d는 앞서 선언되어있는 DelegateTo<String>, Hello, UpperCase 타입 모두를 추론할수있다.. 
                    d.hello();
                    d.uppercase();
                });
            }

            private static <T extends DelegateTo<S>,S> void run(T t, Consumer<T> consumer) {
                consumer.accept(t);
            }
        }
    ```



- 토비의 봄 TV 5회 스프링 리액티브 프로그래밍 (1) - Reactive Streams
  - 리엑티브 : 외부에 뭔가가 발생하면 거기에 대응하는 방식.. 즉, 이벤트발생하면 실행
  - Reactive Streams : reactive 프로그래밍을 하기위해서 정한 표준.. 
    - 이에 대한 구현에 조건이 있음.(정해진 스펙에 맞추어야함)
      - ex. subscriber가 onSubscribe 호출되어서 subscription으로 request를 처음보내게 되었을때, 이 request 호출을 별도의 스레드로 만들면안된다!(Subscription의 request안에서는 상관없음!) 등등의 스펙이있음
    




- 기타 이모저모
  - 제네릭에서 와일드카드(?) 를 쓰는경우는 언제?
    - 구체적으로 T와 같은 선언이 없이, 와일드카드를 쓰겠다는것은 해당 메소드내부에서는 구체적으로 타입관련한 조작이 없을것임을 의미한다! 즉, 그냥 제네릭의 구체적인 타입없이도 동작을 수행하는거라면 와일드카드로 