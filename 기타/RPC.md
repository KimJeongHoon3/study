RPC

- 로컬에서 메서드를 사용하는것처럼 원격 서버에 있는 메서드를 호출하는것.
- rpc의 구현은 상당히 다양
  - java RMI (Remote Method Invocation)
  - gRPC
  - JAX-WS (Java API for XML Web Services)
  - HTTP 사용
    - `POST /addProduct HTTP/1.1` (항상 POST 메서드를 사용하고 body 전달)
      - addProduct 메서드를 호출해라..
- gRPC와 restful api의 성능 주요 차이점
  - chatGPT 답변
  ```md
    네, 맞습니다. 소켓을 통해 전송되는 최종 데이터는 이진 형태로, 프로토콜 버퍼가 JSON이나 다른 텍스트 기반 포맷에 비해 파싱과 역직렬화에서 성능 이점을 갖는 주된 이유는 그 구조와 처리 방식에 있습니다.

    ### 프로토콜 버퍼의 이진 파싱 이점

    1. **최소화된 메타데이터**: 프로토콜 버퍼는 필드 이름 대신 작은 크기의 숫자 태그를 사용하여 필드를 식별합니다. 이 방식은 필드를 표현하기 위해 필요한 데이터 양을 크게 줄여주므로 데이터 전체 크기가 작아집니다. JSON에서는 각 데이터 항목에 필드 이름을 문자열로 명시해야 하므로 이에 비해 훨씬 많은 공간을 차지합니다.
        => 문자는 인코딩을 위한 작업이 필요.. 뭘로 인코딩할것인지..(ex. UTF-8? UTF-16?)

    2. **고정된 스키마**: 프로토콜 버퍼 데이터는 사전에 정의된 스키마에 따라 엄격하게 구성됩니다. 이 스키마 정보는 파싱 과정에서 데이터 타입과 구조를 빠르게 인식하는 데 도움을 줍니다. 반면, JSON은 동적 타입을 가지며, 파싱 시 각 필드의 데이터 타입을 매번 검사하고 해석해야 합니다.

    3. **직렬화 및 역직렬화의 효율성**: 프로토콜 버퍼는 바이너리 형태로 직렬화된 데이터를 매우 빠르게 역직렬화할 수 있습니다. 이는 프로토콜 버퍼의 바이너리 형식이 메모리 내에서 효율적으로 처리되기 때문입니다. 데이터를 바이트로 직접 매핑하는 것은 메모리 복사와 같은 비용이 낮은 작업이 가능하게 하여, 전반적인 처리 속도를 향상시킵니다.

    ### 성능 테스트 및 실제 사용

    실제 어플리케이션에서는 이러한 이론적 이점들이 큰 성능 차이로 이어질 수 있습니다. 특히, 대규모 데이터를 빈번하게 처리하거나, 네트워크 지연이 큰 환경에서 프로토콜 버퍼의 이점이 두드러집니다. 성능 테스트에서 프로토콜 버퍼는 JSON에 비해 역직렬화 시간이 훨씬 짧은 것으로 나타나곤 합니다.

    결론적으로, 프로토콜 버퍼의 이진 파싱 방식은 데이터 처리와 전송에 있어 효율적이며, 이는 네트워크 및 서버 리소스 사용 최적화로 이어져, 시스템 전체의 성능을 향상시키는 중요한 요소가 됩니다.
  ```
- grpc?


- 보고 정리하자
  - https://blog.naver.com/n_cloudplatform/221751268831
  - https://velog.io/@rawoon/REST-vs.-gRPC-%EC%B0%A8%EC%9D%B4%EA%B0%80-%EB%AD%98%EA%B9%8C
  - https://hayz.tistory.com/entry/RPC-gRPC-%EC%97%90-%EB%8C%80%ED%95%B4%EC%84%9C-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-1
  - https://www.baeldung.com/rest-vs-grpc