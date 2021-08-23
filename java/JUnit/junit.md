- junit
  - 테스팅 프레임워크
  - 구성
    - Jupiter : TestEngine API 구현체로 JUnit5 제공
      - The JUnit Platform serves as a foundation for launching testing frameworks on the JVM. It also defines the TestEngine API for developing a testing framework that runs on the platform. Furthermore, the platform provides a Console Launcher to launch the platform from the command line and a JUnit 4 based Runner for running any TestEngine on the platform in a JUnit 4 based environment. First-class support for the JUnit Platform also exists in popular IDEs (see IntelliJ IDEA, Eclipse, NetBeans, and Visual Studio Code) and build tools (see Gradle, Maven, and Ant).
      - 출처 : https://junit.org/junit5/docs/current/user-guide/
    - Vintage : JUnit4, 3 지원하는 TestEngine 구현체(옛날꺼 쓸수있도록 해주는거..)
    - JUnit Platform : 테스트 실행해주는 런처 제공
  - 문법
    - 커맨드 + shift + t : 테스트 원하는 클래스에서 실행시 테스트 클래스 생성해줌
    - @TEST : 테스트 진행
    - @BeforeAll / @AfterAll : 모든 TEST 실행전 / 모든 TEST 완료후 (최초에 한번만) => 반드시 static 이어야함 
    - @BeforeEach / @AfterEach : 각각의 TEST 실행전 / 각각의 TEST 실행후 (@TEST 있는것마다 모두 실행)
    - @Disabled : 테스트 실행못하도록함
    - 테스트 이름표시하기
      - @DisplayNameGeneration
        - Method와 클래스의 레퍼런스를 사용해서 테스트 이름 표기하는 방법 설정
        - 기본 구현체는 ReplaceUnderscores (언더바를 공백으로 변경시켜줌)
      - @DisplayName
        - 어떤 테스트인지 테스트 이름을 보다 쉽게 표현할수 있음. 한글가능
        - Intellij에서 실행안될때
          - http://jmlim.github.io/intellij/2020/03/02/intellij-junit5-display-name-did-not-show-issue/
      - 참고
        - https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-names
    - @Assertion
      - @assertEquals(expected,actual,"에러시 보여줄 메세지") 
      - @assertEquals(excpeted,actual,()->"에러시 보여줄 메세지")
        - 람다식으로 사용가능
        - 이를 사용하면 에러 발생시에 비로소 메세지 생성해줌. 
      - @assertTrue(조건문, "에러시 보여줄 메세지")
      - @assertAll(executables...)
        - @Test 실행시 실행중에 에러나면 다음 테스트 진행이 안되는데, 이를 사용하여 람다로 전달해주면(Executable 구현..) 에러나는것 상관없이 모든 테스트 진행되어 한번에 문제있는것을 모두 파악가능
      - @assertThrows(expectedType, executable)
        - 리턴값이 expectedType(여기서는 에러 타입)이 되어, 해당 에러의 e.getMessage()와 같이 에러 메세지도 확인 가능
      - @assertTimeout(Duration, executable)
        - Duration.ofXX() 동안 executable이 실행안되면 에러
        - @assertTimeoutPreemptively()
          - 지정한 Duration 넘으면 즉각 종료..
          - 그냥 assertTimeout은 executable 실행이끝날때까지 기다림
          - 하지만 이는 주의가필요.. executable은 별도의 스레드로 실행되는데, ThreadLocal을 사용하는 spring tranaction은 기대와 다른 행동을 보일수있음.. 예륻들면, 문제났는데 롤백이 안되는거.. (assertTimeout은 별도의 스레드에서 실행되지않음!!)
      - 조건에 따라 테스트 실행하기
        - @assumeTrue(조건)
          - 시스템환경변수 중에 맞는지를 확인해서 특정 OS나 환경에 따라서 실행하도록 해줄수있음
        - @assumingThat(조건,"테스트")
          - 조건에 맞을때 "테스트" 실행하도록
        - @EnbaledOnOs({OS.xx,Os.xx})
        - @DisaledOnOs({OS.xx,Os.xx})
        - @EnabledOnJre({JRE.xx, JRE.xx ...})
        - @EnabledIfEnvironmentVariable(named ="변수이름" , matches ="정규식가능" )
      - 태깅과 필터링
        - @Tag("태그명")
          - "태그명" 에 따라 테스트 필터링 가능
          - 하나의 테스트 메소드에 여러태그 사용가능
      - 커스텀 태그
        - 어노테이션을 사용하여 커스텀 태그 만들수있음
        - 예시
          - @Target(ElementType.METHOD)<br>
  @Retention(RetentionPolicy.RUNTIME)<br>
  @Tag("fast")<br>
  @Test<br>
  public @interface FastTest {<br>
  }
          - @FastTest 만 붙여주면됨

      - 테스트 반복하기
        - @RepeatedTest
          - 그냥 같은 값으로 반복
        - @ParameterizedTest 
          - 여러 다른 값을 가지고 테스트하고 싶을때(값 전달해주는 반복테스트)
          - @ValuesSource
            - 어떤 값을 받을수잇는지 제공가능
          - 예시
            - @DisplayName{"예시"}<br>@ParameterizedTest(name = "{index} {displayName} message={0}")<br>@ValueSource(strings={"a","b","c","d"})<br>void 함수명(String message){<br> &emsp; sout(message);<br>}
            - 결과(아래는 테스트이름으로 나오는거.. )
              - 0 예시 message=a
              - 1 예시 message=b
              - ...
              - => {index} 는 ValueSource로 부터 가져오는 index
              - => {displayName} 은 @DisplayName에서 사용된 값
              - => {0} 은 함수의 첫번째 파라미터값
        - 인자 값들의 소스
          - @ValueSource (위 참조)
          - @NullSoruce, @EmptySource, @NullAndEmptySource
            - null 혹은 빈값
          - @CsvSource
            - comma 구분자로 테스트 대상의 함수에서 여러개의 인자를 받을수있음
          - 암묵적 타입변환 가능
          - 명시적 타입변환 또한 가능
            - SpringArgumentConverter / @ConverWith 로 명시
            - ArgumentsAggregator / @AggregateWith 로 명시
          - 참고 : https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests

      - 테스트 인스턴스
        - 각각의 Test 메소드 실행시 해당 클래스를 새로이 만들어서 실행..
          - 테스트간 의존성을 줄이기위해!
        - 그러나, 이를 공유하는 방법이 있음
          - @TestInstance(Lifecycle.PER_CLASS)
          - 이때, @BeforeAll 과 @AfterAll 이 반드시 static이 아니여도댐
      - 테스트 순서
        - 단위테스트는 순서에 의존하면안된다. 의도적으로 순서를 지정해야하는게 아니라면, 각각의 테스트를 만들때 순서대로 실행해야 정상동작하도록 만들면안된다!
        - 순서를 지정하고싶다면 아래와 같이 사용할것
        - @TestMethodOrder(MethodOrderer.OrderAnnotation.class) 를 클래스 위에 선언 // MethodOrderer, Alphanumeric, Random 사용가능
        - @Order("숫자") 를 각각의 TEST 메소드에 선언
      - junit-platform.properties
        - junit 설정파일로, "src/test/resources/" 에 넣어주면됨 
          - intellij에서 resources 디렉토리를 Test Resource로 인지하도록 하기위해서 " project structure => Modules => 해당 디렉토리를 Test Resource로 지정 " 해야함

      - 확장모델
        - TEST시 확장모델을 지정한 클래스에 한해서 확장모델이 어떤 역할을 하느냐에 따라서 특정 행동을 하도록 정의할수있음..
        - 확장모델 등록방법
          - 클래스 위에 @ExtendWith
            - 생성자에 파라미터 없을때에만 사용가능(config도 동적으로 생성은 안되겟지..?)
          - 전역변수에 등록 @RegisterExtension
            - new 키워드 또는 빌더를 사용하여 등록. 즉, 직접 생성해주고 어노테이션을 붙이라는것!
            - 그렇기떄문에 생성자에 파라미터가있어도 가능
        - 참고 : https://junit.org/junit5/docs/current/user-guide/#extensions  

---

- Mockito
  - Mock을 사용하도록 해주는 프레임워크
  - Mock : 진짜 객체와 비슷하게 동작하지만, 프로그래머가 직접 그 객체의 행동을 관리하는 객체
    - 찐은 아님.. 그래서 사용할때 Mock으로 만든 객체의 행동을 사용해야하는부분이있다면 따로 어떻게 동작할지를 정의해야줘야함
  - 다음 세가지를 꼭 알아둘것
    1. Mock을 만드는 방법
    2. Mock이 어떻게 동작해야 하는지 관리하는 방법
    3. Mock의 행동을 검증하는 방법
    - 레퍼런스 : https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html 

  - Mock 객체만들기
    - Mockito.mock() 메소드로 만드는 방법
      - > MemberService memberService = mock(MemberService.class);<br>
        StudyRepository studyRepository = mock(StudyRepository.class);
    - @Mock 애노테이션으로 만드는 방법
      - MokitoExtention을 사용하여야함(@ExtendWith)
      - >@ExtendWith(MockitoExtension.class)<br>
    class StudyServiceTest {<br>
    &emsp; @Mock MemberService memberService;<br>
    &emsp; @Mock StudyRepository studyRepository;<br>
    }
      - >@ExtendWith(MockitoExtension.class)<br>
    class StudyServiceTest {<br>
    @Test<br>
    void createStudyService(@Mock MemberService memberService,
                            @Mock StudyRepository studyRepository) { <br>
        &emsp;StudyService studyService = new StudyService(memberService, studyRepository);<br>
        &emsp;assertNotNull(studyService);<br>
    }

  - Mock 객체 Stubbing
    - stubbing이란 Mock객체에 행동을 집어넣어주는것
      - 기대 행위를 작성하는것을 Stub
    - Mock 객체의 default 행동
      - Null 리턴 (Optional 타입은 Optional.empty 리턴)
      - Primitive 타입은 기본 Primitive 값
      - 콜렉션은 비어있는 콜렉션
      - Void 메소드는 예외를 던지지않고 아무런 일도 발생하지않는다
    - 사용예시
      - > Member member=new Member();<br>
      memeber.setId("hi");<br>
      member.setEmail("hihi@gmail.com");<br>
      when(service.findById("hi")).thenReturn(member); //stubbing <br>
      - => 이렇게 되면 service.findById("hi") 를 호출하면 member객체를 리턴해준다는것!
    - argumentMatcher를 통해 특정 매개변수만을 받는게 아닌, 범용적으로 사용할수 있도록 만들수있음
      - 참고사이트 : https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#3
    - Mockito.when ~~ do
    - stubbing에 동일한 메소드를 여러번 호출할때 각각 다르게 행동하도록 조작가능
      - > when(호출할 메소드).thenReturn() <br>
      &emsp;.thenReturn(member)<br>
      &emsp;.thenThrow(new RuntimeException)<br>
      &emsp;.thenReturn(null)<br>
      - => "호출할메소드"를 처음호출하면 member객체 반환 , 두번째는 에러반환 ...

  - Mock 객체 확인
    - Mock 객체가 어떻게 사용됐는지 확인가능
      - 특정 메소드 얼마나 호출되었는지
        - https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#exact_verification
      - 어떤 순서대로 호출되었는지
        - https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#in_order_verification
      - 특정 시점이후 아무일 일어나지않았는지

  - Mockito BDD 스타일 API
    - BDD(Behavior Driven Development): 어플리케이션이 어떻게 "행동"해야 하는지에 대한 공통된 이해를 구성하는 방법.. 
    - 행동에 대한 스펙
      - title
      - narrative
      - 주어진 기준
        - given : 주어진 상황
          - > given(memberService.findById(1L)).willReturn(Optional.of(member));<br>
              given(studyRepository.save(study)).willReturn(study);
        - when : 실제적인 행동
          - > service.doSomething(param1,param2);
        - then : 결과
          - > then(memberService).should(times(1)).notify(study);<br>
              then(memberService).shouldHaveNoMoreInteractions();
        - => 이들 모두는 BddMockito 라는 클래스를 통해서 BDD 스타일의 API를 사용하는것임!
        - 참고
          - https://javadoc.io/static/org.mockito/mockito-core/3.2.0/org/mockito/BDDMockito.html
          -	https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#BDD_behavior_verification
          - Mockito 여러 기능 설명 굿(@Spy, @InjectMocks ..) : https://sun-22.tistory.com/94
          - Mockito 내용정리 굿 : https://jdm.kr/blog/222

- Junit or Mockito 사용관련 참고사항
  - SPY
    - 특정 메소드만 mocking 가능
    ```java
      will(invocation -> messageResponse).given(uplusSender).doSend(any()); //messageResponse가 uplusSender.doSend 호출시 전달받을 값.. 이를 사용하기위해서는 @Spy로 uplusSender를 만들어야함
    ```
  - @SpyBean 사용시 유의사항
    - @SpyBean이 Interface일 경우에는 해당 Interface를 구현하는 실제 구현체가 꼭 스프링 컨텍스트에 등록되어 있어야 합니다.
  - @MockBean으로는 @InjectMocks 으로는 주입안되는 이유
    - @InjectMocks는 Mockito에 의해서 클래스가 초기화될때 해당 클래스안에서 정의된 mock객체를 찾아서 의존성을 해결합니다. 즉, @InjectMocks는 @Mock으로 스프링 컨텍스트에 등록된 mock 객체는 찾을 수 없습니다.
    - 하지만 @MockBean은 mock 객체를 스프링 컨텍스트에 등록하는 것이기 때문에 당연히 @InjectMocks가 찾을 수 없게 됩니다. @MockBean은 @Autowired처럼 스프링이 제공하는 의존성 해결방법으로만 mock객체를 찾을 수 있게 됩니다.
  - 참고사이트 
    - [mockito관련 기본적인 설명 매우 굿.. 적용한것도 잘나와잇음](https://cobbybb.tistory.com/16)