openfeign + loadbalancer + eureka

- openfeign
  - 선언적 rest client를 사용할수있도록 해줌 (어노테이션기반..)
    - Feign is a declarative web service client.
  - Spring Cloud는 Spring MVC 어노테이션과, 스프링web에서 기본값으로 사용하는 HttpMessageConverters를 사용할수있도록 지원해준다. 
  - 또한, Spring Cloud에서 Eureka, CircuitBreaker, Loadbalancer를 통합하여, feign 사용시에 http client를 제공해준다
  - `@FeignClient(name = "stores", url="http://~~")`
    - 이런식으로 쓸 수 있는데, name은 LoadBalancer client를 사용하기위한 client의 이름을 명시하는것.. (그냥 bean 이름)
    - 만약 Eureka를 사용하고있따면, name에서 셋팅된 값이 eureka에 등록된 서비스이름과 동일할때 해당 주소로 resolve 해준다
      - Eureka 사용하지않고, SimpleDiscoveryClient 사용해서 외부 설정의 서버 정보를 가져올수도 있다
    - `FeignClientsConfiguration` 기본 설정아래 추가적으로 커스텀할수있음 (오버라이드됨)
      ```java
        @FeignClient(name = "stores", configuration = FooConfiguration.class) // 요렇게 커스텀가능
        public interface StoreClient {
            //..
        }

        public class FooConfiguration {
            @Bean
            public Contract feignContract() {
                return new feign.Contract.Default();
            }

            @Bean
            public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
                return new BasicAuthRequestInterceptor("user", "password");
            }
        }

      ```
      - 어떻게 그게 가능? 
        - > Spring Cloud creates a new ensemble as an ApplicationContext on demand for each named client using FeignClientsConfiguration. This contains (amongst other things) an feign.Decoder, a feign.Encoder, and a feign.Contract. It is possible to override the name of that ensemble by using the contextId attribute of the @FeignClient annotation.
          - Spring Cloud가 feing client에 따라 내부적으로 새로운 ApplicationContext를 생성하고 기본적으로 FeignClientsConfiguration를 사용하나, 그게 Configuration 에서 Bean으로 등록되어있는게 있다면(Spring Cloud가 새로 만든 ApplicationContext내부에서), 오버라이딩되는듯함
      - `feign.Client` customizing 하기 
        - 공식문서에 보면, ApacheHttpClient를 사용할때 `CloseableHttpClient` 빈을 주입하여 커스텀하라고 되어있고, 여기서 셋팅한 빈을 주입하면 정상적으로 커스텀된다
        - 하지만, 이 ApacheHttpClient를 특정 feing client 에는 다른 셋팅을 하고싶을때 위의 FooConfiguration에서 `CloseableHttpClient` 빈을 다르게 주입한다해도 HttpClient는 기존 만들어진(전역적으로 셋팅된)것을 재사용하게된다
        - 그래서 feignClient마다 개별적으로 셋팅을 먹이려면 Configuration에서 아예 feign.Client로 빈을 주입해주어야한다
          ```java
            // ApacheHttpClient 사용시..

            public class FooConfiguration { // ApacheHttpClient 오버라이딩 안됨 

                public CloseableHttpClient httpClient() {
                    //.. customizing
                }
            }

            public class FooConfiguration { // ApacheHttpClient 오버라이딩 됨
                public feign.Client httpClient() {
                    //.. customizing
                }
            }
          ```
  - Spring Cloud OpenFeign provides the following beans by default for feign (BeanType beanName: ClassName):
    - Decoder feignDecoder: ResponseEntityDecoder (which wraps a SpringDecoder)
    - Encoder feignEncoder: SpringEncoder
    - Logger feignLogger: Slf4jLogger
    - MicrometerCapability micrometerCapability: If feign-micrometer is on the classpath and MeterRegistry is available
    - CachingCapability cachingCapability: If @EnableCaching annotation is used. Can be disabled via feign.cache.enabled.
    - Contract feignContract: SpringMvcContract
    - Feign.Builder feignBuilder: FeignCircuitBreaker.Builder
    - Client feignClient: If Spring Cloud LoadBalancer is on the classpath, FeignBlockingLoadBalancerClient is used. If none of them is on the classpath, the default feign client is used.
  - 동일한 대상(@FeignClient의 name이 동일)으로 api를 요청하고싶다면, contextId를 사용하여아한다
    ```java
        @FeignClient(contextId = "fooClient", name = "stores", configuration = FooConfiguration.class)
        public interface FooClient {
            //..
        }

        @FeignClient(contextId = "barClient", name = "stores", configuration = BarConfiguration.class)
        public interface BarClient {
            //..
        }
    ```
  - Timeout Handling
    - `connectTimeout` prevents blocking the caller due to the long server processing time.
    - `readTimeout` is applied from the time of connection establishment and is triggered when returning the response takes too long.
    - apache에서 connection pool을 사용한다면, 커넥션 요청에 대한 타임아웃을 셋팅하기위해`requestConnectionTimeout` 은 별도 설정필요..

  - 자동구성
    - `FeignLoadBalancerAutoConfiguration` 여기서 Feign 관련 client 이루어짐
    - @Configuration을 사용하여 feign 관련 설정을 먹이면, @FeignClient에 전역적으로 설정이 셋팅된다
      - loadbalancer나 retryTemplate 을 사용하는지에 따라, `feign.Client`의 구현체가 달라진다..
        - ex. ApacheHttpClient 사용시에, loadbalancer와 retryTemplate 사용시 RetryableBlockingFeignLoadbalancerClient 사용하고(ApacheHttpClient를 Wrapping), 그런거 없으면 그냥 ApacheHttpClient 사용
    
          



- Spring Cloud Commons
  - service discovery, load balancing, circuit breakers 는 sping cloud clients에 의해 사용되는 공통의 추상 레이어로 적합하다.
  


--- 

마이그레이션 내용
- netflix feign -> openfeign 으로 변경
  - 기존 어노테이션을 그대로 사용해도 상관없다함
    - [Spring Netflix Feign과 OpenFeign의 차이점](https://recordsoflife.tistory.com/294)



