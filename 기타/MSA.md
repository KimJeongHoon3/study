- MSA
  - 모놀리식 구조의 한계
    - 서비스/프로젝트가 커지면 커질수록, 영향도 파악 및 전체 시스템 구조의 파악에 어려움이 있습니다.
    - 빌드 시간 및 테스트시간, 그리고 배포시간이 기하급수적으로 늘어나게 됩니다.
    - 서비스를 부분적으로 scale-out하기가 힘듭니다.
    - 부분의 장애가 전체 서비스의 장애로 이어지는 경우가 발생하게됩니다.

  - > "the microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API. These services are built around business capabilities and independently deployable by fully automated deployment machinery."
    - 참조 : https://martinfowler.com/articles/microservices.html
    - 마틴 파울러는 MSA 스타일을 보통 *HTTP API를 상호간 사용하는 작은 서비스*이며, 완전 자동화된 배포에 의한 *독립적으로 배포가능한 서비스*여야함을 이야기함..

  - 장점
    - 독립적으로 서비스를 운영하기때문에 배포 굿
      - 배포시 전체 서비스 중단 없음.. 
      - 요구사항을 신속하게 반영가능
    - 확장에 굿
      - 트래픽 몰리면 확장해서 대응 가능
      - 클라우드 사용에 적합한 아키텍처
    - 장애가 전체 서비스로 확장될 가능성 적음
    - 신기술 적용이 모놀리식보다 유연
  - 단점
    - 모놀리식보다 복잡한 아키텍처..
    - 성능
      - API를 호출하기때문에 모놀리식보다 통신비용이나 latency가 있음
    - 테스트/트랜잭션
      - 서비스가 분리되어있기때문에 테스트가 쉽지않을뿐아니라, 트랜잭션을 다루기 쉽지않다
        - 좀더찾아서정리..
    - 데이터 관리 - 데이터가 여러 서비스에 걸쳐 분산되기 때문에 한번에 조회하기 어렵고, 데이터의 정합성 또한 관리하기 어렵습니다.
      - 좀더 찾아서 정리..


https://velog.io/@tedigom/MSA-%EC%A0%9C%EB%8C%80%EB%A1%9C-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0-1-MSA%EC%9D%98-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90-3sk28yrv0e


- MSA에서 내부 통신은 크게 2가지 방식
  - 요청·응답 모델(Request-Response)
    - 
  - 발행-구독(Pub-Sub)

https://www.samsungsds.com/kr/insights/msa_architecture_edm.html