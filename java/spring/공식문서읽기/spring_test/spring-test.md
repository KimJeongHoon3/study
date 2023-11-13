spring-test

- [@WebMvcTest](https://docs.spring.io/spring-boot/docs/2.7.10/reference/htmlsingle/#features.testing.spring-boot-applications.spring-mvc-tests)
  - @WebMvcTest 는 MVC 인프라 스트럭쳐와 @Controller, @ControllerAdvice, @JsonComponent, Converter, GenericConverter, Filter, HandlerInterceptor, WebMvcConfigurer, WebMvcRegistrations, and HandlerMethodArgumentResolver 를 구현된 빈들만 제한적으로 자동구성해줌
  - thymeleaf와 같은 view에 대한 검증이 굳이 필요없다면, ThymeleafAutoConfiguration를 exclude 해버리면됨
    ```java
        @WebMvcTest(value = {UserController.class}
        , excludeAutoConfiguration = ThymeleafAutoConfiguration.class)
        public class UserControllerTest { 
            // test
        }
    ```
  - 추가적으로 필요한 빈들이 있다면 아래와 같이 설정 (여러가지방법이 있지만, 부트에서는 아래가 제일 깔끔한듯)
    ```java
        @WebMvcTest(value = {UserController.class}
        , excludeAutoConfiguration = ThymeleafAutoConfiguration.class)
        public class UserControllerTest { 

            @TestConfiguration(proxyBeanMethods = false)
            static class Config { // inner class로.. 아래 빈들은 자동구성으로 못찾은 대상
                @Bean
                CustomRequestInterceptor customRequestInterceptor() {
                    return new CustomRequestInterceptor();
                }

                @Bean
                MyDeferredResultInterceptor myDeferredResultInterceptor() {
                    return new MyDeferredResultInterceptor();
                }
            }
        }
        
    ```
  - 필터 적용해야한다면, `@AutoConfigureMockMvc()` 요거를 선언하여 filter class 등록하면됨


- [spring boot test](https://docs.spring.io/spring-boot/docs/2.7.10/reference/htmlsingle/#features.testing)
- [spring integration testing](https://docs.spring.io/spring-framework/docs/5.3.26/reference/html/testing.html#integration-testing)