스프링부트
- 제품수준의 어플리케이션을 만들때, 빠르고 쉽게 만들수있도록 도와준다 
- 여러 중요하다고 생각되는 기본설정들을 제공해줌(스프링, 3rd-party(톰켓, kafka..))
   - 당연 수정도 쉽게!
- xml 더이상 안쓰고, 코드 generation 하지않음!

의존성 관리 이해
- 각 라이브러리의 버전관리를 따로 안해도됨
- maven에서는 parent로 spring-boot-starter-parent를 셋팅하게되는데, 이때 spring-boot-starter-parent에는 의존성 라이브러리 뿐아니라 resource와 같은 spring 사용하는데 필요한 설정들또한 셋팅이 미리 되어있다.. ex. application.yml, 인코딩을 UTF-8로.. 등
- maven은 버전과같은 특정 설정을 변경하고싶을때, 상속받은곳에서(최하위 즉, pom.xml) 오버라이딩을 하면됨! 

자동설정이해
- EnableAutoConfiguration (@SpringBootApplication 안에 숨어 있음)
- 빈은 사실 두 단계로 나눠서 읽힘
  - 1단계: @ComponentScan 
    - @Component라고 셋팅된 놈들 빈으로 등록 
   - @Configuration, @Repositroy, @Service, @Controller, @RestController
  - 2단계: @EnableAutoConfiguration
    - 추가적으로 등록해할 빈들을 여기서 만들어줌.. ServletWebServerFactory과 같은 web에 필요한것들도 이때에 만들어줌
   - spring.factories
   - @Configuration
   - @ConditionalOnXxxYyyZzz : 특정조건(XxxYyyZzz)에 따라 bean을 셋팅하는것 
   ex. @ConditionalOnWebApplication(type = Type.SERVLET)
     => Type이 SERVLET일때 만들어질것!
   @ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
     => WebMvcConfigurationSupport.class가 없을때 만들어줄것!


SpringApplication springApplication=new SpringApplication(SpringBootStartedApplication.class);
springApplication.setWebApplicationType(WebApplicationType.NONE);
springApplication.run(args);


9강 & 10강 테스트하고 내용정리
- ***gradle로 하려니 안됨.. 계속 import 경로를 가져오지못함..***
- @ConfigurationProperties("prefix")
  - 설정을 읽어올 클래스에 해당 어노테이션을 명시하면, 설정파일의 prefix와 동일한놈을 가져와 변수들에 매칭시켜준다
- @EnableConfigurationProperties(XX.class)  //XX.class는 @ConfigurationProperties를 통해 셋팅한 클래스
  - 이를 통해서 설정을 셋팅한 객체들을 가져와 사용할수있음!
- 라이브러리 파일로 만들때 @ConditionalOnMissingBean을 사용하여 디볼트 bean을 만들어줄수있음!(어떤 bean도 없을때 생성하라는 뜻이므로 새로운 bean이 만들어지면 그 bean을 사용하고 그냥 바로 autowired를 사용하면 요기에 등록된 bean이 사용됨)


11강 내장 서블릿 컨테이너
- 스프링 부트는 웹서버가아니라, 웹서버를 쉽게 사용할수있도록 도와주는 툴이다
- 웹 어플리케이션을 실행하기위해서는 아래와 같은 절차가 필요하다
   1. 톰캣 객체 생성
   2. 포트 설정
   3. 톰캣에 컨텍스트 추가 
   4. 서블릿 생성
   5. 톰캣에 서블릿 추가 
   6. 컨텍스트에 서블릿 맵핑
   7. 톰캣 실행 및 대기
   - => 이 과정을 유연하게 설정하고 실행해주는게 스프링부트의 자동설정.(위에서했던 방식과같음)
     - ServletWebServerFactoryAutoConfiguration
       - 서블릿 컨테이너에는 여러개가 사용될수있음. 즉, tomcat, jetty, undertow와 같이 변경이 될수있음!
       - 그러나 DispatcherServlet은 고정되어있기때문에 ServletWebServerFactoryAutoConfiguration와 DispatcherServletAutoConfiguration 는 분리가 되어있음!
       - 어노테이션 달려있는것을 보면, DispatcherServletAutoConfiguration에 @AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class) 가 명시되어있으니 ServletWebServerFactoryAutoConfiguration 이후에 셋팅되라는것임!
       - 서버커스터마이징을 위한 클래스도있음!(TomcatServletWebServerFactoryCustomizer)
     - DispatcherServletAutoConfiguration
       - 서블릿 만들고 등록

```java
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(ServletRequest.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ServerProperties.class)
@Import({ ServletWebServerFactoryAutoConfiguration.BeanPostProcessorsRegistrar.class,
		ServletWebServerFactoryConfiguration.EmbeddedTomcat.class,
		ServletWebServerFactoryConfiguration.EmbeddedJetty.class,
		ServletWebServerFactoryConfiguration.EmbeddedUndertow.class })
public class ServletWebServerFactoryAutoConfiguration { ... }

@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
public class DispatcherServletAutoConfiguration { ... }

```

13 내장 웹 서버 응용 1부 : 컨테이너와 서버포트

- https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-web-servers.html


spring boot feature docs : https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-spring-application


SpringApplication 2부
- Application Events and Listners
  - Application events are sent in the following order, as your application runs:
     1. An ApplicationStartingEvent is sent at the start of a run but before any processing, except for the registration of listeners and initializers.
     2. An ApplicationEnvironmentPreparedEvent is sent when the Environment to be used in the context is known but before the context is created.
     3. An ApplicationContextInitializedEvent is sent when the ApplicationContext is prepared and ApplicationContextInitializers have been called but before any bean definitions are loaded.
     4. An ApplicationPreparedEvent is sent just before the refresh is started but after bean definitions have been loaded.

     5. An ApplicationStartedEvent is sent after the context has been refreshed but before any application and command-line runners have been called.

     6. An AvailabilityChangeEvent is sent right after with LivenessState.CORRECT to indicate that the application is considered as live.

     7. An ApplicationReadyEvent is sent after any application and command-line runners have been called.

     8. An AvailabilityChangeEvent is sent right after with ReadinessState.ACCEPTING_TRAFFIC to indicate that the application is ready to service requests.

     9. An ApplicationFailedEvent is sent if there is an exception on startup.

  - The above list only includes SpringApplicationEvents that are tied to a SpringApplication. In addition to these, the following events are also published after ApplicationPreparedEvent and before ApplicationStartedEvent:
     1. A WebServerInitializedEvent is sent after the WebServer is ready. ServletWebServerInitializedEvent and ReactiveWebServerInitializedEvent are the servlet and reactive variants respectively.
     2. A ContextRefreshedEvent is sent when an ApplicationContext is refreshed.



외부설정 1부
   - https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config 
   - 프로퍼티 우선 순위
      1. 유저 홈 디렉토리에 있는 spring-boot-dev-tools.properties
      2. 테스트에 있는 @TestPropertySource // 테스트코드 짤때는 이를 꼭 활용할것! 이렇게 해야 필요한내용만 오버라이딩 하기도 편하고 테스트할때만 사용하기좋음! 물론, application.properties로 이름만들면안댐! 다른 이름으로!! 이는, 컴파일할때 application.properties가 main 소스 컴파일 이후 test 소스 컴파일시 application.properties가 오버라이딩 되는 상황이 연출되기때문에 만약, test/resources의 application.properties의 key값이 main/resources에 없는값이면 컴파일 에러가 계속남..(근데 실제적으로 resources폴더 가보면 main과 test의 application.properties가 나눠져있는데 왜그러는지모르겠음..) 암튼 귀찮게 계속 맞춰주고싶지않으면, 그냥 TEST소스에는 @TestPropertySource(location="classpath:/test.properties) 이런식으로 써주면됨  
      3. @SpringBootTest 애노테이션의 properties 애트리뷰트
      4. 커맨드 라인 아규먼트
      5. SPRING_APPLICATION_JSON (환경 변수 또는 시스템 프로티) 에 들어있는 프로퍼티
      6. ServletConfig 파라미터
      7. ServletContext 파라미터
      8. java:comp/env JNDI 애트리뷰트
      9. System.getProperties() 자바 시스템 프로퍼티
      10. OS 환경 변수
      11. RandomValuePropertySource
      12. JAR 밖에 있는 특정 프로파일용 application properties
      13. JAR 안에 있는 특정 프로파일용 application properties
      14. JAR 밖에 있는 application properties
      15. JAR 안에 있는 application properties
      16. @PropertySource
      17. 기본 프로퍼티 (SpringApplication.setDefaultProperties)

   - application.properties 우선 순위 (높은게 낮은걸 덮어 씁니다.)
      1. file:./config/
      2. file:./
      3. classpath:/config/
      4. classpath:/


---

- 스프링 기타 이모저모
  - @Configuration 와 @Component로 bean등록하는 차이점
    - @Configuration 으로 bean을 등록하면 CGLIB가 사용되어(프록시) 메소드 호출은 1회만 일어나도록 바이트 코드가 수정된다.. 즉, @Configuration 안의 메소드를 어디서 호출해도 오직 한번만 호출되는것! 그래서 보통 함수로 bean 등록을 하고 해당 메소드를 또 호출하여 생성자로 넘겨주는것이 가능!
    - 이런 @Configuration에 proxyBeanMethods란 값을 false로 주면(default가 true임) Cglib를 통한 메소드 프록시가 적용되지않아서 해당 메소드를 호출하면 호출하는 족족 실행된다. 이게 @Component로 bean 만들었을때와 동일하다! 이를 Lite Mode 라고 한다 
      - Lite Mode는 Cglib를 사용하여 바이트 코드 조작을 하지않음을 의미
    - 참고사이트 : https://hyojabal.tistory.com/25
  - @Import
    - @Configuration으로 등록된 클래스를 하나이상 가져오도록해줌 
  - @ConditionalOnBean(XXX.class)
    - XXX 타입의 클래스의 bean이 factory에 등록되어있을때 생성하라
  - @ConditionalOnMissingBean(XXX.class)
    - XXX 타입의 클래스의 bean이 factory에 등록되어있지않을때 생성하라
    - bean 이름을 명시해줄수도있음! 즉, 해당 bean이름이 없을때 생성!

  - @RestController
    - 사용자 요청에 Data를 응답
    - @Controller + @ResponseBody
       -@ResponseBody 를 통해서 사용자에게 json과같은 데이터를 return 해줄수있음
    - 데이터 반환을 위한 HttpMessageConverter 가 동작.. 변환해야 하는 데이터에 따라 사용되는 Converter는 달라짐!
       - 문자열 : StringHttpMessageConverter
       - 객체 : MappingJackson2HttpMessageConverter
       - Spring은 클라이언트의 HTTP Accept 헤더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해 적합한 HttpMessageConverter를 선택하여 이를 처리합니다.

  - @Controller
    - 사용자 요청에 HTML 파일을 응답
    - 만약 사용자에게 json과 같은 data를 응답해줘야한다면 리턴타입에 @ResponseBody 라는 어노테이션을 명시해줘야함 

  - @RestController와 @Controller에 대해 예제와함께 잘 설명 : https://mangkyu.tistory.com/49

  - get 요청외에는 브라우저에서 불가능!(post,put,delete)
        
  - @RequestBody
    - Post와같은 방식에서 body데이터 받을때 사용하는 어노테이션 
    - json 타입으로 보낼때 객체로 파싱해줌(MessageConverter의 역할!(springboot))

  - @RequestParam
    - Get방식과 같은 url에서 ?뒤에 보내는 데이터를 받을때 사용하는 어노테이션 

      