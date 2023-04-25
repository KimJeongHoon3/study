effective_java_열거타입과_에너테이션

- item34_int 상수 대시 열거 타입을 사용하라
  - 열거타입?
    - 일정 개수의 상수값을 정의한 다음, 그 외의 값은 허용하지 않는 타입
    - 완전한 형태의 **클래스**!
      - enum에 선언된 상수는, 상수 하나당 자신의 인스턴스를 하나씩 만들어 `public static final` 필드로 공개하는것!
      - 외부에서 접근할 수 있는 생성자 제공x (사실상 final 클래스)
      - => 열거 타입 선언으로 만들어진 인스턴스들은 오직 딱 한개만존재!
        - 싱글턴은 원소가 하나뿐인 열거타입
          ```java
            enum Singleton {
                INSTANCE, // 원소가 하나뿐인 열거타입은 싱글턴이다.
            }
          ```
  - 열거타입의 이점
    - 컴파일 타임에 타입안정성 제공
      - 해당 열거타입이 아닌 다른 열거타입과 비교하면 바로 컴파일에러
    - 다른 enum 클래스라면 상수이름 당연 같을 수 있음
    - 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일 하지 않아도됨
      - 공개되는것은 오직 필드의 이름뿐이기에, 이름이 변경되지않는 이상은 기존 사용하던 클라이언트 이상무
    - toString 메서드는 출력하기에 적합한 문자열 보여줌
      - 정수상수는 의미없는숫자.. 하지만, enum은 상수이름을 보여준다.. 물론, toString 재정의가능
    - 클래스이기때문에 임의의 메서드나 필드(상수가 가질 데이터)를 추가할 수 있고, 임의의 인터페이스를 구현하게 할 수도 있다.
      - ex. Comparable도 구현되어있음(상수 선언 순서대로 비교.. 의미있으려나..?)
      - 참고로 Object에 정의된 메서드들을 override하도록 허용한건 toString 밖에없다..
  - 열거타입에 메서드나 필드는 언제 필요할까?
    - 상수와 연관된 데이터가 있다면, 필드를 통해서 상수 자체에 내재시키자 
      - 생성자에서 데이터 받아 인스턴스 필드에 저장하면됨
      - 열거 타입은 근본적으로 불변이라 모든 필드는 final!!
      ```java
        enum Operation {
            PLUS("+");

            private final String buho;
            Operation(String buho) {
                this.buho = buho;
            }
        }
      ```
    - 상수마다 동작이 달라져야할때는 상수별 메서드 구현(constant-specific method implementation)을 사용하라
      - switch문 NO!
        - 상수 추가시 switch문도 같이 변경을 해주어야하는데, 놓치기 쉬움..  
      - 열거 타입에 추상메서드를 선언하고, 상수별 클래스 몸체를 상수에서 자신에 맞게 재정의(추상 메서드의 구체화..)
        ```java
            enum Operation {
                PLUS("+") {
                    @Override
                    public double apply(double x, double y) { return x+y;}
                },
                MINUS("-") {
                    @Override
                    public double apply(double x, double y) { return x-y;}
                },
                // ...

                private final String symbol;
                Operation(String symbol) {
                    this.symbol = symbol;
                }

                public abstract double apply(double x, double y); // 상수별 메서드 구현활용을 위한 추상 메서드 선언

                @Override
                public String toString() { // 재정의가능. 하지만 toString을 재정의 하였다면, 이 반환하는 문자열을 기반으로 열거타입 상수로 반환해주는 메서드를 만들자
                    return symbol;
                }

                static Map<String, Operation> symbolToOperation = Stream.of(Operation.values())
                        .collect(Collectors.toMap(Operation::toString, Function.identity())); 

                public static Optional<Operation> fromString(String symbol) { // 인자로 받은 symbol이 없을수도 있으니, 센스있게 Optional로 처리하자 (Optional을 사용하여 클라이언트에게 없을수도 있음을 알리고 이를 처리하라고 알려줌)
                    return Optional.ofNullable(symbolToOperation.get(symbol));
                }
            }
        ```
    - 상수별 메서드 구현시 공용으로 쓰고싶은게 있을때에는 어떻게?
      - 되도록이면 switch문을 사용하지마라~ (갱신 제대로 못하면 오동작.. 실수 유발가능성 높다..)
        - <span style="color:red">예외적으로 switch문이 좋은선택?</span>
          - 기존 열거 타입에 상수별 동작을 혼합해 넣을때..
            - ex. 위 Operation 열거타입의 반대 연산을 반환하는 메서드..
          - 추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접만든 열거 타입이라도 이 방식을 적용하는게좋다..?
          - => 위 두개 뭔말인지 잘 이해가안간다..
      - 전략 열거 타입을 사용하자!
        ```java
            public enum PayrollDay {
                MONDAY(WEEKEND), TUESDAY(WEEKDAY), WEDNESDAY(WEEKEND);
                // ...

                private final PayType payType; // 이 부분을 함수형 인터페이스를 활용해도 나쁘지않을듯..?
                PayrollDay(PayType payType) {
                    this.payType = payType;
                }
                
                public int pay(int minutesWorked, int payRate) {
                    return payType.pay(minutesWorked, payRate);
                }

                // 전략 열거 타입
                enum PayType { // 내부적으로만 사용하니 package-private
                    WEEKDAY {
                        @Override
                        int overtimePay(int mins, int payRate) {
                            return 0;
                        }
                    }, 
                    WEEKEND {
                        @Override
                        int overtimePay(int mins, int payRate) {
                            return 0;
                        }
                    };
                    
                    abstract int overtimePay(int mins, int payRate);
                    
                    int pay(int minsWorked, int payRate) {
                        int basePay = minsWorked + payRate;
                        return basePay + overtimePay(minsWorked, payRate);
                    }
                }
            }
        ```
      
  - 열거 타입을 어떤식으로 만들까?
    - 일반 클래스처럼, 기능을 클라이언트에 노출해야 할 합당한 이유가 없다면, private이나 package-private으로 선언하자~
  - 열거 타입의 제약
    - 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는것은 상수변수만 가능!
      - 열거 타입 생성자가 실행되는 시점은 다른 정적 필드들이 초기화되기전이라, 정적 필드에 접근하는게 제한된다 (ex. 정적 필드로 hashMap을 정의해놨을때, enum 생성자에서 hashMap 접근하려하면 컴파일에러남)
        - 열거 타입의 상수는 상당히 이른 시점에 생성됨 (map 보다 더 빠르게..)
          - 참고로 map을 TEST 상수 위에 위치시키면 컴파일에러남..
        ```java
            public enum EnumCreation {
                TEST("hi");

                private final String s;

                EnumCreation(String s) {
                    System.out.println("TEST 초기화");
                    this.s = s;
                }

                static Map<String, String> map = new HashMap<>();

                static {
                    System.out.println("static block 호출");
                    System.out.println("static map 호출 : "+map);
                    System.out.println("EnumCreation.TEST 호출 : "+EnumCreation.TEST);
                }
            }
        ``` 
          - 위 소스의 바이트 코드
            - ![바이트코드 증명](2023-04-21-22-04-40.png)
      - 또한 동일한 제약으로인해 생성자에서 같은 열겨타입의 다른 상수에도 접근할 수 없음!
        - 열거타입의 상수는 public static final 이므로 즉, static인 정적필드이므로 접근제한되는게 당연
      
  - 결론
    - 컴파일 타임에 정의가능한 상수집합이면 열거타입을 사용하자!!!
      - 태양계행성, 한 주의 요일, 메뉴아이템, 연산 코드 등 고정된 값이나, 컴파일 타임에 허용하는 값 모두를 알고있는것들!
  - 기타 팁
    - 열거타입은 values라는 정적메서드를 통해서 자신안에 정의된 상수들을 배열로 반환해준다  
      - 값들은 선언된 순서대로 저장
    
---

- 아이템35_ordinal 메서드 대신 인스턴스 필드를 사용하라
  - 모든 열거 타입은 해당 상수가 그 열거 타입에서 몇번째 위치인지를 반환하는 ordinal 메서드를 제공
  - ordinal을 활용해서 뭔가를 만들지마라!!
    - 상수의 순서가 변경되면 예상치 못한 오동작이 발생할 수 있을 뿐만아니라, 기대하는 정수의 값을 만들기위해 불필요한 수고를 해야한다..
    - API 문서에서도 프로그래머가 사용할게아닌, EnumSet과 EnumMap같은 열거타입 기반의 범용자료구조에 쓸 목적으로 설계되었다고 나와있다!

---

- 아이템36_비트 필드 대신 EnumSet을 사용하라
  - 집합 개념을 활용하기 위해서는 비트 필드를 사용하기도한다
    ```java
        static class Text { // 구닥다리 기법
            public static final int STYLE_BOLD = 1 << 0;
            public static final int STYLE_ITALIC = 1 << 1;
            public static final int STYLE_UNDERLINE = 1 << 2;
            public static final int STYLE_STRIKETHROUGH = 1 << 3;
            private String str;

            public void applyStyles(int styles) { 
                if ((styles & STYLE_BOLD) != 0) { // STYLE_BOLD 면
                    applyBold();
                }

                if ((styles & STYLE_ITALIC) != 0) { // STYLE_BOLD 면
                    applyItalic();
                }

                // ...

            }

            private void applyItalic() {
                System.out.println("italic 적용");
            }

            private void applyBold() {
                System.out.println("bold 적용");
            }

        }

        public static void main(String[] args) {
            Text text = new Text();
            text.applyStyles(Text.STYLE_BOLD | Text.STYLE_ITALIC); // 이런식으로 집합개념 사용가능. 위의 applyItalic 메서드, applyBold 메서드 호출
        }
    ```
  - 하지만, EnumSet을 활용하여 집합을 더욱 효율적으로 다룰 수 있다
    - EnumSet의 내부는 비트 벡터로 되어있으니 성능상에서도 불리할게 없다
    - enum의 원소가 64개 이하면, EnumSet 전체를 long 변수하나로 표현(long은 64비트까지 표현가능하니..)하여 비트 필드에 비견되는 성능을 보여줌
      - enum의 ordinal을 활용
    - 코드로 확인
    ```java

        // RegularEnumSet 내부 (해당 enum의 상수 갯수가 64개 이하일 경우)

            // ...

            public boolean add(E e) {
                typeCheck(e);

                long oldElements = elements;
                elements |= (1L << ((Enum<?>)e).ordinal()); // enum의 ordinal을 사용하여 element와 OR 비트연산을 하여 enum의 상수를 저장한다 (long을 사용하니 64비트까지만..)
                return elements != oldElements;
            }

            // ...

            public boolean retainAll(Collection<?> c) {
                if (!(c instanceof RegularEnumSet<?> es))       // RegularEnumSet 즉, 64개 이하의 EnumSet이 아니라면~
                    return super.retainAll(c);

                if (es.elementType != elementType) {            // element는 Class<T> 인데, 위의 예에서는 Style.class로 보면됨. 즉, 다른 타입이면~
                    boolean changed = (elements != 0);
                    elements = 0;
                    return changed;
                }

                long oldElements = elements;
                elements &= es.elements;                        // AND(&) 비트 연산을 사용하여 파라미터로받은 c의 elements들과 같은것들만 남기도록.. 만약 중복된게 없으면 빈값이됨..
                return elements != oldElements;
            }
         
        /////////////////////////////

        @Test
        void testEnumSet() {
            EnumSet<EnumSetAnalysis.Style> enumSet = EnumSet.of(BOLD, ITALIC);
            System.out.println(enumSet);

            enumSet.retainAll(EnumSet.of(BOLD)); // 중복된 BOLD만 남음
            assertThat(enumSet)
                    .hasSize(1)
                    .containsOnly(BOLD);


            enumSet.retainAll(EnumSet.of(UNDERLINE, ITALIC));
            assertThat(enumSet)
                    .isEmpty(); // 중복되는게 없으니, 암것도없음
        }

    ```
  - 결론
    - 집합개념을 사용하기위해서 EnumSet이 아닌, 비트필드를 사용하는 어리석은 짓을 하지말자..