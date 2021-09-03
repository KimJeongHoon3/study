MockWebServer 정리

- 외부 API호출 테스트를 진행할때 사용하기 매우좋음
- MockWebServer로 요청하게되면, enqueue되어있는 응답값을 하나씩 꺼내서 리턴해줌
  - 특정 요청에 맞춰서 응답도 가능
  - 응답하는 과정을 조정할수도 있음(ex.응답을 느리게해준다던지..)

- 사용방법
  - 의존성 추가
    ```xml
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>okhttp</artifactId>
        <version>4.0.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>mockwebserver</artifactId>
        <version>4.0.1</version>
        <scope>test</scope>
    </dependency>
    ```
  - MockWebServer 생성
    ```java
        MockWebServer mockWebServer=new MockWebServer();
        mockWebServer.start(); //MockWebServer 생성
        int mockServerPort=mockWebServer.getPort();
        
        HttpClient httpClient=HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .doOnConnected(connection ->
                    connection.addHandlerLast(new WriteTimeoutHandler(5,TimeUnit.SECONDS))
                            .addHandlerLast(new ReadTimeoutHandler(5,TimeUnit.SECONDS))
                );

        WebClient webClient=WebClient.builder()
                .baseUrl("http://localhost:"+mockServerPort) //mockWebServer 사용을 위한 port 셋팅
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build()
    ```
  - 요청에 대한 response 생성
    ```java
    //enque로 아래와 같이 셋팅해 놓으면 요청시 아래의 값을 응답해줌
    mockWebServer.enqueue(MockResponse() 
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .setBody("json내용"))


    //Dispatcher 사용해서 요청정보에 따라서 응답을 줄수도 있고, 응답에 행동을 더해줄수있음..
     mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
               /* if (request.getPath().contains("employee")) {
                    return new MockResponse()....;
                }
                if (request.getPath().contains("employer")) {
                    return new MockResponse()....;
                } */
                Thread.sleep(10000); //응답지연하기위해 sleep 셋팅 
                return getMockResponse(500, gson.toJson(uplusMessageResponseBody)); 
            }
        });

    ```
    
    - [참고사이트](https://berrrrr.github.io/programming/2021/01/24/how-to-use-mockwebserver/)