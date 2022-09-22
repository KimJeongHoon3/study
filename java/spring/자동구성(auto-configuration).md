자동구성(auto-configuration)

- auto-configuration 정리
  - `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 의 `resources/META-INF/spring.factories` 여기서 등록된 자동구성을 살펴볼수있음
    - 자동구성 순서도 지정할수있음 `@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)`
  - `@Conditional` 어노테이션이 기반이되는데, 이를 활용해서 사용자가 자둥구성에 있는 빈을 직접등록하지 않을때 효과발생! 직접 등록하면 해당빈이 등록.. 

  - 참고사이트
    - https://www.baeldung.com/spring-boot-custom-auto-configuration#:~:text=Simply%20put%2C%20the%20Spring%20Boot,in%20the%20auto%2Dconfiguration%20classes.
    - [자동구성에서 사용되는 어노테이션(@Conditional 등 관련) 설명 굿](http://dveamer.github.io/backend/SpringBootAutoConfiguration.html)
    - [autoConfiguration 만들기](https://donghyeon.dev/spring/2020/08/01/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8%EC%9D%98-AutoConfiguration%EC%9D%98-%EC%9B%90%EB%A6%AC-%EB%B0%8F-%EB%A7%8C%EB%93%A4%EC%96%B4-%EB%B3%B4%EA%B8%B0/)