자동구성(auto-configuration)
- 간단설명
  - 클래스 패스에 적절한 jar 가 있다면, 별도로 bean을 생성하지않더라도 알아서 생성되는것! (물론, 자동구성 셋팅이 되어있어야겠지.. 근데 그걸 기본적으로 spring boot가 해줬다는것..)
  - `@EnableAutoConfiguration` 이 필요한데(`@Configuration`과 함께씀..), `@SpringBootApplication`에 포함되어있기때문에 둘중에 하나만 있으면됨
  - 자동구성은 default를 잡아주는것뿐, 실행하는 프로젝트에 사용자가 정의해놓은 bean이 있으면 그걸 우선사용하게됨
  - 물론 자동구성셋팅하기 싫으면 제외도 가능하다
  - https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.auto-configuration
- mvc에서의 자동구성
  - https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.auto-configuration

- 자동구성 만들어보기
  - `@AutoConfiguration` 은 자동구성으로 셋팅해놓은것들을 `@Configuration`과 같이 만들어줌..(해석이 이게맞나..? 무튼 설정어노테이션과 동일..)
  - 이는 보통 `@ConditionalOnClass`, `@ConditionalOnMissingBean` 과 같이씀
  - 2.7.3 기준 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 여기서 자동구성 확인가능 (한줄에 한 클래스)
  - componentscan 대상이 되면안됨.. 
  - `@AutoConfiguration`의 속성의 befor, beforeName, after, afterName 속성으로 순서지정가능
    - `@AutoConfigureBefore`, `@AutoConfigureAfter` 도 사용가능
    - `@AutoConfigureOrder` 으로도 순서 지정가능
- Condition 어노테이션
  - Class Conditions
  - Bean Conditions
  - Property Conditions
  - Resource Conditions
  - Web Application Conditions
  - SpEL Expression Conditions
- 테스트 가능
    ```java
      private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
              .withConfiguration(AutoConfigurations.of(MyServiceAutoConfiguration.class));
    ```
    - webContext일때는 `WebApplicationContextRunner` or `ReactiveWebApplicationContextRunner`
    - 

- auto-configuration 정리
  - `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 의 `resources/META-INF/spring.factories` 여기서 등록된 자동구성을 살펴볼수있음
    - 자동구성 순서도 지정할수있음 `@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)`
  - `@Conditional` 어노테이션이 기반이되는데, 이를 활용해서 사용자가 자둥구성에 있는 빈을 직접등록하지 않을때 효과발생! 직접 등록하면 해당빈이 등록.. 

  - 참고사이트
    - https://www.baeldung.com/spring-boot-custom-auto-configuration#:~:text=Simply%20put%2C%20the%20Spring%20Boot,in%20the%20auto%2Dconfiguration%20classes.
    - [자동구성에서 사용되는 어노테이션(@Conditional 등 관련) 설명 굿](http://dveamer.github.io/backend/SpringBootAutoConfiguration.html)
    - [autoConfiguration 만들기](https://donghyeon.dev/spring/2020/08/01/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8%EC%9D%98-AutoConfiguration%EC%9D%98-%EC%9B%90%EB%A6%AC-%EB%B0%8F-%EB%A7%8C%EB%93%A4%EC%96%B4-%EB%B3%B4%EA%B8%B0/)


---

- [2.7.10 기준 자동구성 만들기](https://docs.spring.io/spring-boot/docs/2.7.10/reference/htmlsingle/#features.developing-auto-configuration)
  - 7.9.1. Understanding Auto-configured Beans
    - 자동 구성을 위한 클래스는 반드시 @AutoConfiguration 이 붙어야함
      - @AutoConfiguration는 메타어노테이션이며 @Configuration도 가지고있음
    - 자동구성이 적용해야할 때를 지정하기위해서 @Conditional을 함께 사용하여 제약을 둘 수 있음
    - 보통 자동구성 클래스들은 @ConditionalOnClass과 @ConditionalOnMissingBean 을 함께 사용
    - 말 그대로 자동구성은 사용자가 직접 @Configuration으로 셋팅하지않거나 관련 클래스가 발견될때만 적용되도록 자동 구성할 수 잇는 것
    - spring-boot-autoconfigure 프로젝트는 @AutoConfiguration 클래스를 찾아서 자동구성 해준다 (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 이 위치에 자동구성 클래스들이 정리되어있다)
      - 즉, spring-boot-autoconfigure에서 .imports 파일에 저장된 자동구성 클래스들을 모두 읽어와서 적용시켜준다
      - AutoConfigurationImportSelector.process 에서 해당 파일을 읽는 작업수행
  - 7.9.2. Locating Auto-configuration Candidates
    - `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 이렇게 파일을 생성하고 아래와 같이 자동구성클래스를 추가
      ```
        com.mycorp.libx.autoconfigure.LibXAutoConfiguration
        com.mycorp.libx.autoconfigure.LibXWebAutoConfiguration
      ```
    - 위와 같이 자동구성을 위한 클래스는 **컴포넌트 스캔 대상이 되면안된다.**
      - Specific `@Imports` should be used 
    - 순서 지정도 가능
      - @AutoConfiguraion의 속성으로 정의가능 
        - before, beforeName, after and afterName attributes on the @AutoConfiguration annotation 
      - 명시적으로 선언도 가능
        - @AutoConfigureBefore and @AutoConfigureAfter annotations.
      - 특정 클래스 다음에 순서를 지정하는 그런게 아니라면 @AutoConfigureOrder 사용하면됨


  - 7.9.3. Condition Annotations
    - @AutoConfiguration은 사용자 정의된 빈이 추가된 후에 로드되도록 보장되기떄문에, 특정 빈이 있는지 확인하는 조건 애너테이션인 @ConditionalOnBean 및 @ConditionalOnMissingBean 어노테이션만 사용하는 것이 좋다
    - 클래스레벨에서 선언된 @Condition~~ 은 아예 @Configuration이 만들어지는것을 막아주나, 메서드에 선언된 @Condition~~ 은 @Configuration으로 클래스가 생성되는걸 막지못한다
    - @Bean 선언시 리턴타입을 구체 클래스로.. 인터페이스x