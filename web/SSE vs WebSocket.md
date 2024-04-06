- SSE vs WebSocket
  - 공통점
    - 서버가 클라이언트로에게 실시간으로 어떤 데이터를 전달할 수 있다
    - 둘다 OSI 7계층에 위치하고, TCP에 의존
  - 차이점
    - SSE
      - HTML5 표준
        - > The EventSource API is standardized as part of HTML5 by the WHATWG.
          - https://en.wikipedia.org/wiki/Server-sent_events
          - https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events
        - 새로운 라이브러리 추가나 프로토콜 학습 없이 바로 적용가능
        - MIME type: text/event-stream
      - HTTP 프로토콜사용
      - 서버에서 클라에게 전송하는 단방향
      - HTTP를 통해서 할때 브라우저당 6개까지 가능 (HTTP2는 100개가 기본)
        - > Warning: When not used over HTTP/2, SSE suffers from a limitation to the maximum number of open connections, which can be especially painful when opening multiple tabs, as the limit is per browser and is set to a very low number (6). The issue has been marked as "Won't fix" in Chrome and Firefox. This limit is per browser + domain, which means that you can open 6 SSE connections across all of the tabs to www.example1.com and another 6 SSE connections to www.example2.com (per Stackoverflow). When using HTTP/2, the maximum number of simultaneous HTTP streams is negotiated between the server and the client (defaults to 100).
      - 순수한 텍스트 프로토콜 (UTF-8 인코딩)
        - 바이너리 데이터 사용불가
      - 만료시간이 다되면 브라우저에서 자동으로 서버에 재연결 요청
      - 실시간 처리시 proxy 주의필요
        - proxy서버(ex. nginx)에서 buffer를 채워서 응답하도록 설정되어있다면 실시간으로 데이터 전송 안될 수 있음
        - buffer 기능을 꺼두어야함
      - 자동재연결
        - 연결이 끊킬 경우, 클라이언트가 자동으로 서버에 재접속을 시도
          - 서버측에서 재접속 로직 구현필요없음
      - 사용방법 참고 사이트 (적용시 트러블슈팅도 잘 정리)
        - https://gong-check.github.io/dev-blog/BE/%EC%96%B4%EC%8D%B8%EC%98%A4/sse/sse/
    - WebSocket
      - 처음 연결할때만 HTTP를 활용 (이후는 그냥 TCP 동작 - 로우레벨)
      - websocket 프로토콜 사용
      - 양방향
      - 텍스트, 바이너리 모두 가능
        - 즉, 이미지, 오디오, 파일 등도 전송가능 (다만, 파일 처리에 오버헤드 발생가능)
          - 바이너리 보낼때 base64 인코딩 (원본 데이터보다 33% 증가)
          - TCP 기반 프로토콜이라 네트워크 지연과 TCP 흐름제어 및 혼잡제어 메커니즘 등이 대용량 파일 전송할대 전송속도 제한될수 있음..
      - 웹소켓이 same-oirgin 정책을 강제하지않기때문에, CSRF같은 공격에 취약
        - 그러나 스프링은 same-origin 정책을 적용할수있도록 api가 만들어져있음 (보안강화)
      - 재연결 관련 별도 관리 필요
    - 간단하게 표로 정리
      - ![](2024-04-02-23-13-43.png)
  - 그래서 무얼선택해야하나?
    - 클라이언트에 지속적인 업데이트 스트림을 푸시하기만 하면 된다면 SSE가 더 적합한 선택. 반면에 이러한 이벤트 중 하나에 어떤 식으로든 클라이언트가 반응해야 하는 경우에는 WebSocket이 더 유용
    - WebSocket을 날것 그대로 사용하진 않을테니 STOMP를 사용하게되고 이에 따라 spring 사용시 관련해서 추상화한 내용들에 대해서도 잘 알아야함.. 러닝커브.. (추가로 spring websocket 사용시 websocket 지원안되는경우를 대비하여 SOCKJS 또한 사용) 
    - postman에서 SSE, websocket 모두지원가능하여 테스트가능
      - jmeter와 gatling 으로 성능테스트도 가능

  - 참고사이트
    - [차이점 심플하게 설명 굿](https://surviveasdev.tistory.com/entry/%EC%9B%B9%EC%86%8C%EC%BC%93-%EA%B3%BC-SSEServer-Sent-Event-%EC%B0%A8%EC%9D%B4%EC%A0%90-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B3%A0-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0)
    - [완전 자세하고 명확한 설명](https://softwaremill.com/sse-vs-websockets-comparing-real-time-communication-protocols/#sse-vs-websockets-comparing-real-time-communication-protocols)

- STOMP (WebSocket subprotocol)
  - https://docs.spring.io/spring-framework/reference/web/websocket/stomp/overview.html
    - Simple Text Oriented Messaging Protocol
    - TCP와 WebSocket같은 신뢰할수있는 양방향 스트리밍 네트워크 프로토콜에서 사용됨
    - text oriented이지만, payload에 text뿐아니라 binary도 가능
    - HTTP 스타일의 프레임
      - STOMP 프레임은 COMMAND, HEADER, BODY 섹션으로 구성되며, 이는 HTTP 프로토콜의 요청 및 응답 형식을 모델
        - "Frame-based protocol"이란 데이터를 전송할 때 "프레임"이라는 단위로 구성하여 전송하는 통신 프로토콜을 말함. 여기서 "프레임"은 통신 데이터의 기본 단위로, 일반적으로 헤더, 본문(body), 그리고 종종 트레일러(footer)로 구성
      ```
        COMMAND
        header1:value1
        header2:value2

        Body^@
      ```
        - `SEND` 나 `SUBSCRIBE` 명령어를 통해서 메세지를 전송하거나 구독가능. 이를 통해 간단한 pub-sub 메커니즘 사용가능
    - 스프링 사용하면, HTTP기반 보안, 공통 validation 등 사용가능
    - path 관례
      - `/topic/..` implies publish-subscribe (one-to-many)
      - `/queue/` implies point-to-point (one-to-one) message exchanges
- SockJS
  - https://velog.io/@koseungbin/WebSocket
  -  SockJS는 WebSocket이 사용 가능한 환경에서는 WebSocket을 사용하고, 그렇지 않은 경우에는 다른 기술(예: AJAX 롱 폴링, iframe을 이용한 스트리밍 등)로 대체하여 통신

- websocket 사용시 tomcat의 스레드모델
  - tomcat 8.5는 websocket으로 통신시 NIO 사용한다함..
    - 즉, 수많은 연결 요청이 온다해서 톰켓의 가용스레드가 없어지는건 아님
  - 데이터 수신받고 요청 처리할때 톰켓 스레드풀에서 처리되는건아닌가??
    - http 요청을 처리하는 톰켓 스레드풀과는 별도임
    - spring에서는 clientInboundChannel(데이터 수신할때), clientOutboundChannel(데이터 송신할때) 에서 thread pool을 활용하는데, ThreadPoolExecutor가 사용됨
      - https://docs.spring.io/spring-framework/reference/web/websocket/stomp/configuration-performance.html


- [websocket 관련해서 한글로 설명이 매우좋음 (스프링 문서 해석도 다 해놓은듯)](https://velog.io/@koseungbin/WebSocket)