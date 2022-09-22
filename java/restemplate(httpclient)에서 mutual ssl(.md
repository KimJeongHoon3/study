- restemplate (httpclient)에서 mutual ssl(tls) 진행시 주의해야할점..
  - 원인
    - restTemplate에서 connection pool을 셋팅하면 keep alive가 작동하여(물론, 상대방 서버에 keep alive 지원되어야..) ssl에 대한 지속적인 핸드쉐이크가 일어나지않을거라 생각..
    - 그러나, 모든 요청에 대해서 새로운 커넥션을 만들고 핸드쉐이크도 계속진행..
    - 지정한 풀의 갯수만큼은 만드는데, 계속 새로운 커넥션으로 풀을 채움.. 
      - 예를들어 5개로 connection 을 지정하면, 새롭게 만들면서 5개의 커넥션을 유지.. 풀에대한 의미가..;
    - httpClient는 httpContext를 통해 요청시 커넥션을 재사용할지를 결정하는데, restTemplate에서 httpClient 사용시에 httpContext를 공유하도록 셋팅이 안되어있었음
  - 해결
    - HttpComponentsClientHttpRequestFactory 에서 request 생성시에 httpContext를 넘겨주는데, HttpComponentsClientHttpRequestFactory.createHttpContext 를 오버라이딩하여 httpContext를 재사용할수있도록 지정


- 작동원리 설명
  - https://skasha.tistory.com/48

- https://stackoverflow.com/questions/63829066/mtls-and-http-client-connection-pool-usage