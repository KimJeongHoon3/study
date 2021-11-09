webclient

- [정리굿](https://medium.com/@odysseymoon/spring-webclient-%EC%82%AC%EC%9A%A9%EB%B2%95-5f92d295edc0)

- netty를 사용했음..(비동기 라이브러리..)
  - 이벤트 루프구조를 사용하므로 스레드에대한 최적의 활용이 진행..


??? 어떻게 http를 비동기로 가능할까..? Netty를 사용한다하는데, Http는 요청을 하고 응답을 받아야하는데,, 어떻게..?
-> 


- RestTemplate의 대안으로 WebClinet사용을 권고
- 특징
  - Non-blocking I/O
  - Reactive Streams back pressure
  - High concurrency with fewer hardware resources
  - Functional-style, fluent API that takes advantage of Java 8 lambdas
  - Synchronous and asynchronous interactions
  - Streaming up to or streaming down from a server
- WebClient.create()로 생성하는것보다 WebClient.builder() 를 사용하면 좋은데 아래와 같은 이점이 있음
  - 모든 호출에 대한 기본 Header / Cookie 값 설정
  - filter 를 통한 Request/Response 처리
  - Http 메시지 Reader/Writer 조작
  - Http Client Library 설정
- 설정
  - MaxInMemorySize
    - Spring WebFlux 에서는 어플리케이션 메모리 문제를 피하기 위해 codec 처리를 위한 in-memory buffer 값이 256KB로 기본설정 되어 있습니다. 이 제약 때문에 256KB보다 큰 HTTP 메시지를 처리하려고 하면 DataBufferLimitException 에러가 발생하게 됩니다. 이 값을 늘려주기 위해서는 ExchageStrategies.builder() 를 통해 값을 늘려줘야 합니다.
    ```java
        ExchangeStrategies exchangeStrategies = 
        ExchangeStrategies
            .builder()
            .codecs(configurer -> configurer.defaultCodecs()
                                        .maxInMemorySize(1024*1024*50)) //50MB
            .build();
    ```
  - Logging
    - ExchangeStrateges와 Logging level 설정을 통해 Request와 Response 정보를 상세히 확인가능(개발시에만 쓰면될듯)
    ```java
        exchangeStrategies
        .messageWriters().stream()
        .filter(LoggingCodecSupport.class::isInstance)
        .forEach(writer -> ((LoggingCodecSupport)writer).setEnableLoggingRequestDetails(true)); //요기 true 하고, org.springframework.web.reactive.function.client.ExchangeFunctions의 로깅레벨을 DEBUG로
    ```
  - Client Filters
    - Request 또는 Response 데이터에 대해 조작을 하거나 추가작업 가능
    ```java
        WebClient.builder()
                .filter(ExchangeFilterFunction.ofRequestProcessor( //filter에 ExchangeFilterFunction.ofRequestProcessor를 사용
                    clientRequest -> {
                        log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                        clientRequest.headers()
                                    .forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                        return Mono.just(clientRequest);
                    }
                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                    clientResponse -> { 
                        clientResponse.headers()
                                    .asHttpHeaders()
                                    .forEach((name, values) -> 
                                        values.forEach(value -> log.debug("{} : {}", name, value)));
                        return Mono.just(clientResponse);
                    }
                ))
    ```
  - HttpClient
    - HttpClient를 변경하거나 ConnectionTimoeout과 같은 설정값 변경할수있음
    ```java
        WebClient
        .builder()
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient
                    .create()
                        .secure(
                            ThrowingConsumer.unchecked(
                                sslContextSpec -> sslContextSpec.sslContext(    
                                SslContextBuilder
                                    .forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE) //HTTPS 인증서 검증하지않고 바로 접속하는 설정
                                    .build()
                                )
                            )
                        )
                        .tcpConfiguration(
                            client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120_000)
                                            .doOnConnected(
                                                conn -> conn.addHandlerLast(new ReadTimeoutHandler(180))
                                                            .addHandlerLast(new WriteTimeoutHandler(180))
                                                            )
                        )
                )
            )
    ```

- 사용방법
  - 기본적인 셋팅을 Configuration에서 @Bean으로 등록한뒤, 사용하는 서비스에서 DI받아서 사용
  - 기본 셋팅한것을 일부 수정해서 사용해야한다면, 해당 서비스에서 DI받아서 mutate()를 호출하여 변경이 필요한곳만 셋팅수정 
  - 4xx and 5xx 처리
    - WebClint에서는 WebCLintResponseException이 발생.. 이때 각 상태코드에 따라 임의의 처리를 하거나 Exception을 랩핑하고싶을때는 onStatus() 함수를 사용
    ```java
        webClient.mutate()
         .baseUrl("https://some.com")
         .build()
         .get()
         .uri("/resource")
         .accept(MediaType.APPLICATION_JSON)
         .retrieve()
         .onStatus(status -> status.is4xxClientError() 
                          || status.is5xxServerError()
             , clientResponse ->
                           clientResponse.bodyToMono(String.class)
                           .map(body -> new RuntimeException(body)))
         .bodyToMono(SomeData.class)
    ```
  - get사용
    ```java
        public Mono<SomeData> getData(Integer id, String accessToken) {
            return
                webClient.mutate()
                        .baseUrl("https://some.com/api")
                        .build()
                        .get()
                        .uri("/resource?id={ID}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(SomeData.class)
                ;
        }
    ```
  - post사용
    ```java
        //form데이터
        webClient.mutate()
         .baseUrl("https://some.com/api")
         .build()
         .post()
         .uri("/login")
         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
         .accept(MediaType.APPLICATION_JSON)
         .body(BodyInserters.fromFormData("id", idValue)
                            .with("pwd", pwdValue)
         )
         .retrieve()
         .bodyToMono(SomeData.class);

         //json body
         webClient.mutate()
         .baseUrl("https://some.com/api")
         .build()
         .post()
         .uri("/login")
         .contentType(MediaType.APPLICATION_JSON)
         .accept(MediaType.APPLICATION_JSON)
         .bodyValue(loginInfo)
         .retrieve()
         .bodyToMono(SomeData.class);

    ```
  - retrieve() vs exchange()
    - HTTP 호출 결과를 가져오는 두 가지 방법으로 retrieve() 와 exchange() 가 존재합니다. retrieve 를 이용하면 바로 ResponseBody를 처리 할 수 있고, exchange 를 이용하면 세세한 컨트롤이 가능합니다. 하지만 Spring에서는 exchange 를 이용하게 되면 Response 컨텐츠에 대한 모든 처리를 직접 하면서 발생할 수 있는 memory leak 가능성 때문에 가급적 retrieve 를 사용하기를 권고하고 있습니다.
    ```java
      Mono<Person> result = webClient.get()
                                 .uri("/persons/{id}", id)
                                 .accept(MediaType.APPLICATION_JSON) 
                                 .retrieve() 
                                 .bodyToMono(Person.class);
      
      Mono<Person> result = webClient.get()
                                 .uri("/persons/{id}", id)
                                 .accept(MediaType.APPLICATION_JSON)
                                 .exchange()
                                 .flatMap(response -> 
                                   response.bodyToMono(Person.class));

    ```
  - block(동기) 으로 사용하는방법
    - block()함수를 호출
    - dependency에 직접적으로든 아니면 다른 dependency에 의해서든 spring-boot-starter-web가 포함되어있어야함
      - 없으면 아래의 에러가 발생합니다.
        - java.lang.IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread
      - stackoverflow 내용
        - If your application is just using spring-boot-starter-webflux, it means both the server and client will be using Spring WebFlux. In this case, it is forbidden to call a block operator within a Controller handler, as it will block one of the few server threads and will create important runtime issues.    
        If the main driver behind this is to use WebClient, then you can depend on both spring-boot-starter-web and spring-boot-starter-webflux. Your Spring Boot application will still use Spring MVC on the server side and you'll be able to use WebClient as a client. In that case, you can call block operators or even use Flux or Mono as return types in your controllers, as Spring MVC supports that. You can even gradually introduce WebClient in an existing Spring MVC application.
        - 출처 : https://stackoverflow.com/questions/53083790/spring-webclient-as-an-alternative-to-resttemplate/53084705#comment117480100_53084705
  - 기타 참고사항
    - connection pool?
      - 동기식으로 만든 RestTemplate은 커넥션풀을 고려해야하지만, 비동기로 만들어진 WebClient에서는 크게 신경쓸 필요없다함(Channel 500, Queue 1000개가 default).. 
        - http://egloos.zum.com/preludeb/v/7466753
        - https://jjeong.tistory.com/1427
      - [connection pool 관련 내용](https://stackoverflow.com/questions/57673715/webclient-maxconnection-pool-limit)
      - [HttpClient의 connection pool 왜 쓰는가?](https://somnusnote.tistory.com/entry/HttpClient-Connection-Pooling)
        - Connection을 맺으려면 포트를 열어야하는데, 계속 지속적으로 열었다가 닫게되면 TIME_WAIT상태에 있따가 끊어지게되어 쌓여있게되면 Too Many Open Files 에러가 나면서 Connection을 맺지못하는 상황이 나타날수있으니, Connection을 재사용하기위함
        - RestTemplate의 Connection Pool이란
          - Spring 3.0부터 지원하는 HTTP 통신 템플릿인 RestTemplate은 복잡한 HttpClient 사용을 한번 추상화한 객체로써 단순 메소드 호출만으로 쉽게 HTTP 요청을 주고 받을 수 있도록 도와준다. RestTemplate은 호출할 때마다, 로컬에서 임시 TCP 소켓을 개방하여 사용한다. 이렇게 사용된 TCP 소켓은 TIME_WAIT 상태가 되는데, 요청량이 많아진다면 TIME_WAIT 상태의 소켓들은 재사용 될 수 없기 때문에 응답에 지연이 생길 수 있다. 이러한 응답 지연 상황을 대비하여 DB가 Connection Pool을 이용하듯이 RestTemplate도 Connection Pool을 이용할 수 있다. 그러기 위해선 RestTemplate 내부 구성을 설정해줘야한다.
          - 단, 호출하는 API 서버가 Keep-Alive를 지원해야지 RestTemplate의 Connection Pool을 활용할 수 있다. 타겟 서버가 Keep-Alive를 지원하지 않는다면 미리 Connection Pool을 만들어 놓지 못하고 요청마다 새로운 Connection이 연결되어 매번 핸드쉐이크가 발생된다. 따라서 Connection Pool을 위한 RestTemplate의 내부 설정이 무의미하게 된다.
          - 출처 - 내용설명도 매우굿 : https://minkwon4.tistory.com/216
      - [RestTemplate 사용시 connection pool 설정 및 서버가 keep-alive 이어야한다는것 설명](https://multifrontgarden.tistory.com/249)
- [참고사이트 - 기본적인 webclint 사용방법 내용정리 매우좋음](https://medium.com/@odysseymoon/spring-webclient-사용법-5f92d295edc0)
- [webclient 동기식으로 사용하는 방법](https://darkstart.tistory.com/258)
- [테스트 예시](https://akageun.github.io/2019/06/23/spring-webflux-tip-2.html)
- 위 내용보다 추가적인 사용방법알고싶으면, reference찾아볼것

- webclient 주의할점(메모리누수): https://gipyeonglee.tistory.com/256
  - NOTE: When using a ClientResponse through the WebClient exchange() method, you have to make sure that the body is consumed or released by using one of the following methods:

body(BodyExtractor)
bodyToMono(Class) or bodyToMono(ParameterizedTypeReference)
bodyToFlux(Class) or bodyToFlux(ParameterizedTypeReference)
toEntity(Class) or toEntity(ParameterizedTypeReference)
toEntityList(Class) or toEntityList(ParameterizedTypeReference)
toBodilessEntity()
releaseBody()
You can also use bodyToMono(Void.class) if no response content is expected. However keep in mind the connection will be closed, instead of being placed back in the pool, if any content does arrive. This is in contrast to releaseBody() which does consume the full body and releases any content received.

  - 출처 : https://stackoverflow.com/questions/60304827/spring-webclient-how-to-handle-error-scenarios
  - 추가 주의할점
    - https://yangbongsoo.tistory.com/9

 	