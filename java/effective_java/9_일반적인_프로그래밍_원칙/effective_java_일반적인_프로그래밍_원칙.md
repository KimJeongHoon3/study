effective_java_일반적인_프로그래밍_원칙

- 아이템57_지역변수의 범위를 최소화하라
  - 지역변수의 유효범위를 최소로 줄이면, 코드 가독성과 유지보수성이 높아지고 오류가능성은 낮아진다!
  - 어떻게 지역변수의 범위를 줄일 수 있을까?
    - 가장 처음 쓰일때 선언하기!
      - 굳이 미리 선언해서 언제써야할지를 까먹게되는.. 즉, 코드가독성을 잃어버리지말자..
      - 또한, 실제로 사용하는곳의 블록 바깥에 앞서서 선언하게되면, 실수로 블록이 끝난 이후에 변수를 사용해서 망할수도..
      - 그리고 선언과 동시에 초기화하자
        - 초기화에 필요한 정보가 충분하지않다면 선언을 미뤄야한다
        - 하지만, try-catch는 예외~
          - 변수를 초기화하는 표현식에서 검사 예외를 던질 가능성이 있다면 try 블록 안에서 초기화
            - 그리고 이후에 try블록 나와서 계속해서 작업수행하면되겠지..
          - 변수 값을 try블록 바깥에서 사용해야한다면, try 블록 앞에서 선언해야한다..
    - 메서드를 작게 유지하고, 한가지 기능에 집중하도록 하는것!
      - 한 메서드에 여러기능이 있다면, 기능마다 필요한 지역변수가 혼재할 수 밖에 없으니.. 쪼개자!!
  - 반복문(전통적인for, for-each)은 변수 범위를 최소화해줄 수 있음
    - 반복 변수가 루프 안에서만 사용되고 쓸일이 없다면, for문을써라! (while쓸 이유가없음..)
    - 기본적인 컬렉션 순회 관용구
      ```java
        for (Element e : collection) {

        }
      ```
    - 반복자가 필요할때의 관용구 (ex. 반복자의 remove 써야할경우)
      ```java
        for(Iterator<Element> i = collection.iterator(); i.hasNext(); ) {
            i.remove();
        }

        ////////////////////////////////
        // 이걸 while문으로 쓰게되면
        Iterator<Element> iterator = collection.iterator();
        while(iterator.hasNext()) {
            doSomething(iterator.next()); // 사실 iterator는 여기 block외에서 쓸데가 없음..
        }

        Iterator<Element> iterator2 = collection2.iterator();
        while(iterator.hasNext()) { // 범위를 최소화하지않으니 이런 실수를 유발하여.. iterator2의 원소를 못가져오게됨.. (물론 컴파일 에러도 안나니깐 더 큰 문제..)
            doSomething(iterator2.next());
        }

      ```
    - for의 한계값을 별도로 구해야하고, 비용이 많이 든다면 아래와 같은 관용구 사용가능
      ```java   
        for (int i = 0, n = expensiveComputation(); i < n; i++) { // 이렇게 사용하면 n은 i의 한계값이기때문에 블럭 밖에서 쓸 필요도 없고, 그렇다고 계속 블록 안에서 n을 계산해서 확인할 필요도 없다 
            // ...
        }
      ```

---

- 아이템58_전통적인 for문보다는 for-each문을 사용하라
  - for-each문의 정식이름은 "향상된 for문(enhanced for statement)"
  - 하나의 관용구로 컬렉션(**정확하게는 Iterable인터페이스를 구현한 구현체**)과 배열 모두 처리
    - Iterable 구현체들은 컴파일러가 while문과 Iterator를 호출하여 만들어줌
    - 배열은 컴파일러가 전통적인 for문으로 만들어줌
    - 증명한 코드는 "아이템47" 참고 
    - => for-each문이 만들어내는 코드가 결국 사람이 손으로 최적화한것과 같기때문에 성능에 차이가 없다..
  - for-each를 사용하기 어려운 상황
    - 파괴적인 필터링(destructive filtering)
      - 그냥.. 루프돌면서 원소 지워야하면 이거 못쓴다
        - 반복자의 remove를 호출해야함..
        - 자바 8부터는 컬렉션에 removeIf메서드 쓰는게 좋음
          - removeIf 내부에 알아서 루프돌아서 제거해줌
          ```java
            // Collection 내부
            default boolean removeIf(Predicate<? super E> filter) {
                Objects.requireNonNull(filter);
                boolean removed = false;
                final Iterator<E> each = iterator();
                while (each.hasNext()) {
                    if (filter.test(each.next())) {
                        each.remove();
                        removed = true;
                    }
                }
                return removed;
            }
          ```
    - 변형(transforming)
      - 리스트나 배열을 순회하면서 해당 자리에 그 원소의 값 일부 혹은 전체를 교체해야한다면, 반복자나 배열의 인덱스를 사용해야한다.
        - 물론, 불변객체가 아니라면, 루프돌면서 해당 원소 내부의 값을 변경할수는 있겠지만.. 불변객체인 상태에서 해당 원소를 변경해야한다면 인덱스에 맞추어 다시 할당해주어야하니깐..
    - 병렬반복(parallel iteration)
      - 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해서 엄격하고 명시적으로 제어해야한다
        - 보통 중첩된 for문을 쓸때 안쪽에 있는 루프를 다 돌면 다시 바깥 루프의 다음 원소로 가게되는 일련의 순차적인 과정이 있는데, 바깥루프와 안쪽루프를 순차적인 과정으로 도는게아닌 왔다갔다해야하는 즉, 병렬로 처리해야하는 경우가 있을때를 이야기하는듯함.. 
  - 원소들의 묶음을 표현하는 타입을 작성해야한다면 Iterable 구현을 고민해보자~ 그래서 이를 사용하는 사용자가 for-each를 사용할수있도록 해주자~

---

- 아이템59_라이브러리를 익히고 사용하라
  - 표준라이브러리 사용이점
    - 코드를 작성한 전문가의 지식과 나보다 앞서 사용한 다른 프로그래머들의 경험활용 가능
    - 일과 크게 관련 없는 문제를 해결하느라 시간허비않아도됨
      - 수학적인거라든지, 복잡한 알고리즘 만드는걸로 시간을 쓰기보다 애플리케이션 기능개발에 초점 가능
    - 따로 노력하지 않아도 성능이 지속해서 개선됨
      - 계속해서 벤치마크하여 개선..
    - 개발자 커뮤니티에서 이야기 나온것들을 논의하여 필요한 기능들이 계속 추가됨
    - 이를 활용하면 당연히 많은 사람이 쓰고있기때문에, 낯익은 코드가 되어 유지보수나 읽기 쉬운 코드가 됨
  - 필요한 기능이 있다면 
    - 일단 표준 라이브러리에 해당 기능이 있는지 보고
    - 없으면 좋은 서드파티 라이브러리가 있는지 보고
    - 이 또한 없으면 개발하라
  - 기타 팁
    - 제공해주는 기능이 있는지 몰라서 직접 개발하는 경우가 있기에, 메이저 릴리즈마다 주목할만한 기능들을 확인해보자!
      - [java10 릴리즈노트의 신규기능](https://www.oracle.com/java/technologies/javase/10-relnote-issues.html#NewFeature)
      - java.lang, java.util, java.io 는 잘 숙지하자!

---

- 아이템60_정확한 답이 필요하다면 float과 double은 피하라
  - float과 double은 부동소수점 연산에 쓰이며, 넓은 범위의 수를 빠르게 정밀한 **근사치**로 계산하도록 설계되었음
  - 따라서 정확한 결과가 필요할때는 사용하면안된다!
    - 금융 관련 계산과 맞지않음!!
      - 금융 계산에서는 BigDecimal, int나 long을 사용해한다
        - BigDecimal은 기본타입보다 쓰기가 훨씬 불편(메서드를 사용해서 더하고 빼고를 해야함..)하고, 느리다(내부적으로 long을 사용하여 더하고 빼고는 하지만, 하나의 컨테이너이기에 값을 가져오는것도 그렇고, 계산결과를 위해 새로운 불변 객체(BigDecimal)을 만든다..)
          - 하지만, 8가지 반올림 모드를 제공해주기때문에, 반올림을 완벽히 제어가능하다.. 법으로 정해진 반올림을 수행해야하는 경우라면 개꿀~
        - 결국, 빨라야하거나 계산편의를 위해서 기본자료형을 사용해야한다면, int나 long으로..
          - 또한, 숫자가 너무 크지 않아야.. int를 사용하면 아홉자리 십진수까지.. long은 열여덟자리 십진수.. 이거 넘어가면 BigDecimal로 가야지..
            - 참고로.. int는 4바이트로 음수포함일때 양수는 최대 2^31 - 1 의 값인데, 대략 2^10이 십진수 3자리(1024) 이므로 대략 9자리정도로 볼 수 있다~
        
    
  - float/double 관련 테스트 코드 (아이템10에서 테스트한 코드)
    ```java
      @Test
      void test() {
          assertFalse(1.1 + 0.1 == 1.2); // 소수점 기본타입은 double
          assertTrue(1.1f + 0.1f == 1.2f);
          assertFalse(1.1f + 0.2f == 1.3f); // float 끼리 더한다고 항상 같지않음 (가수부 범위 넘어가면 반올림이 있기때문에 무한소수일경우는 기대했던 계산 결과가 나오지않을수있다)
          assertFalse(1.1f + 0.1f == 1.2d);

          assertTrue(1.25f == 1.25d); // 가수부가 길지않기에 (23bit보다 적기에) 문제발생안함
          assertFalse((float)0.1 == 0.1);
          assertFalse(Double.compare(0.1f, 0.1) == 0);

          System.out.printf("1.1f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(1.1f))); // 가수부분 저장시 잘려나가는 데이터가있는 경우 반올림해서 저장(마지막이 1100으로 끝나야할것 같은데 1101로 저장되는이유)
          System.out.printf("0.1f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(0.1f)));
          System.out.printf("1.2f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(1.2f))); // float 끼리 더하면 항상 맞으려나..?
          System.out.printf("1.2d 의 이진수: %64s%n",Long.toBinaryString(Double.doubleToLongBits(1.2d)));
          System.out.printf("(double)1.2f 의 이진수: %64s%n",Long.toBinaryString(Double.doubleToLongBits(1.2f))); // 가수부는 그냥 double 크기에 맞추어 0 더 붙인거일뿐.. 그렇기에 1.2d와 같지가 않다..

          System.out.println();

          System.out.printf("1.1f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(1.1f)));
          System.out.printf("0.2f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(0.2f)));
          System.out.printf("1.3f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(1.3f)));

          System.out.println();

          System.out.printf("1.25f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(1.25f)));
          System.out.printf("0.1f 의 이진수: %32s%n",Integer.toBinaryString(Float.floatToIntBits(0.1f)));
          System.out.printf("0.1d 의 이진수: %64s%n",Long.toBinaryString(Double.doubleToLongBits(0.1d)));
              // 0.1d 의 이진수
              // 0    01111111011   1001100110011001100110011001100110011001100110011010
              // 부호  지수부          가수부 (무한소수일 경우에는 double이나 float 같은 경우 가수부 범위가 넘어서면 잘리기때문에 오차가 발생)

      }
    ```

---

- 아이템61_박싱된 기본 타입보다는 기본 타입을 사용하라
  - 기본타입에 대응 되는 참조타입이 하나씩 있는데, 이를 **박싱된 기본 타입** 이라한다
    - ex. int -> Intger
  - 자바는 기본적으로 오토박싱과 오토언박싱으로 두 타입을 구분하지않고 사용할 수 있는데, 기본타입과 참조타입간에는 차이점이 있기에 주의해야한다
    - 기본타입 vs 박싱된 기본타입 (참조타입)
      - 기본 타입은 값만 가지고 있은, 박싱된 기본 타입은 값과 식별성(identity)을 추가로 갖는다
        - 즉, 박싱된 기본타입의 두 인스턴스는 식별성이 있기에 값이 같아도 다르게 식별할 수 있다
        - 그래서 박싱된 기본타입에 == 연산자를 사용하면 오류가 일어나게 된다
          ```java 
            Comparator<Integer> integerComparator = (i, j) -> i < j ? -1 : (i == j ? 0 : 1);
            System.out.println(integerComparator.compare(new Integer(10), new Integer(10))); // 1반환.. i < j 비교시엔 오토언박싱을 통해서 예상된   값을 반환하나, i == j 비교시에 "객체 참조의 식별성"을 보게되므로.. 두 인스턴스가 비록 값은 같을지라도 엄연히 다르니.. 0이 아닌 1을 반환한다..
            System.out.println(integerComparator.compare(Integer.valueOf(10), Integer.valueOf(10))); // 0 반환. -128~127 까지는 캐싱되어있기때문에 동일한 인스턴스가 동일하다

            // 비교자를 써야한다면 만들어져있는걸 사용하자..
            Comparator<Integer> tComparator = Comparator.naturalOrder();
            int compare = tComparator.compare(new Integer(10), new Integer(10));
            System.out.println(compare); // 0 반환. Integer에 구현되어있는 compareTo를 사용하게되어 문제없이 비교됨
          ```
      - 기본 타입의 값은 언제나 유효하나, 박싱된 기본 타입의 값은 유효하지않은 값인 null을 가질 수 있다
        - 박싱된 타입은 null을 가질 수 있기때문에 특별히 주의해야할 점이 있다
          - 기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 박싱된 기본 타입의 박싱이 자동으로 풀린다(언박싱). 그러나 이때, 만약 null참조인 상태를 언박싱하면 NullPointerException이 발생한다!
            - 이를 해결하기위해서는 박싱된기본타입 쓰지말고 그냥 기본타입 사용하라~
      - 기본 타입이 박싱된 기본타입보다 시간과 메모리 사용면에서 효율적이다
        - 박싱과 언박싱이 지속적으로 이루어진다면 매우매우 성능이 저하된다
          - 개발 실수로 기본타입을 계속 박싱하도록하면.. 필요없는 객체를 생성하게되는꼴..
            ```java
              int i = 128; // -128 ~ 127 까지는 캐싱된 인스턴스를 가져오기때문에 오토박싱해도 항상 동일한 인스턴스를 가져오긴한다..
              int j = 128;

              Integer boxedI = i; // 오토박싱 -> 새로운 객체 생성
              Integer boxedJ = j; // 오토박싱 -> 새로운 객체 생성

              System.out.println(boxedI == boxedJ);
            ```
  - 박싱된 기본타입을 언제쓰는게 좋을까?
    - 컬렉션의 원소, 키, 값으로 사용
      - 자바 언어가 타입 파라미터로 기본 타입을 지원안하기때문..
    - 리플렉션을 통해 메서드를 호출할 때도 박싱된 기본타입 사용필요

---

- 아이템62_다른 타입이 적절하다면 문자열 사용을 피하라
  - 문자열을 쓰지 말아야할 사례
    - 문자열은 다른 값 타입을 대신하기에 적합하지 않다
      - 데이터를 받을떄 주로 문자열을 사용하는데, 받을 데이터가 진짜 문자열일 때만 그렇게 하자
      - 기본 타입이든 참조 타입이든 적절한 값 타입이 있다면 그것을 사용하고, 없다면 새로 하나 만들어라..
      - **잘 지키자..**
    - 문자열은 열거타입을 대신하기에 적합하지 않다
      - 상수 열거할때는 열거타입으로!!
    - 문자열은 혼합 타입을 대신하기에 적합하지 않다
      - ex. `String compoundKey = className + "#" + i.next();`
        - 만약 className이나 i.next()에 "#" 가 들어가있으면 에러 날수있음..
        - 문자열을 따로 파싱해야하니.. 성능도 그렇고 오류날 가능성 높음
        - String이 제공하는 기능에만 의존해야함
      - => 전용 클래스를 만들자! private 정적 멤버 클래스가 이런걸로 쓰기 딱좋다
    - 문자열은 권한을 표현하기에 적합하지 않다
      - ex. ThreadLocal이 나오기전, 스레드 지역변수 기능을 만들기위해 각 스레드 마다 사용하는 key를 String으로 받았다고함. 여기서 사용된 key가 권한을 표현..
        - 이렇게 사용하게되면, 개발자들끼리 소통의 오류로 같은 키를 사용하게되었을 경우 심각한 오류가 유발..
        - => 클래스 자체를 key로 만들어서 사용한게 ThreadLocal
          ```java
            // ThreadLocal 내부
            public void set(T value) {
                Thread t = Thread.currentThread();
                ThreadLocalMap map = getMap(t);
                if (map != null) {
                    map.set(this, value); // key로 ThreadLocal 자신을 셋팅
                } else {
                    createMap(t, value);
                }
            }
            
            ThreadLocalMap getMap(Thread t) {
                return t.threadLocals;
            }

            void createMap(Thread t, T firstValue) {
                t.threadLocals = new ThreadLocalMap(this, firstValue);
            }
          ```

---

- 아이템63_문자열 연결은 느리니 주의하라
  - 문자열 연결 연산자(+)는 여러 문자열을 합쳐주는 편리한 수단이나, 문자열 n개를 잇는 시간은 n^2에 비례..
    - 두 문자열은 불변이라 피연산자인 양쪽 문자열 모두를 복사하므로 성능엔 당연 좋지않다~
  - 문자열 연결이 잦다면, StringBuilder를 사용하자
    - java17은 StringBuilder와 StringBuffer가 AbstractStringBuilder를 상속하는데, 대부분이 공통으로 사용됨. 특히나 append와 같은 부분은 완전동일..
      - java8은 StringBuffer의 append 메서드에는 sychronized 키워드가 있다..

---

- 아이템64_객체는 인터페이스를 사용해 참조하라
  - 적합한 인터페이스만 있다면, 매개변수뿐 아니라 반환값, 변수, 필드를 전부 인터페이스 타입으로 선언하자
    - 프로그램의 유연성을 위해!!! (구체 클래스를 사용하는, 생성자로 생성하는 부분만 변경하면 어디도 고치지않을수 있다..!)
  - 적합한 인터페이스가 없다면? 클래스로!
    - 값 클래스
      - ex. String, BigInteger
      - 보통 final 클래스로 지정되기도함
    - 클래스 기반으로 작성된 프레임워크가 제공하는 객체일 경우
      - 이런 경우라도 구현 클래스보다 상위의 클래스 혹은 추상클래스를 사용하자
    - 인터페이스에는 없는 특별한 메서드 사용하는 경우
      - ex. PriorityQueue에는 Queue 인터페이스에서 제공하지않는 comparator 메서드가 

---

- 아이템65_리플렉션보다는 인터페이스를 사용하라
  - 리플렉션 기능을 활용하면 프로그램에서 임의의 클래스에 접근 가능
    - 생성자(Constructor), 메서드(Method), 필드(Field) 인스턴스를 가져와서 해당 인스턴스를 통해 클래스의 멤버이름, 필드 타입, 메서드 시그니처 등 가져올 수 있음
    - 또한 인스턴스를 생성하고, 메서드를 호출하거나, 필드에 접근할 수 있다
  - 리플렉션의 단점
    - 컴파일타임에 이루어지는 타입 검사가 주는 이점을 누릴수없음..
      - 해당 클래스에 없는 메서드를 리플렉션으로 호출하면 런타임시에 에러발견..
      - 아래 예시에는 6개의 예외를 런타임에 잡을수밖에 없다..
    - 리플렉션을 이용하면 코드가 지저분해지고 장황해진다
      - 아래 예시딱보면.. 더럽..
    - 성능이 떨어진다
      - 일반 메서드호출보다 훨씬느림
      ```java
        public static void main(String[] args) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("리플렉션사용");
            for (int i = 0; i < 100; i++) {
                makeSetWithReflection(name);
            }
            stopWatch.stop();

            stopWatch.start("일반생성자사용");
            for (int i = 0; i < 100; i++) {
                makeSetNormal();
            }
            stopWatch.stop();

            System.out.println(stopWatch.prettyPrint());
        }
        

        private static Set<String> makeSetNormal() {
            return new HashSet<>();
        }

        private static Set<String> makeSetWithReflection(String clazzName) {
            Class<? extends Set<String>> clazz = null;
            try {
                clazz = (Class<? extends Set<String>>) Class.forName(clazzName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            Constructor<? extends Set<String>> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.exit(1);
            }

            try {
                return constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                System.exit(1);
            }

            return null;
        }

        /*
          // 결과: 약 200배 차이남..

          StopWatch '': running time = 2277726 ns
          ---------------------------------------------
          ns         %     Task name
          ---------------------------------------------
          002266631  100%  리플렉션사용
          000011095  000%  일반생성자사용
        */
      ```
  - 단점이 있기에 리플렉션은 아주 제한된 형태로만 사용하자
    - 컴파일타임에 이용할 수 없는 클래스를 사용해야하는경우.. 그것도 인스턴스 생성에만 주로 쓰자.. 그리고 이렇게 만든 인스턴스는 인터페이스나 상위클래스로 참조해서 사용하자(물론, 인터페이스나 상위클래스가 있을때..)
      - 이래야지 적어도 만들어진 클래스를 사용할때, 컴파일 타임에 오류를 발견할수 있다..! (즉, 런타임에 에러발견을 막기위해서 상위로의 변환이 필요하다는것..)
  - 또한, 드물긴하나 런타임에 존재하지 않을 수도 있는 다른 클래스, 메서드, 필드와의 의존성을 관리할 때 사용하기 좋음
    - 버전이 여러개 존재하는 외부 패키지를 다룰 때 유용
      - 가장 오래된 버전만을 지원하도록 컴파일한 후, 이후 버전의 클래스와 메서드 등은 리플렉션으로 접근
    
---

- 아이템66_네이티브 메서드는 신중히 사용하라
  - 자바 네이티브 인터페이스(JNI)는 자바 프로그램이 네이티브 메서드를 호출하는 기술
    - 네이티브 메서드란 C나 C++ 같은 네이티브 프로그래밍 언어로 작성한 메서드
  - 네이티브 메서드 쓰임새? 
    - 플랫폼 특화 기능사용을 위해
    - 네이티브 코드로 작성된 라이브러리를 사용하기위해
      - 자바 라이브러리가 없으면..
    - 성능 개선을 위해 성능에 결정적인 영향을 주는곳만 네이티브로 사용
      - => 이를 위해서는 네이티브 메서드를 사용하는것은 권장하지않음.. 자바 버전업 되는 과정에서 많은 튜닝으로 네이티브 구현보다도 빠른게 있다함
  - 그러나, 자바가 성숙해가면서 하부 플랫폼 기능들을 점차 흡수하여, 네이티브 메서드를 사용할 필요가 줄어들고있음
    - ex. 자바9부터 새로운 process api를 추가하여 OS 프로세스에 접근가능
  - 네이티브 메서드 단점
    - 네이티브 언어를 사용시에 이를 사용하는 어플리케이션 메모리 훼손 오류로부터 안전하지않음..
    - 네이티브 언어는 플랫폼을 많이타고 이식성도 낮아서 속도가 오히려 느려질수도... 또한, 디버깅도 어렵..
    - 가비지 컬렉터가 네이티브 메모리는 자동 회수하지 못하고, 심지어 추적조차 할수없음
    - 자바코드와 네이티브 코드 경계를 넘나들때마다 비용 추가
    - 네이티브 메서드와 자바코드 사이에 별도의 접착 코드를 작성해야함 (귀찮음, 가독성떨어짐)


---

- 아이템67_최적화는 신중히 하라
  - > 최적화를 할때는 다음 두 규칙을 따르라.    
  첫번째, 하지마라.    
  두번째, (전문가한정) 아직 하지 마라. 다시 말해, 완전히 멍백하고 최적화된 해법을 찾을 때까지는 하지마라. by M.A.잭슨
  - 섣불리 최적화하면, 빠르지도 않고 제대로 동작하지도 않으면서 수정하기는 어려운 소프트웨어를 만들게된다
  - 최적화에 앞서 먼저 생각하면 좋을 것
    - 빠른 프로그램보다 좋은 프로그램을 작성하는것에 초점을 두자
      - 좋은 아키텍처로 만든다면 기본적으로 성능이 따라오기 마련
      - 그래도 성능이 안나온다면, 아키텍처 자체가 최적화 할 수 있는 길을 안내해줄것
        - 아키텍처가 잘 되어있다면(좋은 프로그램), 각각의 구성요소들이 독립되어있을 것이므로 최적화하기 좋은환경이 됨
      - 그러나, 이미 짜여진 아키텍처와 같은 큰 틀을 억지로 일부 변경해서 최적화를 진행하게되면, 유지보수나 개선이 어렵고 꼬인 구조의 시스템이 만들어진다
      - 그렇기에, 아키텍처를 설계할때 반드시 성능을 염두해 두어야할 것!
    - 컴포넌트끼리 혹은 외부 시스템과의 소통방식(ex. API, 네트워크 프로토콜, 영구저장용 데이터포맷)은 변경하기가 어렵기때문에 설계에서 반드시 세심하게 고려해야한다
      - **개발할때 컴포넌트간 통신을 어떻게 할 것인지는 개발하다가 필요해서 만드는게 아니라, 처음부터 어떻게 할지를 반드시 잡고 가야 한다!**
    - API 설계시 성능에 주는 영향을 고려하자
      - public 타입을 가변으로 만들면 방어적 복사를 유발
      - 컴포지션으로 해결 할 수 있는것을 상속으로 설계한 public 클래스는 상위클래스에 종속되어 그 성능제약까지도 물려받게됨
      - 인터페이스가 있는데 특정 구현타입을 사용하면, 해당 인터페이스의 성능향상된 구현타입을 사용못함
      - 잘못 설계된 API가 미치는 영향 예시
        - java.awt.Component
          ```java
            Component component = null;
            Dimension dimension = component.getSize();
            dimension.setSize(1,2); // 가변클래스.. getSize 메서드 호출시 계속 Dimension 객체를 새로 만들어서 전달(방어적 복사). => 불변으로 만드는게 이상적

            // getSize의 대안으로, getSize 호출시마다 Dimension을 새로 만드는대신, 아예 getWidth, getHeight를 호출하여 Dimension의 기본 타입 값을 반환하도록..(즉 괜히 Dimension을 만들어서 width와 height 가져오지말고, 바로 Component에서 전달해준다..) => 자바2부터
            component.getWidth();
            component.getHeight();

            // 하지만 이미 getSize를 호출하여 사용하고 있는 클라이언트는 자바2부터 개선은 되었지만 기존 코드를 수정하지못한다면 계속 가변클래스로 인해 나타나는 복사를 계속 경험해야한다.. => API 설계의 중요성!!
          ```
    - 성능 개선하겠다고 API를 왜곡하는것도 안좋다
      - 해당 성능은 다음 릴리즈때 개선될 수도 있는 문제이기에 그냥 내비뒀으면 알아서 개선이 되었을수 있는데, 괜히 왜곡된 API를 만들어서 사용하거나 이해하기 불편하게 만들어서 유지보수가 어렵게 만들수 있다
    - 꼭 최적화 시도 전후로 성능을 측정하자
      - 시도한 최적화가 생각보다 잘 안나올 수도 있음
      - 또한, 내가 짐작한 부분을 열심히 개선해봤는데, 아예 다른 구간일수도 있음
      - 프로파일링 도구를 활용하여 최적화 노력을 어디에서 해야하는지를 파악하자
        - 프로파일링 도구는 개별 메서드의 소비시간, 호출횟수 같은 런타임 정보를 제공해준다. 또한, 알고리즘 변경해야하는 사실도 알려줌
        - **프로파일링 도구는 아니지만, 자바 코드의 상세한 성능을 알기 쉽게 보여주는 마이크로 벤치마킹 프레임워크로 JMH가 있다**
          - [jmh 간단설명](https://yainii.tistory.com/30)
    - 자바의 성능 모델은 정교하지않기에 성능 변화를 일정하게 예측하기가 어렵다..
      - 구현 시스템, 릴리스, 프로세서 마다 차이가 잇음.. 그래서 자바 플랫폼과 하드웨어 플랫폼의 최적을 측정하고 찾아야한다..
        
---

- 아이템68_일반적으로 통용되는 명명 규칙을 따르라
  - 자바의 명명 규칙은 크게 철자와 문법으로 나뉜다 
    - [자바 명명 규칙](https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.1)
  - 철자규칙
    - 패키지, 클래스, 인터페이스, 메서드, 필드, 타입 변수의 이름을 어떻게 할것인가를 다룸
    - 꼭 따라라
    - 패키지, 모듈
      - 점으로 구분하여 계층적으로
      - 요소들은 모두 소문자 (혹은 드물게 숫자사용)
      - 조직 바깥에서도 사용될 패키지라면 조직의 인터넷 도메인 이름을 역순으로
        - ex. com.google~~
        - 표준 라이브러리와 선택적 패키지들은 각각 java와 javax로 시작
      - 그 나머지 패키지 이름은 하나 이상의 요소로 이루어지고, 8자 이내로.. 
        - 약어 사용가능
        - 첫 글자만 따서 해도 굿
    - 클래스(열거타입과 에너테이션 포함), 인터페이스
      - 하나 이상의 단어
      - 각 단어는 대문자로 시작
      - 널리 통용되는 줄임말(ex. min, max)을 제외하고는 단어를 줄여쓰지않도록
    - 메서드와 필드 이름
      - 첫 글자 소문자 사용하는것 외에는 클래스 명명규칙과 동일
      - 첫 단어가 약자라면 단어 전체가 소문자로..
      - 상수필드는 예외!
        - static final로 선언된것들이 상수
        - 상수필드를 구성하는 단어는 모두 대문자로 쓰며, 단어 사이는 밑줄로 구분
        - 이름에 밑줄 사용하는것은 상수필드가 유일
      - 지역변수는 다 비슷하나, 약어 써도 괜춘
        - 변수가 사용되는 문맥에서 의미를 쉽게 유추할수있으니..
        - 그러나, 매개변수는 좀더 신경쓸것!
      - 타입 파라미터 이름은 보통 한 문자로 표현
        - T: 임의의 타입 (Type)
        - E: 컬렉션 원소의 타입 (Element)
        - K, V: 맵의 키와 값 (Key, Value)
        - X: 예외 (eXecption)
        - R: 메서드의 반환 타입 (Return)
        - T, U, V, T1, T2, T3: 임의 타입의 시퀀스
  - 문법규칙
    - 객체를 생성할 수 있는 클래스 (열거 타입 포함)
      - 단수명사, 명사구
      - ex. Thread, PriorityQueue
    - 객체를 생성할 수 없는 클래스
      - 복수명사
      - ex. Collectors, Collections
    - 인터페이스 이름
      - 클래스도 동일한 명명규칙이거나, able, ible로 끝나는 형용사
      - ex. Runnable, Iterable
    - 애너테이션
      - 다양하게 사용
      - 지배적인 규칙없이 명사, 동사, 전치사, 형용사 두루 쓰임
      - ex. Inject, BindingAnnotation, Singleton, ImplmentedBy
    - 메서드
      - 어떤 동작을 수행하는 메서드
        - 동사, 동사구
      - boolean값을 반환하는 메서드
        - is (or 드물게 has) 로 시작해서 명사, 명사구, 형용사로
        - ex. isDigit, isProbablePrime, isEmpty, hasSiblings
      - 반환 타입이 boolean이 아니거나, 해당 인스턴스의 속성을 반환하는 메서드
        - 명사, 명사구, 혹은 get으로 시작하는 동사구
        - ex. size, hashCode, getTime
      - 객체의 타입을 바꿔서, 다른 타입의 또 다른 객체를 반환하는 인스턴스 메서드
        - to[Type]
        - ex. toString, toArray
      - 객체의 내용을 다른 뷰로 보여주는 메서드
        - as[Type]
        - ex. asList
      - 객체의 값을 기본 타입으로 반환하는 메서드
        - typeValue
        - ex. intValue
      - 정적 팩터리를 통해 객체를 생성할때
        - valueOf, instance, getInstance, newInstance, get[Type], new[Type]
    - 필드이름
      - boolean 타입의 필드이름
        - boolean 접근자 메서드에서 앞 단어를 뺀 형태
          - is나 has 이거 빼면될듯
      - boolean 외에는 명사나 명사구 사용
  - 주의할점
    - 오랫동안 따라온 규칙과 충돌할때 그 규칙을 맹종하지말고, 상식이 이끄는대로 만들자