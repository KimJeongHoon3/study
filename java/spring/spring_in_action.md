- 스프링
  - 스프링 애플리케이션 컨텍스트라는 컨테이너가 컴포넌트(Bean)들을 생성하고 관리.
    - 여기서 bean을 상호연결시켜주는것이 의존성 주입(Dependency Injection) 이다
  - 스프링의 큰 강점은 자동구성(autoconfiguration) 인데, 이는 알아서 자동연결(autowiring) 과 컴포넌트 검색이라는 스프링 기법을 기반으로 한다
  - 이러한 자동구성의 기능이 더욱 확장된것이 스프링부트이다..
  - 스프링 Initializer
    - 프로젝트 디렉토리 구조생성과 빌드명세를 자동으로 정의해줌
    - 여러 외부 dependency도 선택하면 바로 적용
    - 빌드스크립트(maven 기준)를 보면, "starter" 라는 단어를 포함하는 라이브러리를 볼수있는데, 이는 자체적으로 라이브러리 코드를 갖는것이아니라, 부트 스타터의 의존성을 사용한다
      - 이렇게되면 빌드파일이 훨씬 줄어들고 관리하기가 쉬워진다
      - 그리고 부트 스타터가 관리해주기때문에 각각의 라이브러리들의 버전들이 상호호환되는것에 대한 걱징이 필요없어진다 
    - 스프링부트 devtools (개발할때만 사용됨. 배포시 적용x)
      - 코드변경시 자동으로 애플리케이션 다시시작
      - 브라우저로 전송되는 리소스가 변경될때 자동으로 브라우저를 새로고침
      - 템플릿 캐시를 자동으로 비활성화
    - 프레임워크를 사용하기위해 정의해야하는 코드들의 부담이 현저히 적어진다!(부트에서!) 그래서 좀더 어플리케이션 자체의 로직에 좀더 초점을 맞추어 개발이 가능하다
    - 톰켓내장..!

  - 빈 유효성 검사 API지원 (Hibernate 컴포넌트)  


- 리액티브 스프링
  - 리액티브 프로그래밍
    - 명령형 프로그래밍의 대안이되는 패러다임.. 명령형 프로그래밍의 한계를 해결가능 
      - 명령형 프로그래밍?
        - 한번에 하나씩 만나는 순서대로 실행되는 명령어들로 코드를 작성..
        - 하나의 작업이 완전히 끝나기를 기다렸다가 그 다음 작업을 수행
        - 각 단계마다 처리되는 데이터는 전체를 처리할 수 있도록 사용할수있어야한다
        - 그러므로 DB같은곳에 접근해서 데이터를 읽고 쓰려면 스레드는 block되어 대기상태에있게된다.. 이에 대한 대안으로 concurrent 프로그래밍이 지원되었지만, 이를 관리하는것은 쉽지않다
    - 리엑티브 프로그래밍은 함수적이고 선언적
    - 순차적으로 수행되는 작업단계를 나타낸것이 아니라, 데이터가 흘러가는 파이프라인이나 스트림을 포함
    - 리액티브 스트림은 데이터 전체를 사용할 수있을때까지 기다리지않고 사용가능한 데이터가 있을때마 처리되므로 사실상 입력되는 데이터는 무한
    - 확장성과 성능관점에서 유리
  - 리엑티브 스트림
    - 피보탈, 넷플릭스 등 엔지니어들에 의해서 시작..
    - 차단되지 않는 백프레셔를 갖는 비동기 스트림처리의 표준을 제공하는것이 목적
      - 비동기 스트림 처리의 표준 : 동시에 여러 작업 수행가능하기에(확장성)
      - 백프레셔 : 지나치게 빠른 데이터 소스로부터의 전달을 조절가능
    - Publisher로부터 시작해서 0 또는 그 이상의 Processor를 통해 데이터를 끌어온 다음 최종 결과를 Subscriber에게 전달
      - 중간중간 계쏙 sub하고 pub 하는것!
  - 리엑터
    - 리액티브 스트림을 구현하며, 수많은 오퍼레이션을 제공하는 Flux와 Mono의 두가지 타입으로 스트림을 정의한다(Publisher)
    - 일련의 작업단계를 기술하는것이 아니라, 데이터가 전달될 ***파이프라인***을 구성하는것!
      - <span style="color:yellow">코드만 봤을때는 작업단계같지만, 구독을 하지않으면 실제 실행되지않기에 파이프라인을 구성하는것이라 하는듯함</span>
    - 파이프라인의 각단계에서는 데이터를 변경시킬수있음
    - 각 오퍼레이션은 같은 스레드로 실행되거나 다른스레드로 실행될수있음 (subscribeOn, publishOn)
    - Flux와 Mono가 제공하는 오퍼레이션은 500개가 넘으며, 각 오퍼레이션은 다음과 같이 분류할수있음
      - 생성 오퍼레이션
        - `Flux.just("값")`
        - 배열(fromArray), Iterable(fromIterable), 자바Stream(fromStream) 으로부터 Flux나 Mono 생성가능
        - `Flux.range(1,10)`
        - `Flux.interval(duration)`
        - 등등..
      - 조합 오퍼레이션
        - 두개의 리액티브 타입을 결합하거나 하나의 Flux를 두개 이상의 리액티브 타입으로 분할해야하는 경우 사용
        - `flux1.mergeWith(flux2)`
          - flux1과 flux2 중 먼저 들어오는놈의 데이터부터 발행
          - 완벽하게 flux1과 flux2를 번갈아 방출되게 할수는없음
        - `Flux<Tuple2> zipFlux=Flux.zip(flux1,flux2)`
          - flux1에서 발행하는 데이터와 flux2가 발행하는 데이터가 하나의 쌍이 될때 발행됨
          - Tuple2말고 사용자가 직접 정의해서 새로운 타입으로 만들수도있음
        - `Flux.first(flux1,flux2)`
          - 둘중에 먼저 발행시작한 스트림만 사용
          - 즉, flux1의 데이터가 처음먼저 들어왔다면 flux1만 발행
      - 변환 오퍼레이션
        - skip
          - 발행하는 데이터의 갯수 뿐아니라 시간을 지정해서 건너뛸 수도 있다
        - take
          - skip과는 반대로 발행한 데이터가 지정한 갯수만큼만 방출하고, 특정시간동안만 방출하도록도 가능
        - filter
          - 조건식(Predicate)이 지정되면 원하는 조건을 기반으로 선택적인 발행가능
        - distinct
          - 기존에 발행한 데이터가 있다면 발행 안함. 즉, 중복발송 막을수있음
          - 주의해서 사용해야함..
            - 아마 어딘가에 중복을 막기위해 데이터를 저장하고 잇으므로 무한으로 발행되는거면 OOM 나타날수도..
            - <span style="color:yellow">좀더 찾아볼 필요잇음</span>
        - map
          - Flux로부터 발행될때 동기적으로(각 항목을 순차적으로 처리) 매핑이 수행
            - 비동기적으로(각 항목을 병행처리) 매핑을 수행하고싶다면 flatmap을 사용해야함
              - flatmap이 자동적으로 가능하게하는건 아닌듯하고.. flatmap은 여러 데이터들을 다시 Flux나 Mono로 만들때 인터리빙(끼어들기) 방식으로 순서를 보장하지않고 빠르게 스트림을 만들게 되는데, 이러한 인터리빙의 효과를 극대화 하기위해서는 flatmap에 전달하는 함수형 인터페이스 내부에 스케줄러를 잘 활용해서 해야함
        - flatmap
          
          ```java
            // flatMap과 subscribeOn을 사용하여 리액터 타입의 변환을 "비동기"적으로 수행하는 방법
            Flux.just("마이클 조던","스카티 피팬","스테판 커리")
                          .flatMap(n -> Mono.just(n)
                                  .map(p -> {
                                      String[] split=p.split("\\s");
                                      return split[0]+" - "+split[1];
                                  }).subscribeOn(Schedulers.parallel())
                          )
                          .subscribe(log::info);

            /*
            로그
            16:07:57.294 [parallel-3] INFO com.jh.webflux.publisher.FluxOperation - 스테판 - 커리
            16:07:57.295 [parallel-1] INFO com.jh.webflux.publisher.FluxOperation - 마이클 - 조던
            16:07:57.295 [parallel-1] INFO com.jh.webflux.publisher.FluxOperation - 스카티 - 피팬

            => map의 subscribeOn을 통해 작업이 병행으로 수행되므로 어떤 작업이 먼저끝날지 보장이 안되고, 그렇기때문에 flatMap을 통해 평면화된 Flux를 통해 발행되는 데이터들중 어떤게 먼저 발행될지 알수없다!
            */

          ```
        - `Flux<List<String>> bufferedFlux=flux.buffer(3)`
          - List로 모아줄수있음
        - `Mono<List<String>> monoList=flux1.collectList`
          - flux1의 모든 데이터를 list로..
          - Mono로 감싸짐
        - collectMap
          - 넘겨주는 key값을 기준으로 Map이 만들어짐
          - Mono로 감싸짐
      - 로직 오퍼레이션
        - all
          - 스트림에 전달받는 모든 데이터가 조건문(Predicate)에 맞으면 `Mono<Boolean>` 리턴
        - any
          - 스트림에 전달받는 데이터가 하나라도 조건문(Predicate)에 맞으면 `Mono<Boolean>` 리턴
    - StepVerifier 를 사용해서 리엑터를 검증할수있다!
    ```java
        @Test
        void zipFluxes(){
            Flux<String> flux1 = Flux.just("Garfield", "Kojak", "Barbossa")
                    .delayElements(Duration.ofSeconds(1));

            Flux<String> flux2 = Flux.just("Garfield2", "Kojak2", "Barbossa2")
                    .delayElements(Duration.ofSeconds(1));

            Flux<Tuple2<String, String>> zip = Flux.zip(flux1, flux2);

            StepVerifier.create(zip)
                    .expectNextMatches(tuple->tuple.getT1().equals("Garfield") && tuple.getT2().equals("Garfield2"))
                    .expectNextMatches(tuple->tuple.getT1().equals("Kojak") && tuple.getT2().equals("Kojak2"))
                    .expectNextMatches(tuple->tuple.getT1().equals("Barbossa") && tuple.getT2().equals("Barbossa2"))
                    .verifyComplete();
        }
    ```

- zip이나 merge는 내부적으로 어떤 스레드를 사용하나? (parallel 사용 - 가용 cpu 숫자에 맞추어 스레드 갯수 지정)


- https://m.blog.naver.com/gngh0101/221529470975 여기에 webflux 동작방식 정리 잘되어있음



```java
//이런점때문에 단순 테스트의 경우 @SpringBootApplication 을 사용하지 않고 단순 Netty 서버를 사용해 빠르게 서버를 실행하고 구현한 메서드들을 테스트 할 수 있다.
//만약 패스워드 암호화 및 복호화 테스트를 한다면 서버기능을 하는 객체 외에 추가적으로 필요한 객체는 해시 기능이 있는 spring-boot-starter-security 의 PasswordEncoder 뿐이다.

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

public static void main(String... args) {
    long start = System.currentTimeMillis();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(18); // encoder 생성

    HttpHandler httpHandler = RouterFunctions.toHttpHandler( // RouterFunction -> HttpHandler 변경
        route(POST("/password"), request -> request // RouterFunction 으로 라우터 로직 생성 
            .bodyToMono(PasswordDTO.class)
            .map(p -> passwordEncoder.matches(p.getRaw(), p.getSecured()))
            .flatMap(isMatched -> isMatched
                ? ServerResponse.ok().build()
                : ServerResponse.status(HttpStatus.EXPECTATION_FAILED).build())
        )
    );
    ReactorHttpHandlerAdapter reactorHttpHandler = new ReactorHttpHandlerAdapter(httpHandler); 
    // HandlerAdapter 에 HttpHandler 삽입, BiFunction 를 구현한 클래스임  
    DisposableServer server = HttpServer.create() // Netty Server
        .host("localhost").port(8080)
        .handle(reactorHttpHandler) // BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> 요구함
        .bindNow(); // 서버 엔진 시작  
        
    LOGGER.debug("Started in " + (System.currentTimeMillis() - start) + " ms"); // Started in 703 ms
    server.onDispose().block(); // main 스레드 차단  
}

``` 
- WebHttpHandlerBuilder
  - *WebHttpHandlerBuilder 목적
    - This builder has two purposes:
      - One is to assemble a processing chain that consists of a target WebHandler, then decorated with a set of WebFilters, then further decorated with a set of WebExceptionHandlers. 
        - => WebHandler, WebFilter, WebExceptionHandlers 관련 셋팅해줌..
      - The second purpose is to adapt the resulting processing chain to an HttpHandler: the lowest-level reactive HTTP handling abstraction which can then be used with any of the supported runtimes. The adaptation is done with the help of HttpWebHandlerAdapter. 
        - => HttpHandler에 체이닝할것들 
      - The processing chain can be assembled manually via builder methods, or detected from a Spring ApplicationContext via applicationContext, or a mix of both.
  - WebHttpHandlerBuilder에 직접 등록하거나 어플리케이션 컨텍스트에서 자동으로 주입받을수잇는 컴포넌트들
    - HttpHandler
      - 서로 다른 HTTP 서버를 쓰기위한 추상화가 전부
      - Netty, Jetty, undertow, 톰캣 ...
    - WebHandler
      - 요청을 처리하는핸들러(DispatcherServlet과 같은역할.. 프론트 컨트롤러)
      - 웹 어플리케이션에서 흔히 쓰는 광범위한 기능을 제공
      - User session과 Session Attribute
      - Request attributes
      - Local, Principal 리졸브
      - form 데이터 파싱, 캐시조회
      - multipart 데이터 추상화
      - 등등등
    - WebFilter
      - 다른 필터 체인과 WebHandler 전후에 요청을 가로채 원하는 로직을 넣을수있음
    - WebExceptinoHandler
      - WebFilter 체인과 WebHandler에서 발생한 예외를 처리
