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

- 아이템3_
  - 기타 팁 
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