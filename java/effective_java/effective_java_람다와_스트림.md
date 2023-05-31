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

---

- 아이템45_스트림은 주의해서 사용하라
  - 스트림 API의 목적
    - 다량의 데이터 처리작업(순차 or 병렬)을 돕고자 자바8 에서 추가
  - 스트림 API가 제공하는 추상개념
    - 스트림
      - 데이터 원소의 유한 혹은 무한 시퀀스를 뜻함
      - 스트림의 원소들은 배열, 컬렉션, 파일, 정규표현시 패턴매처, 난수 생성기, 다른 스트림 등으로 올 수 있다
        - 스트림안의 데이터 원소들은 객체참조나 기본 타입값
          - 기본 타입값으로는 int, long, double 3개 지원
    - 스트림 파이프라인
      - 데이터의 연산단계를 표현하는 개념
      - 스트림 파이프라인은 소스 스트림에서 시작하여 종단연산으로 끝나며, 그 사이에 하나 이상의 중간연산이 있을 수 있다
        - 소스 스트림 -> 중간연산(여러개 가능) -> 종단연산  
        - 중간연산은 스트림을 어떠한 방식으로 "변환" 하는것
      - 스트림 파이프라인은 지연평가(lazy evaluation) 된다
        - 종단연산 호출시 이루어지기때문
        - 종단연산에 쓰이지않는 데이터 원소는 계산에 쓰이지않는다
          - => 종단연산의 계산을 이야기하는듯...? 중간연산에서 계산되다가 걸리질수도있으니..?
          - => 종단연산이 호출되어야 데이터 처리가 시작되니깐.. 종단연산이 호출되지않으면(= 종단연산에 쓰이지않으면)데이터 원소는 계산에 쓰이지않는다고 하는것인듯..?
        - 이러한 종단연산이 무한 스트림을 다룰수 있게해주는 열쇠
  - 스트림 API는 메서드 연쇄(체이닝)를 지원하는 플루언트(fluent) API
    - 파이프라인 하나를 구성하는 모든 호출을 연결하여 하나의 표현식으로 완성가능
  - 무조건 스트림으로 변경하려하지마라~
    - 스트림으로 변경시에 코드 가독성과 유지보수 측면에서 손해볼 수 있다 (적절하게 반복문과 스트림을 조합하는것도 좋음)
  - 함수객체(람다, 메서드참조) vs for-loop의 코드블록
    - 코드 블록에서는 지역변수를 읽고 수정 가능
      - 람다에서는 final 이거나 사실상 final인 변수(람다캡처링)만 읽을 수 있고, 바깥 지역변수 수정불가능
    - 코드 블록에서는 return을 사용해 메서드에서 빠져나가거나 , break, continue 등으로 블록 바깥의 반복문을 종료하거나 반복을 건너띌수 있다
    - 코드 블록에서는 메서드 선언에 명시된 검사 예외(checked exception)를 던질 수 있다
    - 람다는 위 코드블록이 가능한것을 다 못한다. 즉, 위와 같은 작업이 필요하다면, 스트림에서 쓰기가 어렵기때문에 for-loop를 사용해야한다
    - 람다 객체를 사용한 스트림을 쓰기 좋은 상황은?
      - 원소들의 시퀀스를 일관되게 변환
      - 원소들의 시퀀스 필터링
      - 원소들의 시퀀스를 하나의 연산을 사용해 결합 (더하기, 연결하기, 최솟값구하기 등)
      - 원소들의 시퀀스를 컬렉션에 모은다 (공통된 속성을 기준으로 묶어가며?)
      - 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다
  - 스트림으로 처리가 어려운 일
    - 한 데이터가 파이프라인에서 여러 단계(중간연산)를 통과할때, 각 단계에서의 값들을 접근하기가 어려움
    - 스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는구조..
    - 원래 값과 새로운 값의 쌍을 저장하는 객체를 사용해 우회할수도있지만.. 코드가 지저분해진다..
    - 앞단계의 값이 필요하면, 매핑을 거꾸로 수행하라!
  - 결론
    - 스트림과 for-loop 방식중 무조건 어느것이 정답은 없음
    - 둘 조합이 짱인듯
    - 애매하면 둘다 해보고 더 나은쪽 선택

  - 기타 팁
    - 람다에서는 타입이름이 자주 생략되기때문에, 매개변수 이름을 잘 지어야한다! 그래야 스트림 파이프라인의 가독성이 유지
    - 도우미 메서드를 활용하는것이 스트림 파이프라인에서 가독성도 높여주고 타입 정보(람다에서는 타입정보가 자주생략..)를 유추하기도 쉽다
    - 스트림을 반환하는 메서드 이름은 원소의 정체를 알려주는 **복수 명사**로 쓰자

---

- 아이템46_스트림에서는 부작용 없는 함수를 사용하라
  - 스트림이 제공해주는 표현력, 속도, 병렬성을 얻기위해서는 스트림 API에 대한 숙지와 더불어 함수형 프로그래밍에 기초한 패러다임을 받아들여야한다
    - 이 패러다임은 계산을 일련의 **변환**으로 재구성하는게 핵심인데, 각 변환 단계는 이전 단계의 결과를 받아 처리하는 **순수함수**여야한다
      - 순수함수란 오직 입력만이 결과에 영향을 주는 함수를 말한다 (즉, 입력이 같으면 결과는 항상 같은 것)
        - => 다른 가변상태를 참조하지도않고, 함수 스스로도 상태를 변경하지않는 함수 (외부 가변객체 참조 안함 + 넘겨받는 파라미터 객체의 상태변경안함)
    - 패러다임을 잘 이해하지 못한 채 API만 사용한코드
    - 코드로 보자
      ```java
        void 바람직하지않은_stream사용() throws FileNotFoundException { // 패러다임을 잘 이해하지 못한 채 API만 사용한코드
            File file = null;
            Map<String, Long> freq = new HashMap<>();

            try(Stream<String> words = new Scanner(file).tokens()) {
                words.forEach(word -> {
                    freq.merge(word, 1L, Long::sum); // 상태를 수정하고 있음. 즉, 순수함수가 아니다!
                });
            }
        }

        void 바람직한_stream사용() throws FileNotFoundException {
            File file = null;
            Map<String, Long> freq;

            try(Stream<String> words = new Scanner(file).tokens()) {
                freq = words.collect(groupingBy(String::toLowerCase, counting()));
            }

            //  try(Stream<String> words = StreamUtils.asStream(new Scanner(file))) { // java 8 일때..
            //      freq = words.collect(groupingBy(String::toLowerCase, Collectors.counting()));
            //  }
        }

        static class StreamUtils {
            public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
                return asStream(sourceIterator, false);
            }

            public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
                Iterable<T> iterable = () -> sourceIterator;
                return StreamSupport.stream(iterable.spliterator(), parallel);
            }
        }
      ```
  - 수집기(Collector)
    - 스트림을 사용하는데 꼭 필요한 개념
    - Collectors 클래스는 축소(reduction)전략을 캡슐화한 블랙박스 객체라고 볼 수 있음
      - 블랙박스 객체란 객체의 내부동작을 외부에서(사용자) 알 필요없이 인터페이스만을 제공하는 객체
        - Collectors를 통해서 (이쁘게 만들어진)Collector 인터페이스를 제공해주기때문~ 
      - 축소란 스트림의 원소들을 객체 하나에 취합한다는뜻 (보통 수집기가 생성하는 객체는 Collection)
      - Collectors의 메서드는 39개 (자바 10에서는 4개가 더늘어 총 43개)
        - **toList**, **toSet**, toCollection
          ```java
            List<String> 가장흔한단어10개뽑아내기(Map<String, Long> freq) {
                return freq.keySet().stream()
                        .sorted(comparing(freq::get).reversed()) // 한정적 메서드 참조
                        .limit(10)
                        .collect(toList());
            }
          ```
        - **toMap**
          - 스트림의 각 원소가 고유한 키에 매핑되어있을때 사용
          - 스트림의 원소가 같은 키로 매핑하려한다해도, 적절한 merge 함수만 사용한다면 문제없음 (만약 merge함수 사용안하면 예외던져짐)
            ```java
                void toMap_예제_앨범들중에서_음악가의_베스트앨범() {
                    List<Album> albums = null;

                    Map<Artist, Album> collect = albums.stream()
                            .collect(Collectors.toMap(Album::getArtist, v -> v, maxBy(comparingInt(Album::getSales)))); // albums에 동일한 artist는 여럿 있을수 있다. 즉, 같은 key로 매핑이 될텐데, maxBy라는 병합함수를 통해서 key가 중복될때 무엇을 선택할지 명시해주고있다. maxBy는 BinaryOperator의 디폴트 메서드. 최대값인 대상으로 병합하라고 알려주는것
                    
                }
            ```
        - **groupingBy**
          - 입력으로 분류 함수(classifier)를 받고, 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기(Collector)를 반환해준다
          - 인자로 분류함수 하나만 받는 경우에는, Map의 value는 toList가 된다. 즉, toMap은 원소하나에 value 하나가 mapping 되는데, groupingBy는 이름처럼 지정한 분류함수대로 N개의 값이 나올 수 있기에 default가 List
          - List외의 값을 갖도록(ex. Set) 하기위해서는 다운스트림 수집기도 명시해야한다 (인자로 두개 받는 메서드)
            - `public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) { ... }`
            - Collectors.counting()을 downstream 수집기에 지정하면, 분류함수로 지정한 카테고리(key)에 속하는 원소의 갯수를 값(value)으로하는 Map을 얻을수 잇다
        - partitioningBy
          - 분류 함수자리에 predicate를 받고 키가 Boolean인 맵을 반환
        - counting
          - 다운스트림 수집기 전용으로 보면됨
          - Stream에 count가 있기때문에, `collect(Collectors.counting())` 이런식으로 쓸일은 없다
        - summing, averaging, summarizing
          - 위 각각이 int, long, double 스트림용으로 하나씩 존재
        - reducing, filtering, mapping, flatMapping, collectingAntThen
          - 대부분 프로그래머는 이들의 존재를 몰라도 상관없음
          - 설계 관점에서 보면, 이 수집기들은 스트림 기능의 일부를 복제하여 다운스트림 수집기를 작은 스트림처럼 동작하게 한것
            - 주로 Collectors 내부적으로 사용하기위한것으로 보임..
            - ![](2023-05-21-21-28-02.png)
              - reducing 관련 Doc 설명에 따르면, groupingBy나 partitioningBy의 downstream 수집기에서 사용하는 용도라고함. 그냥 단순 reduction할꺼면 Stream.reduce 사용하라함
              - => 그냥 downstream Collector 사용하는곳에서 쓰고자 만든것으로보임..
        - minBy, maxBy
          - Collectors에 정의되어있지만, "수집"과는 별 관련없음
          - Stream 인터페이스의 min과 max 메서드를 살짝 일반화한것. BinaryOperator의 minBy나 maxBy의 수집기 버전으로 이해하자 (위와 유사한듯.. downstream 수집기에서 사용하기위함인듯..)
        - **joining**
          - Collectors에 정의되어있지만, "수집"과는 별 관련없음
          - 원소들을 연결하는 수집기를 반환
          - CharSequence 인스턴스(ex. String)의 스트림에만 적용가능
  - 기타 팁
    - forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산하는데는 쓰지말자! (출력이나 로깅에서나 사용하자!!)
      - 가끔은 스트림 계산 결과를 기존 컬렉션에 추가하는 등의 다른 용도로 사용하기도한다함
    
---

- 아이템47_반환타입으로는 스트림보다 컬렉션이 낫다
  - 일련의 원소를 반환하는 메서드의 반환타입 (자바7까지..)
    - Collection, Set, List 같은 컬렉션 인터페이스, Iterable, 배열.. 
    - 어떤 기준으로 반환타입을 고르나?
      - 보통은 Collection 인터페이스로
      - 반환된 원소 시퀀스가 일부 Collection의 인터페이스를 구현할 수 없으면 Iterable
        - Collection.contains 메서드같은.. 
          - <span style="color:red">언제이러나?</span> 
      - 반환 원소들이 기본타입이거나 성능에 민감하면 배열
  - 자바8부터 스트림등장..!
    - Stream은 iteration(반복)을 지원하지않는다..! 따라서 이 둘을 조화롭게 사용해야 좋은코드가 나옴 (아이템45참고)
    - Stream은 for-each가 불가하다.. (ex. `for(Object obj : list)`)
      - Stream 인터페이스는 Iterable 인터페이스가 정의한 추상메서드를 전부 포함하고, Iterable 인터페이스가 정의한 방식대로 동작하나, Stream이 Iterable을 확장하고있지않기때문에 for-each를 사용할 수 없다
      - 이에 대한 우회로 Stream이 Iterable 인터페이스의 추상메서드를 가지고있기에 Stream의 메서드참조(Stream반환메서드::iterator)를 통해 Iterable만들어서 for-each 를 사용가능하긴하나.. 캐스팅도 해야하는 부분으로 상당히 불편 및 위험
        - 어댑터를 만들어라!
          ```java
          public static <E> Iterable<E> streamToIterable(Stream<E> stream) { // 캐스팅할 필요없이 타입추론가능. 하지만 이런 어뎁터는 성능이 중요할때에는 사용하면안된다~ 느려~~
              return stream::iterator;
          }

          public static <E> Stream<E> iterableToStream(Iterable<E> iterable) {
              return StreamSupport.stream(iterable.spliterator(), false);
          }
          ```
  - 그럼 스트림이 등장했으니 객체 시퀀스 반환하는 메서드는 어떻게 써볼까
    - 오직 스트림 파이프라인에서만 쓰인다면, 스트림을 반환하자
    - 반환된 객체들이 반복문에서만 쓰일걸 안다면 Iterable을 반환
    - public api를 작성할때는 둘다 제공해주자
      - 둘다 제공해줄수 있는 가장 베스트는 Collection이나 그 하위타입으로 리턴해주는게 좋음
        - Collection은 Iterable의 구현체 + stream도 반환하는 메서드 제공
          ```java
            Stream<Temp> stream = collection.stream(); // 컬렉션은 stream도 반환가능
            for (Temp temp : collection) { } // 컬렉션은 Iterable 구현체
          ```
        - 반환하는 시퀀스의 크기가 메모리에 올려도 안전할 만큼 작다면 ArrayList와 HashSet과 같은 표준 컬렉션 구현체를 반환하자~
        - 하지만 메모리에 올리기 큰 시퀀스라면?
          - 해당 시퀀스의 전용 컬렉션을 별도 구현 
            - ex. 멱집합(한 집합의 모든 부분집합을 원소로 하는 집합)을 모두 메모리에 계속 들고있는게아닌, 필요할때만 메모리에 올려서 사용하도록하는 전용 컬렉션을 만든다!
              ```java
                public class PowerSet {

                    public static <E> Collection<Set<E>> of(Set<E> set) {
                        ArrayList<E> src = new ArrayList<>(set);

                        if (src.size() > 30) {
                            throw new IllegalArgumentException("원소가 30개 초과는 놉"); // AbstractList의 size가 int를 반환하기에.. 양수는 2^31 - 1 넘는 값은 안됨..
                        }

                        return new AbstractList<>() {
                            @Override
                            public Set<E> get(int index) {      // get이 호출되어 메모리에 result가 로드되는 시점은 iterator.next를 호출할때이다! (지연평가!) 그리고 for-each를 통해서 데이터를 계속 가지고있는게아닌, 루프를 빠져나오면 GC 대상이된다!
                                HashSet<E> result = new HashSet();

                                for (int i = 0; index != 0; i++, index >>= 1) {     // for문을 이렇게 잘 활용하자 + 비트를 잘 활용하자
                                    if ((index & 1) == 1) {
                                        result.add(src.get(i));
                                    }
                                }
                                return result;
                            }

                            @Override
                            public int size() {
                                return 1 << src.size();
                            }

                            @Override
                            public boolean contains(Object o) {
                                return o instanceof Set && src.containsAll((Set)o);
                            }
                        };
                    }

                    public static void main(String[] args) {

                        Set<String> a = Set.of("a", "b", "c");
                        Collection<Set<String>> powerSets = PowerSet.of(a); // debugger에서는 이 라인에 값이 다 나오는데, 이것은 debug evaluation을 진행해서임.. 즉, 디버깅을 디버거 내부적으로 임시로 평가해놓은것.. 그렇기때문에 실제 HashSet 생성할때의 주소와 이 라인에 셋팅된 HashSet 주소가 당연다르다~
                        System.out.println("멱집합 사이즈 : "+powerSets.size());
                        for(Set<String> set : powerSets) {
                            System.out.println(set);
                        }
                    }
                }
              ```
          - 위의 상황처럼 contains와 size를 구할수 있는 경우에는 AbstractCollection 또는 그 하위 구현체(ex. AbstractList)를 활용하여 지연평가(lazy collection이라고도 할 수 있는듯)를 수행하면되나, 이를 구현하기 어려운경우에는 Stream이나 Iterable을 사용하자
            - ex. Stream을 활용하여 입력리스트의 모든 (연속적인)부분리스트를 만들기(이 또한, 모두 메모리에 로드해놓는것은 좋지않다.. 스트림을 활용하여 지연평가를 수행하자..)
              ```java
                public class SubList {
                    // 입력리스트의 (연속적인) 부분리스트만들기

                    public static <E> Stream<List<E>> of(List<E> list) {
                        /*
                            prefix의 suffix를 사용하자
                              - prefix: 맨 앞의 원소가 포함된 부분리스트
                              - suffix: 맨 마지막 원소가 포함된 부분리스트
                        */

                //        return Stream.concat(Stream.of(Collections.emptyList()),
                //                IntStream.rangeClosed(1, list.size())
                //                        .mapToObj(start -> list.subList(0, start))
                //                        .flatMap(l -> IntStream.range(0, l.size())
                //                                .mapToObj(end -> l.subList(end, l.size()))));

                        return Stream.concat(Stream.of(Collections.emptyList()),
                                prefix(list).flatMap(SubList::suffix));
                    }

                    private static <E> Stream<List<E>> prefix(List<E> list) {
                        return IntStream.rangeClosed(1, list.size())
                                .mapToObj(start -> list.subList(0, start));
                    }

                    private static <E> Stream<List<E>> suffix(List<E> list) {
                        return IntStream.range(0, list.size())
                                .mapToObj(end -> list.subList(end, list.size()));
                    }

                    public static void main(String[] args) {
                        SubList.of(Arrays.asList("a", "b", "c"))
                                .forEach(System.out::println);
                    }
                }
              ```
              
    


  - Stream을 사용하면, 메모리에 모두 올려놓는게 아닌, filter등을 사용해서 몇개를 걸러서 정말 필요한것만 메모리에 올려놓을수 있겠다.. (지연평가로 인한..?)
  - 하지만, 그냥 컬렉션을 사용하게되면, 메모리에 모두 올려놓고 필요한것들을 다시 거르는 작업을 수행하게되므로.. 메모리 낭비가 있을수 있다..
- 기타 팁
  - 비트 벡터란(챗gpt): 
    - 비트 벡터(Bit vector)는 데이터 구조의 한 종류로, 이진 비트의 배열로 표현되는 벡터입니다. 각 비트는 0 또는 1의 값을 가질 수 있으며, 이를 이용하여 효율적인 데이터 저장과 연산을 수행할 수 있습니다.
    - 비트 벡터는 일반적으로 메모리를 절약하면서 효율적인 비트 단위 연산을 지원하는 자료구조입니다. 각 비트는 주로 특정 원소의 상태를 나타내기 위해 사용됩니다. 예를 들어, 비트 벡터를 사용하여 집합(set)을 표현할 수 있으며, 각 비트의 값이 1이면 해당 원소가 집합에 속하는 것을 의미합니다.
    - 비트 벡터는 다양한 응용 분야에서 사용될 수 있습니다. 예를 들어, 압축 알고리즘에서 비트 벡터는 데이터를 압축하고 해제하는 데 사용될 수 있습니다. 또한, 그래프 알고리즘에서 비트 벡터는 노드의 방문 여부를 표시하거나 경로 검색 등에 사용될 수 있습니다.
    - 비트 벡터는 데이터 구조 자체가 단순하고 연산이 빠르기 때문에, 일부 상황에서 메모리 사용량과 성능 측면에서 이점을 제공할 수 있습니다. 하지만 비트 벡터는 원소의 개수가 많아지면 메모리 사용량이 증가하므로, 원소의 범위가 크거나 많은 원소를 다루어야 하는 경우에는 고려해야 할 사항입니다.
  - for-each는 배열과 Iterable 인터페이스를 구현한 구현체에서 사용가능하다! 
    - Iterable 인터페이스를 구현한 구현체에서는 iterator를 호출하여 while문으로 돌림
    - 배열은 일반 for문으로..
      ```java
        Iterable<Temp> iterable = () -> {
            return new Iterator<Temp>() {
                public boolean hasNext() {
                    return false;
                }

                public Temp next() {
                    return null;
                }
            };
        };

        for (Temp t : iterable) {
            System.out.println(t);
            System.out.println();
        }

        /*
        // 컴파일러가 iterable 인터페이스를 구현한 구현체를 for-each문을 사용하면, 아래와 같이 만들어준다..

        Iterator var3 = iterable.iterator();

        while(var3.hasNext()) {
            Main.Temp t = (Main.Temp)var3.next();
        }
        */


        String[] strArr = new String[]{"A", "B"};

        for (String s : strArr) {
            System.out.println(s);
        }
        /*
        // 컴파일러가 배열을 가지고 for-each문을 사용하면, 아래와 같이 만들어준다..
            String[] strArr = new String[]{"A", "B"};
            String[] var9 = strArr;         // var 뒤에 숫자는 중요하지않음..
            int var5 = strArr.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String s = var9[var6];
                System.out.println(s);
            }
        */
      ```

---

- 아이템48_스트림 병렬화는 주의해서 적용해라
  - 동시성 프로그래밍을 할떄는 **안정성(safety)**과 **응답가능상태(liveness)**를 유지하기위해 애써야하는데, 병렬스트림 파이프라인 프로그래밍에서도 마찬가지
  - 스트림 라이브러리가 병렬화하는 방법을 찾아내지 못하면 CPU만 겁나 높아지고 응답불가상태(liveness failure)가 된다
    - 파이프라인 병렬화로 성능기대어려운 경우
      - 데이터 소스가 Stream.iterate 
      - 중간연산으로 limit 사용
        - 파이프라인 병렬화는 limit을 다룰때, CPU코어가 남는다면 원소를 몇개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정
        - ex. 쿼드 코어를 사용하고있고, limit이 20이라면, 20번째 결과를 구할때 나머지 3개의 코어는 쉬고있기에 다음 21,22,23번째 작업을 수행할수있는데, 이 세 작업이 겁나 오래걸린다면 이미 20번째 결과는 구했음에도 파이프라인은 종료되지않는다..
    - 파이프라인 병렬화로 효과 좋은경우
      - 데이터 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap, 배열, int범위, long 범위
        - 위 자료구조는 데이터를 원하는 크기로 정확하고 손쉽게 나눌수 있기에 다수의 스레드가 분배하기 좋다
          - 나누는 작업은 SplitIterator 가 수행
        - 또한 참조 지역성(locality of reference)이 뛰어난 자료구조들이기에 처리가 빠르다
          - 참조 지역성: 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻
            - 참조 지역성이 가장 뛰어난 자료구조는 **기본타입의 배열** (기본 타입의 배열은 데이터 자체(참조x)가 메모리에 연속해서 저장)
          - 참조 지역성이 낮으면, 스레드는 데이터가 주 메모리에서 캐시메모리로 전송되어 오기를 기다리며 대부분 시간을 멍때림
      - 병렬화하기 좋은 종단연산을 사용
        - 종단연산에서 수행하는 작업량이 파이프라인에서 큰 비중을 차지하고, 순차적인 연산이라면 파이프라인 병렬수행의 효과는 제한
        - 적절한 종단연산 종류
          - reduce(축소)
            - min, max, count, sum 과 같은 종단연산은 내부적으로 reduce를 사용
          - anyMatch, allMatch, noneMatch 처럼 조건에 맞으면 바로 반환되는 메서드
        - 주의
          - 가변축소(mutable reduction)를 사용하는 Stream의 collect도 병렬시에 사용할수는 있지만, 컬렉션들을 합치는 부담이 크기때문에 성능에 그리 좋지않다
      - 직접구현한 Stream, Iterable, Collection이 병렬화의 이점을 누리고싶다면, spliterator를 재정의하라!
        - **자세한 내용은 찾아볼것**
  - 스트림을 잘못 병렬화하면 결과가 잘못되거나 오동작할수도있는데, 이를 안전실패(safety failure) 라고 한다
    - 언제 저런 안전 실패를 유발하는가?
      - Stream 명세대로 개발하지않을경우
        - ex. Stream의 reduce연산에 건네지는 accumulator와 combiner 함수는 반드시 결합법칙을 만족하고(앞에서부터 결합하나 뒤에서부터 결합하나 순서에 상관없이 동일해야한다는것), 간섭받지않고(파이프라인 수행동안 데이터 소스변경되면 안됨), 상태를 갖지 않아야한다
  - 병렬화를 하기위한 모든 조건이 만족한다할지라도 **병렬화에 드는 추가 비용을 상쇄하지못한다면** 성능향상을 미미..
    - 아래 공식이 되어야 성능향상을 경험할 수 있음..
      - 스트림 안의 원소 수 x 원소당 수행되는 코드 줄 수 >= 수십만
  
  - 성능 테스트가 반드시 필요하며, 병렬화시 보통은 공통의 포크-조인 풀(스레드풀)을 사용하므로 잘못된 파이프라인을 사용하면 시스템의 다른부분에 악영향끼칠수 있음을 명심
    - [스트림 병렬화 사용시 thread-pool custom하는 방법](https://www.baeldung.com/java-8-parallel-streams-custom-threadpool)
    
  - 이렇게 주의해야할 점이 많고, 실제로 사용되는 경우가 많이 없을수 있지만, 머신러닝이나 데이터처리 같은 특정분야에서는 병렬화를 잘 활용하는게 큰 이점이 될 수 있다
    - 코어 수에 비례하는 성능을 경험할수 있으니!
    
  - 기타 팁
    - 무작위 수들로 이뤄진 스트림을 병렬화하려거든 SplittableRandom을 사용하라
      - ThreadLocalRandom은 단일스레드에서 쓰고자 만들어짐 (병렬 스트림에서도 사용가능하나, 성능이 SplittableRandom보다 뒤짐)
      - Random은 병렬처리하면 최악.. Random쓸곳도 ThreadLocalRandom쓰면된다..
        
    - [SplittableRandom, ThreadLocalRandom, Random 관련 설명](https://dveamer.github.io/backend/JavaRandom.html)
      - Random 도 thread-safe하고 ThreadLocalRandom도 thread-safe합니다. 하지만 그 이유는 다릅니다. Random은 seed를 AutomicLong을 사용하기 때문에 멀티 쓰레드의 요청에 대해서도 순서대로 처리하여 마치 동기화(synchronized) 처리처럼 동작하기 때문에 느립니다. ThreadLocalRandom은 AutomicLong 사용하지 않고 thread별로 seed값을 다르게 관리하기 때문에 Random보다 멀티 쓰레드에 대해 응답이 빠르면서도 thread-safe 합니다. 
      - 결론을 이야기 하자면, JDK 7부터는 Random을 사용할 이유가 없습니다. ThreadLocalRandom을 쓰면 됩니다.
    