effective_java_람다와_스트림

- 아이템42_익명 클래스보다는 람다를 사용하라
  - 예전 자바에서는 함수타입을 표현할때 추상메서드 하나있는 인터페이스를 사용
    - 이런 인터페이스의 인스턴스를 함수 객체라고하고, 특정 함수나 동작을 나타내는데 사용
    - jdk1.1 에서는 익명 클래스로 사용
    - 요런건 전략 패턴에서 활용
      - ex. Comparator 인터페이스가 추상전략, Comparator를 구현한게 구체적인 전략(이를 익명클래스로 구현)
  - 익명클래스는 너무길다.. 함수형 프로그래밍에 부적합..
  - 이에, 자바 8부터 **추상 메서드하나** + **인터페이스** 는 함수형 인터페이스라 부르고, 이 인터페이스의 인스턴스를 람다식(람다)을 통해 만드는 방식이 나오게됨
    - 코드 간결 + 어떤 동작하는지 잘 나타남
    - 컴파일러가 타입을 추론해주기 때문에, 타입을 명시하지않아도 된다
      - 타입을 명시해야 코드가 더 명확할때 혹은 컴파일러가 타입을 추론하지못할때를 제외하고는, 람다의 모든 매개변수 타입은 생략하자!
      - 컴파일러가 타입추론할때 필요한 타입정보의 대부분은 제네릭에서 얻는다
    - 람다를 활용해서 열거 타입의 인스턴스 필드에 함수형 인터페이스를 정의하면, 상수별로 다르게 동작하는 코드를 구현가능
        ```java
            public enum OperationWithLambda {
                PLUS("+", (a,b) -> a+b),
                MINUS("-", (a,b) -> a-b);

                private final String symbol;
                private final DoubleBinaryOperator doubleBinaryOperator;

                OperationWithLambda(String symbol, DoubleBinaryOperator doubleBinaryOperator) {
                    this.symbol = symbol;
                    this.doubleBinaryOperator = doubleBinaryOperator;
                }

                // ...
            }
        ```
      - 그렇다면, 기존 상수별 몸체를 만드는것(enum에 추상메서드 선언한거)은 의미가 없을까?
        - NO!
        - 메서드나 클래스와 달리 람다는 이름이 없고, 문서화 어려움.. 따라서 코드 자체로 동작이 명확하게 보이지않거나 코드 줄 수가 많아지면 람다를 쓰면안된다!
          - 람다는 한줄일때 가장좋고 길어야 세줄 안에 끝내야한다!
        - 람다식에서 enum의 인스턴스 필드나 메서드를 사용불가! 이를 사용하기위해서는 몸체로 만들어야함
  - 그렇다면, 익명 클래스는 이제 필요없나?
    - NO!
    - 추상 클래스의 인스턴스를 만들때 람다를 못쓰니 익명클래스를 써야한다 (인터페이스만 람다사용가능)
    - 추상 메서드가 2개 이상일때는 익명 클래스 써야한다
    - 람다는 자신을 참조할수 없기때문에 함수객체가 자신을 참조해야하는 경우가 있다면 익명클래스로 사용해야한다
      - 람다에서 this 사용시 바깥 인스턴스를 가리킨다. 익명 클래스는 인스턴스 자신을 가리킴
      - 이런 경우가 언제있을까나..?
  - 람다 주의점
    - 람다를 직렬화하는 일은 삼가자
    - 직렬화해야만 하는 함수 객체가 있다면, private 저적 중첩 클래스의 인스턴스를 사용하자
      - 무슨말?

---

- 아이템43_람다보다는 메서드 참조를 사용하라
  - 메서드참조를 사용하는 편이 보통은 더 짧고 간결하므로, 람다를 구현했을때 너무 길거나 복잡해지면 메서드참조를 사용하는게 좋다
  - 메서드 참조는 기능을 잘 드러내는 이름을 지어줄 수 있고, 메서드로되어있으니 문서로 남길수도있다
  - 메서드 참조의 유형
    
메서드 참조 유형 | 예 | 같은 기능을 하는 람다
---------|----------|---------
 정적 | Integer::parseInt | str -> Integer.parseInt(str)
 한정적(인스턴스) | Instant.now()::isAfter | Instant then = Instant.now(); <br> t -> then.isAfter(t)
 비한정적(인스턴스) | String::toLowerCase | str -> str.toLowerCase()
 클래스 생성자 | TreeMap<K,V>::new | () -> new TreeMap<K,V>()
 배열 생성자 | int[]::new | len -> new int[len]

---

- 아이템44_표준 함수형 인터페이스를 사용하라
  - 람다를 지원하게되면서, 템플릿메서드 패턴대신, 함수 객체를 받는 정적 팩터리나 생성자를 제공할수 있게됨
    - 이렇게되면, 함수객체를 매개변수로 받는 생성자나 메서드를 만들어야할것이고, 그에따라 함수형 매개변수 타입을 올바르게 선택할 수 있어야한다 (즉, 메서드나 생성자에 넘겨받는 파라미터를 어떤걸 선택할것인가? 기본적으로 java api가 제공하는 함수형인터페이스냐? 아니면 직접 만들거냐?)
    - 템플릿메서드 패턴을 사용하는 `LinkedHashMap.removeEldestEntry` 함수형 인터페이스를 활용해서 변경해보자
    ```java
      public static void main(String[] args) {
          Map<String, String> map = new LinkedHashMap<>() { 
              @Override
              protected boolean removeEldestEntry(Map.Entry<String, String> eldest) { // 템플릿에서 변경이 필요한 부분을 하위 클래스에서 재정의하여 사용하도록 하는 템플릿 메서드 패턴
                  return size() > 100; 
              }
          };

          // 위의 내용을 NewLinkedHashMap(함수형 인터페이스)으로 새롭게 만든다면..
          NewLinkedHashMap<String, String> newLinkedHashMap = new NewLinkedHashMap<>((EldestEntryRemovalFunction<String, String>) (m, eldest) -> m.size() > 100);

          // 위의 내용을 표준함수형 인터페이스로 변경하면..
          NewLinkedHashMap<String, String> newLinkedHashMap_표쥰함수버전 = new NewLinkedHashMap<>((BiPredicate<Map<String, String>, Map.Entry<String, String>>) (m, eldest) -> m.size() > 100); // 표준함수형으로 만들었을때는 컴파일러가 타입추론이 어려워, 캐스팅을 통해 제네릭으로 정보를 알려줘야함.. 이게 단점이라면 단점이겠네..

      }

      static class NewLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

          private EldestEntryRemovalFunction<K, V> eldestEntryRemovalFunction;
          private BiPredicate<Map<K, V>, Map.Entry<K, V>> eldestEntryRemovalFunction_표준함수버전;

          public NewLinkedHashMap(EldestEntryRemovalFunction<K, V> eldestEntryRemovalFunction) {
              this.eldestEntryRemovalFunction = eldestEntryRemovalFunction;
          }

          public NewLinkedHashMap(BiPredicate<Map<K, V>, Map.Entry<K, V>> eldestEntryRemovalFunction_표준함수버전) { // 되도록이면 기존에 있는 표준함수형을 사용하자!
              this.eldestEntryRemovalFunction_표준함수버전 = eldestEntryRemovalFunction_표준함수버전;
          }

          /*
          void afterNodeInsertion(boolean evict) { // possibly remove eldest
              LinkedHashMap.Entry<K,V> first;
              if (evict && (first = head) != null && eldestEntryRemovalFunction.remove(this, first)) { // (1)
                  K key = first.key;
                  removeNode(hash(key), key, null, false, true);
              }
          }

          (1) removeEldestEntry.remove(first) => eldestEntryRemovalFunction.remove(this, first) 로 변경
              **removeEldestEntry는 인스턴스 메서드 이므로 size() 메서드를 사용가능하나, 함수객체인 eldestEntryRemovalFunction은 생성시에 해당 Map의 인스턴스를 알수 없으니, Map을 넘겨줄수 있도록 해야한다** => 요런 센스중요!!
          */
      }

      @FunctionalInterface
      interface EldestEntryRemovalFunction<K, V> {
          boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
      }
    ```
  - java.tuil.function 패키지를 보면 다양한 용도의 표준 함수형 인터페이스가 담겨있는데, 필요한 용도에 맞는게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활용하자!
    - 총 43개가있음.. 상당히 범용적으로 사용할수있도록 이미 만들어놓았으니 최대한 사용하자
    - 생각보다 필요할때 찾아 쓸 수 있을 만큼 범용적인 이름을 사용
    - 성능 지대한 악영향을 미치니.. 기본타입이 지원되는 함수형 인터페이스(ex. LongToIntFunction)에 박싱된 기본 타입을 넣어 사용하지는 말자.. 굳이 그럴필요도없고, 그래야만 한다면 박싱된걸 받을수 있도록 새로 만들어야할듯..
      ```java
        @FunctionalInterface
        public interface LongToIntFunction {
            int applyAsInt(long value);
        }
      ```
  - 하지만, 직접 함수형 인터페이스를 만들어야할때도 있는데, 어떤경우가 그럴까?
    - 표준 인터페이스 중 필요한 용도에 맞는게 없을때 (당연..)
      - ex. 매개변수 3개 받는 Predicate, checked Exception을 던져야하는경우 등
    - 다음 3가지중 하나 이상을 만족한다면,, (`Comparator<T>`가 `ToIntFunction<T,U>`로 만들지 않은 이유와 동일)
      - 자주 쓰이며, 이름 자체가 용도를 명확히 설명
      - 반드시 따라야 하는 규칙이 있을때
      - 유용한 디폴트 메서드를 제공할 수 있을때
  - 주의점
    - 함수형 인터페이스를 만들때 @FunctionalInterface를 붙여주자~
      - 해당 클래스를 읽을때, 람다용으로 설계된것임을 알려줄수있음
      - 해당 인터페이스가 추상메서드를 오직 하나만 가지고 있어야 컴파일이 됨
      - 그로인해 개발자의 실수 줄일수 있음
    - 서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중정의해서는 안된다
      - 코드로보자
        ```java
            public static void main(String[] args) {
                // 함수형 인터페이스를 인수로 받을때, 다중정의의 모호함
                ExecutorService executorService = null;
                executorService.submit(() -> setString("hi")); // ExecutorService.submit은 인수가 없는 람다식을 사용하는 Callable과 Runnable을 받을수있도록 오버로딩 되어있는데, 당연 컴파일러가 에러를 내진않지만, 이런식으로 사용하면 클라이언트 입장에서 Callable을 써서 결과값을 받을수있는것인지, Runnable을 써서 결과값을 받을수 없는지가 코드로 딱 보아서 파악하기 애매할듯..
                executorService.submit(() -> setObject("hi"));
                executorService.submit(() -> "hi");

                // 또한, 함수형 인터페이스의 이름만 다르고 동일한 람다형식을 받는 메서드가 오버로딩되어 있을 경우, 클라이언트가 별도의 형변환을 명시해주어야 사용가능한데, 해당 예시는 위의 newLinkedHashMap을 보자
            }

            static Object setString(String str) {
                return new Object();
            }

            static void setObject(Object obj) {

            }
        ```
