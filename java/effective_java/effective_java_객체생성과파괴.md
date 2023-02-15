effective_java_객체생성과파괴

- 아이템1_생성자대신 정적 팩토리 메서드를 고려하라
  - 정적 팩토리 메서드 : 클래스의 인스턴스를 반환하는 단순한 정적 메서드
    - 장점
      - 이름을 가질수 있다
        - 이를 통해 반환될 객체의 특성을 설명하기좋음 (그냥 생성자는 어렵..)
        - 시그니처가 같은 생성자가 여러개 만들어질 것 같으면, 정적팩토리 메서드로 바꾸고 각각의 차이를 잘 드러내줄 이름을 사용하자
      - 호출될 때마다 인스턴스를 새로 생성하지는 않도록 할 수 있다
        - 인스턴스를 미리만들어놓거나, 새로 생성한 인스턴스를 캐싱하여 사용하도록 구성가능
        - 인스턴스 통제 클래스로 만들수 잇음
          - 인스턴스 통제 클래스 : 언제 어느 인스턴스를 살아 있게 할지를 통제가능한 클래스
          - 활용
            - 싱글턴
            - 인스턴스화 불가 (Ex. util성 클래스)
            - 플라이웨이트패턴의 근간
            - 열거타입(enum)
              - *열거타입은 인스턴스가 하나만 만들어짐을 보장!*
      - 반환 타입의 하위 타입 객체를 반환 할 수 있음
        - Collections의 유틸클래스 하나로, 수정불가나 동기화 등의 기능을 덧붙인 총 45개의 유틸리티 구현체를 제공해줄수 있도록 활용
        - 명시한 인터페이스대로 동작하는 개체를 얻을 것임을 알기에 굳이 별도 문서를 찾아가며 실제 구현체가 어떻게 되는지 찾을 필요없다! (사용자는 인터페이스로만 다루게 된다)
        - 자바 8부터는 인터페이스로 정적팩토리를 만들수있기에 인터페이스에서 설계하자!
          - 그러나 인터페이스에 정의된 정적메서드는 package-private을 허용하지않기때문에, 접근범위가 package-private이라면 여전히 동반클래스(ex. Collections 클래스)에 두어야할수도 있다 
      - 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환 가능
        - EmumSet과 같은경우 생성자 없이 정적 팩터리 메서드만 제공하는데, 원소갯수에따라 다른 구현체를 반환한다.. 사용자는 EnumSet을 사용하는데 있어서 이런 내용을 알지도못하고 알 필요도 없다. 
        - 그에 따라 다음 개선된 릴리즈때에는 또 다른 EnumSet의 구현체를 제공할 수 있다.
      - 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다
        - 이 유연함은 서비스 제공자 프레임워크 (service provider framework) 를 만드는 근간이됨
          - 클라이언트에게 필요한 구현체를 전달해주는데, 프레임워크가 이를 통제하여서 구현체로부터 분리할수 있게해준다
          - 핵심 컴포넌트
            - 서비스 인터페이스: 구현체의 동작을 정의하는 인터페이스 (팩토리 메서드 통해서 받은 리턴타입)
            - 제공자 등록 API: 제공자가 구현체를 등록할때 사용하는 api
            - 서비스 접근 API: 클라이언트가 서비스의 인스턴스를 얻을때 사용하는 api (팩토리 메서드)
            - 서비스 제공자 인터페이스: 서비스 인터페이스의 인스턴스를 생성하는 팩터리 객체를 설명
              - => 서비스 인터페이스를 생성하는 로직들이 있기때문에 이렇게 이야기하는듯!
          - 서비스 제공자 프레임워크의 여러 변형들
            - 브릿지 패턴
            - 의존 객체 주입 프레임워크
            - ServiceLoader
          - ex. JDBC(Java Database Conectivity)
          ```java
                    Class.forName("com.mysql.cj.jdbc.Driver"); // 클래스 로드
                    /*
                    // 로드시 아래와 같은 초기화 진행

                    com.mysql.cj.jdbc.Driver 소스중 일부

                    static {
                        try {
                            java.sql.DriverManager.registerDriver(new Driver()); // 1
                        } catch (SQLException E) {
                            throw new RuntimeException("Can't register driver!");
                        }
                    }

                    ...

                    1. 여기서 DriverManager.registerDriver는 "제공자 등록 API"
                        그리고 자기자신의 인스턴스를 파라미터로 제공해주는데, 이 Driver가 "서비스 제공자 인터페이스"이며, 이때 만들어지는 Driver 구현체가 벤더사마다 만들어지게된다
                        그리고 Driver를 통해서 실질적인 Connection이 만들어지나, 사용자는 이 Driver를 직접 접근하거나 알 필요가없다.
                    */

                    Connection connection = DriverManager.getConnection("url", "user", "password");
                    /*
                    DriverManager.getConnection "서비스 접근 API".
                    Connection은 "서비스 인터페이스" 이다. Connection을 만들기위해서는 위의 Driver 구현체를 통해서 만들어지기때문에 각 벤더사의 Driver가 필수적이며,
                    여러 Driver들이 등록될 수 있기때문에 url에 따라 적절한 driver의 Connection을 전달해준다.
                    driver.connect() 를 통해서 Connection을 리턴해주는데, 인터페이스를 리턴해주기때문에, 내부적으로 하나의 Connection의 구현체들(SINGLE_CONNECTION 용, FAILOVER_CONNECTION 용, REPLICATION_CONNECTION 용)을 용도에 맞게 커넥션 구현체들을 리턴해준다

                    벤더사가 바로 Connection을 만들어서 등록할 수도 있겠지만(확장은 프록시 적절하게 사용하여..), Driver 라는 서비스 제공자 인터페이스를 둠으로써 서비스 제공자 프레임워크에서 필수적으로 필요로 하는 정보들을 정의하도록 할 수 있다. 프레임워크로써 동작하기수월하도록..
                    */

                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("select~~");

          ```
    - 단점
      - 정적 팩터리 메서드만 제공하면 하위클래스 생성불가 (생성자가 public이나 protected가 아니니깐..)
      - 정적 팩터리 메서드는 프로그래머가 찾기 어려움 (근데 이건 IDE 자동완성으로 커버가능할듯한데..)
        - 정적 팩터리 메서드에 흔히 사용하는 명명방식
          - `from` : 매개변수 하나 받아서 해당타입의 인스턴스 반환
          - `of` : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환
          - `valueOf` : `from`과 `of`의 더 자세한버전
          - `instance` 혹은 `getInstance` : 매개변수가 있다면 매개변수를 적절하게 사용한 인스턴스를 반환. 하지만 항상 같은 인스턴스임을 보장하지않는다 (같을수도 다를수도)
          - `create` 혹은 `newInstance` : 위와 같지만 항상 새로운 인스턴스 반환함을 보장
          - `get[Type]` : `getInstance` 와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 사용
            - `[Type]`은 팩터리 메서드가 반환할 객체의 타입
          - `new[Type]` : `newInstance` 와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 사용
          - `[Type]` : `get[Type]` 과 `new[Type]` 의 간결한 버전
      
  - 기타 팁
    - 메서드 시그니처
      - 메서드이름 + 파라미터
        - 리턴타입은 시그니처에 포함하지않는다!!
      - 메서드 시그니처를 기준으로 오버로딩이 된다
    - 플라이 웨이트 패턴?
      - 캐싱과 동일개념
      - 팩토리에서 인스턴스를 반환해줄때 팩토리에 저장되어있는 객체면 새로 생성하지않고, 기존에 저장되어있는것 반환. 그렇지않으면 새로생성하여 내부에 저장한뒤 반환
      - 참고사이트
        - https://velog.io/@hoit_98/%EB%94%94%EC%9E%90%EC%9D%B8-%ED%8C%A8%ED%84%B4-Flyweight-%ED%8C%A8%ED%84%B4
        - https://refactoring.guru/ko/design-patterns/flyweight
    - ServiceLoader?
      - https://velog.io/@adduci/Java-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%A1%9C%EB%8D%94ServiceLoader
    - 정적 팩터리를 사용하는게 유리한 경우가 많으므로 무작정 public 생성자를 제공하진말자!

---

- 아이템2_생성자에 매개변수가 많다면 빌더를 고려하라
  - 생성자 매개변수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다
    - 값의 의미가 헷갈리고, 매개변수도 몇개인지 주의해서 세어보아야.. 타입도 많으면 헷갈려서 버그유발쉬움
  - 자바 빈즈 패턴
    - setter로 넣는것
    - 읽기는 쉬움..
    - but 객체 하나를 만들기 위해서는 메서드를 여러개 호출해야하고, 객체가 완전히 생성되기 전까지 일관성이 무너진상태가됨..
    - 또한 setter가 있기때문에 불변으로 만들수 없음.. 그래서 스레드 안전성을 얻으려면 프로그래머가 추가작업필요..
  - 빌더 패턴
    - 안전성 + 가독성
    - 빌더로 생성 과정
      1. 필수 매개변수만으로 생성자(혹은 정적 팩터리 - 보통 이렇게 쓰이는듯)를 호출해 빌더 객체 얻음
      2. 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정 (보통 메서드체이닝방식)
      3. 매개변수가 없는 build 메서드를 호출해 필요한 객체를 얻는다
    - 유효성검사?
      - 잘못된 매개변수를 빨리 발견할 수 있도록 빌더의 생성자와 메서드에서 입력 매개변수를 검사
      - build 메서드가 호출하는 생성자에서는 여러 매개변수에 걸친 불변식을 검사
        - ex. startDate 와 endDate의 유효성검사 (startDate <= endDate)
    - 활용
      - 계층적으로 설계된 클래스에 좋음
      - 추상클래스엔 추상빌더, 구체 클래스에는 구체빌더
      - 빌더 하나로 여러 객테를 순회하면서 만들수있고, 빌더에 넘기는 매개변수에 따라서 다른 객체를 만들도록가능. 
    - 매개변수가 4개 이상은 되어야 활용하는게 좋음!
  - 생성자나 정적 팩터리가 처리해야할 매개변수가 많다면, 빌더 패턴을 선택하자! 특히 매개변수중 다수가 필수가 아니거나 같은 타입이라면 특히나!
  - 기타 팁
    - 불변과 불변식
      - 불변: 어떠한 변경도 허용하지않는다는 뜻
        - ex. String 객체
      - 불변식: 프로그램이 실행되는 동안, 혹은 정해진 기간동안 반드시 만족해야하는 조건
        - 변경을 허용할 수는 있으나, 주어진 조건 내에서만 허용
        - ex. Period 클래스에서 start 필드의 값은 반드시 end 필드의 값보다 앞서야하는것..
      - => 가변객체에도 불변식은 존재할 수 있다! 크게보면 불변은 불변식의 극단적 예
    - 공변반환 타이핑 (covariant return typing)
      - 하위 클래스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌, 그 하위 타입을 반환하는 기능
        - 아래 시뮬레이트한 셀프타입 관용구와 맥락을 같이하는듯함
      - 이를 통해서 클라이언트가 형변환에 신경쓰지않고도 빌더 사용가능 
    - self 타입이 없는 자바를 위한 우회방법을 `시뮬레이트한 셀프타입 관용구`라고함 (simulated self-type)
       
---

- 아이템3_private 생성자나 열거 타입으로 싱글턴임을 보증하라
  - 싱글턴: 인스턴스를 오직 하나만 생성할 수 있는 클래스
    - 함수와 같은 무상태 객체
      - ex?
    - 설계상 유일해야하는 시스템 컴포넌트
      - ex?
    - 클래스가 싱글턴으로 만들어지면 테스트가 어려워진다.. (mock으로 만들기가 여러우므로..)
    - 만드는방식?
      - `public static final` 필드 방식의 싱글턴 (정적 멤버가 필드)
      ```java
      class Elvis {
          public static final Elvis INSTANCE = new Elvis();
          private Elvis() {}
          public void doSomething() {
            //...
          }
      }
      ```
        - 간결하다! 
        - 싱글턴임이 명확하게 API에서 보여진다
      - 정적 팩토리 방식 (정적 멤버가 메서드)
      ```java
      class Tom { // 정적 팩토리 메서드를 제공
          private static final Tom INSTANCE = new Tom();
          private Tom() {}

          public static Tom getInstance() { return INSTANCE; }

          public void doSomething() {
              //...
          }
      }
      ```
        - 마음 바뀌면 코드 안바꾸고 싱글턴이 아니게 만들 수 있음
        - 정적 팩터리를 제네릭 싱글턴 팩토리로 만들수 있다는점
        - 정적 팩터리의 메서드 참조를 공급자로 사용가능
          - ex. `Supplier<Tom> = Tom::getInstance`
        - => 굳이 이런 장점이 필요하지않다면 첫번째로하라!
      - 열거타입 방식
      ```java
      enum Jeremy {
          INSTANCE;

          public void doSomething() {
              // ...
          }
      }
      ```
        - 매우 간결. 추가 노력없이 직렬화 가능
        - 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다함..
        - ***대부분 상황에서는 원소가 하나뿐인 열거타입이 싱글턴을 만드는 가장 좋은방법이라함!***
        - Mock은 어떤식으로 만드는게좋을까?
  - 기타 팁 
    - 만드는 방식 1번과 2번은 직렬화시에 아래와 같이 진행해야함..
      ```java
      class TomSerialization implements Serializable {
          private static final TomSerialization INSTANCE = new TomSerialization();
          private transient String instanceField; // 1
          private TomSerialization() {}

          public static TomSerialization getInstance() { return INSTANCE; }

          public void doSomething() {
              //...
          }

          private Object readResolve() { // 2
              return INSTANCE;
          }
      }

      1. transient필요.. 직렬화시에 제외해야할 변수에는 transient 사용
      2. 역직렬화할때 새로운 인스턴스가 계속 만들어진다 (readObject 가 내부적으로 역직렬화시에 계속 새로운 인스턴스를 만들어준다.    
        이를 해결하기위해서 readResolve 가 필요한데, 해당 메서드가 있으면 여기서 리턴해주는 값을 사용하게된다.     
        readObject를 통해서 만들어진 객체는 GC에 의해서 사라지게되고 결국 readResolve가 전달해준 인스턴스만 남게된다!
      ```
    - [객체 직렬화시 readResolve와 readObject](https://madplay.github.io/post/what-is-readresolve-method-and-writereplace-method)
    - 리플렉션 공격??

---

- 아이템4_인스턴스화를 막으려거든 private 생성자를 사용하라
  - 정적메서드와 정적 필드만을 담은 클래스를 만드는경우
    - 특정 인터페이스를 구현하는 객체를 생성해주는 정적 메서드(혹은 팩터리)를 모아놓기도함
      - ex. Collections
      - 자바 8부터는 인터페이스에 정적메서드를 활용하여 사용가능 (default와 다름.. default는 그냥 해당 인터페이스 기반 사용가능한 구현된 메서드)
    - final 클래스와 관련한 메서드들을 모아놓을때도 사용 (무슨말..?)
      - final 클래스는 상속불가..
  - **유틸성 클래스** 같은 경우 생성자를 private으로 두어, 인스턴스화를 막자!
    - 생성자 private이면 상속도 어려움~

---

- 아이템5_자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
  - 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면, 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다.
    - 해당 자원은 변경될 가능성이 있을수있고, 이에대해 의존객체주입(DI)는 클래스의 유연성, 재사용성, 테스트 용이성을 개선시켜준다!

---

- 아이템6_불필요한 객체 생성을 피하라
  - String을 생성할때 new 키워드를 사용하지말자 (리터럴을 사용해서 이미 만든적이있던 String이라면, string constant pool에서 가져오도록하자)
  - 생성자 대신 정적팩터리 메서드를 제공하는 불변 클래스에서는 내부적으로 캐싱을 사용해서 불필요한 객체 생성을 피하기도한다 (생성자는 호출할때마다 새로운 객체를 만들지만, 팩터리메서드는 그렇지않을수 있다!)
    - ex. `Boolean.valueOf(String)`
  - `String.matches` 메서드는 성능이 중요한 상황에서 반복해 사용하기엔 적합하지않다
    - 내부적으로 생성비용이 비싼 `Pattern` 인스턴스가 한번쓰고 버려지게됨..
    - `Pattern`은 클래스 초기화 과정에 캐싱해두어서 해당 인스턴스를 재사용하는것이 좋다!
  - 어뎁터와 같은 제2의 인터페이스 역할을 하는 객체는 뒷단 객체만 관리하면 되므로 접근시 굳이 계속해서 새로이 만들어줄 필요가 없다
    - 예를들어, `Map.keySet` 메서드를 호출할때, Map안의 모든 Key를 Set으로 반환해주는데, 여기서 만들어진 Set은 항상 새로이 만들어지는 인스턴스가 아니다. 여기의 Set은 어뎁터와 동일한데, Map의 Key들을 담아서 전달만 해준다. 만약, 해당 Key 객체를 변경한다면, 이를 사용하고잇는 모든 사용자는 영향을 받게된다.(모두 변경된다..) 그렇기때문에 굳이 Set을 새로이 인스턴스를 만들어서 다시 전달해주는게 의미가 없다
  - 오토박싱은 불필요하게 객체를 생성하는꼴이 될 수 있다
    - 오토박싱은 기본타입과 그에 대응하는 박싱된 기본타입의 구분을 흐려주지만, 완전히 없애주는것은 아니다
    - 박싱된 기본타입보다는, 기본타입을 사용하고, 의도치않은 오토박싱이 숨어들지 않도록주의!
      ```java
        private static long sum() {
          Long sum = 0L;

          for (long i = 0; i <= Integer.MAX_VALUE; i++>) {
            sum += i; // 계속 오토박싱.. 여기서로인해 Long 인스턴스가 2^31 개 생성됨..
          }

          return sum;
        }
      ```
  - 주의할점
    - ***프로그램의 명확성, 간결성, 기능을 위해서 객체를 추가로 생성하는것이라면 일반적으로 좋은일!!*** (controller -> service 단으로 넘어갈때 service단에서만 사용하는 커맨드 객체는 좋다고생각함..)
    - 방어적 복사를 통해서 필요한 상황에서 객체를 재사용했을때의 피해가 필요없는 객체를 반복 생성했을때의 피해보다 훨씬 클수있따!
      - 방어적 복사에 실패하면 언제 터져 나올지 모르는 버그와 보안구멍으로 이어지나, 불필요한 객체 생성은 그저 코드형태와 성능에만 영향..
  - 기타 팁
    - 불변객체는 언제든 재사용가능! 굳이 안만들어도되면 만들지말고 이미 만든거 사용하자



- 아이템7_다 쓴 객체 참조를 해제하라
  - 해당참조를 다 썼을떄 필요한 경우 null 처리를 하자! (null 처리를 하지않았을경우 메모리 누수가 날수있다면..)
  - 참조를 담은 변수를 유효범위 밖으로 밀어내어 알아서 GC가 회수해가도록 하는것이 제일 깔끔! 
    - 변수의 범위를 최소가되게 정의한다면, 이런일은 자연스럽게 이루어진다
    - ex. `for(int i = 0;i <5; i++)` 과 같은 루프에서 int i 는 변수의 범위가 매우 좁다. 즉, 참조가 없으니 알아서 잘 회수해간다
  - 자기 메모리를 직접 관리하는 클래스는 항상 메모리 누수 주의!!
    - `Stack` 에서 요소의 데이터들을 포함하고 있는 배열은 사용하지않으먄(ex. pop 호출되어 마지막 원소 필요없어짐) null처리는 필수적이다!
  - 캐시도 조심하자!
    - `WeakHashMap` 활용하자
      - 외부에서 키를 참조하고 있는 동안에만(값 아님) 엔트리가 살아있는 캐시로 사용가능
    - 엔트리의 가치를 떨어뜨리는 방식 활용하여 주기적으로 체크하여 오랫동안 사용안하는 엔트리는 제거하도록
      - 스케줄러활용 (`ScheduledThreadPoolExecutor`)
      - 새 엔트리를 추가할때 부수작업으로 수행하는 방법 
        ```java
            public class LinkedHashMap<K,V> extends HashMap<K,V> implements Map<K,V> {
                protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { // 1
                    return false;
                }
            }

            /*
            1. 해당 메서드를 오버라이딩해서 true를 리턴해주면, 가장 오래된 엔트리부터 지운다. 
            예를들어, 해당 메서드에 size() == 6 ? true : false 로 리턴해주면,
            가장 map의 size가 6이되면 가장 오래된 엔트리를 하나 지운다

            */
        ````
         
          
  - 리스너, 콜백 주의!
    - 클라이언트가 콜백만 등록하고, 명확하게 해지안해주면 콜백 계속 쌓임..
    - 약한 참조로 저장하자! ex. `WeakHashMap`의 key로 저장
      - ??? 잘 이해안감.. 결국 key를 null로 셋팅은 해주어야하는게 맞는건가??

  - 기타 팁
    - 힙 프로파일러를 활용해서 메모리 누수를 잠 감지하자!
    - [java reference와 GC](https://d2.naver.com/helloworld/329631)
      - 정리하자!
    - weakHashMap
      - JVM GC는 reference라는 참조방식에 따라 GC 하는 시점이 달라진다
        - strong refernce
        - soft reference
        - weak reference
          - weakHashMap
      - [내용설명 굿](https://blog.breakingthat.com/2018/08/26/java-collection-map-weakhashmap/)
      - [내용설명 굿](https://jake-seo-dev.tistory.com/11)