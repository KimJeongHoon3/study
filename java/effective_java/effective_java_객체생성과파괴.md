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
      - 

- 아이템7_
  - 기타 팁
    - weakHashMap
      - JVM GC는 reference라는 참조방식에 따라 GC 하는 시점이 달라진다
        - strong refernce
        - soft reference
        - weak reference
          - weakHashMap
      - [내용설명 굿](https://blog.breakingthat.com/2018/08/26/java-collection-map-weakhashmap/)
      - [내용설명 굿](https://jake-seo-dev.tistory.com/11)