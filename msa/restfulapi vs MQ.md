restfulapi vs MQ

restful api의 경우 비동기식으로 처리하게 되었을때 요청 서버(작업을 수행하는 서버)가 다운되었을 경우 처리를 못하여 이에따른 적절한 예외처리가 별도로 필요함 (ex. retry 등)
MQ를 사용하면, 작업을 수행하는 서버가 다운된것과 상관없이 큐에 작업이 들어가있기때문에, 작업을 수행하는 서버는 동작이 가능할때 해당 큐에 데이터를 컨슘하여 작업을 수행

MQ는 pub/sub 으로 나눠지기때문에 어떤 어플리케이션이 온라인상태가 아닐지라도 문제되지않음 (MQ에 pub과 sub하는 서버간에 어떤 영향도 없음)


restful api를 비동기로 구현하기엔 고려해야할 사항들이 많다..
MQ는 비동기로 구현하기에 자연스러운 모습을 보여준다

MQ는 작업분산가능 => 처리량 증가 (restful도 어떻게 핸들링하느냐에따라 분산가능.. - 별도로 로드밸런서같은 기능이 필요)


MQ는 동기식 보장이 어려움
API는 동기식 보장이 용이


- https://www.oracle.com/cloud/cloud-native/api-management/what-is-api/apis-and-messaging-differences/#:~:text=While%20APIs%20define%20the%20terms,from%20one%20system%20to%20another.
  - api / messaging 으로 구분
    - 일반적으로 API는 해당 소프트웨어가 서비스 요청을 수신하고 응답하는 방법을 정의
    - API는 소프트웨어가 서비스 요청을 보내고 받는 방법에 대한 용어를 정의하는 반면, 메시징은 한 시스템에서 다른 시스템으로 정보를 전송하는 프로세스입니다.
    - 커뮤니케이션은 동기식 비동기식으로 구분가능
      - 동기식 커뮤니케이션은 커뮤니케이션에 관련된 모든 당사자가 참석하고 재전송할 수 있어야 한다는 의미입니다. 
      - 비동기식은 통신을 원하는 시스템 당사자가 동시에 존재하지 않아도 됨. 우리가 서로에게 이메일을 보낼 때 이런 방식으로 작동합니다. 통신이 비동기적으로 작동하려면 정보를 주고받을 수 있도록 중개자가 개입
    - 메세징 시스템은 다양한 패턴으로 구현됨
      - service bus
      - web services
        - point to point
      - message broker
        - 메시지 공급자와 메시지 소비자 사이의 중개자
        - 수신자가 컨슘해갈때까지 메세지를 저장해놓는다
        - 수신자의 즉각적인 가용성에 대한 걱정이 없음

  - => 메세징 시스템의 web services는 point to point 이기때문에 연관된 모든 어플리케이션이 온라인 상태여야 정상동작. 하지만, message broker는 연관된 모든 어플리케이션이 오프라인 상태여도 broker에 저장되어있기때문에 동작에 이상없음.  온라인 상태되면 broker에서 데이터 가져오면됨

- https://dzone.com/articles/http-vs-messaging-for-microservices-communications
  - http api는 별도의 작업을 위해서 새로운 endpoint를 지정필요.. 
  - 반면 messaging은 (브로커에 따라 다를수있지만) 메세지 소비시 브로커가 메세지를 없애지않는다면, 별도의 작업을 수행하는 새로운 컨슈머를 만들면 끝
  - HTTP는 보다 간단하고 잘 정립된 프로토콜로, 기존 인프라에 쉽게 구현하고 통합할 수 있습니다. 또한 로드 밸런서 및 프록시와의 호환성이 우수하여 고가용성 및 확장성이 필요한 시스템에 적합한 선택입니다.
  - 반면에 메시징은 마이크로서비스를 위한 보다 강력하고 유연한 통신 메커니즘을 제공합니다. 비동기 및 분리형 통신이 가능하므로 느슨한 결합과 이벤트 중심 아키텍처가 필요한 시스템에 유용할 수 있습니다.
  - HTTP와 messaging을 같이 사용하는 hybrid로 가는게 좋다..
  - => rest api와 같이 end to end는 동일한 데이터로 추가 작업을 수행하기위한 api 서버에 대한 호출을 추가로 작업하여야한다. 그러나, kafka와 같은 message broker를 사용하면 새로운 작업을 수행하는 어플리케이션이 관련 토픽만 컨슘하면 끝

- https://memphis.dev/blog/comparing-rest-and-message-brokers-choosing-the-right-communication/
  - **REST는 단순성과 직접적인 상호 작용을 제공하는 반면, 메시지 브로커는 커플링을 제거하고 비동기식이고 안정적이고 훨씬 더 확장 가능한 통신을 가능**
    - 디커플링은 요청에 따른 작업을 수행하는 endpoint를 정확하게 알아야하는지 여부..
  - 여기 좀더 보고 정리
    - 여기가 제일 설명 좋은듯



- message queue와 message broker의 관계
  - message queue는 메세지를 저장하는곳이고, message broker가 message queue들을 포함하여 전체 메시징 시스템을 관리 (큐와 관련된 라우터, 변환, 처리 기능을 관리하는 mq를 포함하는게 message broker)
    

- kafka 선택이유
  - 확장성 + 분리가능
  - 상태변경에 따른 이벤트 발생
    - 외부 api 호출시 해당 api가 받을 수 있는 만큼의 트래픽을 전송가능
  - 도메인 구분이 확실함 
    - 모든 어플리케이션이 온라인 상태가 아니여도 됨
  - restful api 는 경우 엔드투엔드를 알아야함
  