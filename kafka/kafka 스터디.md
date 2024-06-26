kafka 스터디

- centos7.4는 yum으로 설치가안된다. 그래서 binary 설치방식으로 셋팅진행
  - [문서참고](https://docs.docker.com/engine/install/binaries/)
  - [docker compose도 수동으로 셋팅](https://docs.docker.com/compose/install/linux/#install-the-plugin-manually)
- 주요 명령어
  - 생성된 토픽확인 (docker-compose로 설치 전제)
    - `$ docker-compose exec kafka-1 kafka-topics --describe --topic my-topic --bootstrap-server kafka-1:29092`\
  - consumer  
    - `$ docker-compose exec kafka-1 kafka-console-consumer --topic my-topic --bootstrap-server kafka-1:29092`
  - producer
    - `$ docker-compose exec kafka-1 kafka-console-producer --topic my-topic --broker-list kafka-1:29092`
  - 특정 컨테이너 로그 보기
    - `$ sudo docker-compose logs kafka-1 | less`
  - 특정 컨테이너 로그 실시간 보기
    - `$ sudo docker-compose logs -f kafka-1`
  - 카프카 토픽 설정 변경
    - `$ kafka-configs --bootstrap-server kafka-1:29092 --alter --entity-type topics --entity-name my-topic3 --add-config min.insync.replicas=1`
  - 로그 파일 보기
    - `$ kafka-dump-log --print-data-log --files 00000000000000000000.log`
  - __transcation_state 토픽 로그 보기
    - `$ kafka-console-consumer --bootstrap-server kafka-1:29092 --topic transaction-topic --formatter "kafka.coordinator.transaction.TransactionLog\$TransactionLogMessageFormatter" --from-beginning`

- 4장 카프카의 내부 동작 원리와 구현
  - high-watermark는 뭔가?
    - 카프카에서는 가장 최근 커밋된 오프셋 즉, 복제를 보장한 메세지의 오프셋을 이야기한다
    - high-watermark는 자신의 커밋된 지점을 가리키며, `replication-offset-checkpoint` 파일에서 확인할 수 있다
    - 0번 오프셋의 데이터를 커밋하게되면, `replication-offset-checkpoint`는 해당 토픽의 오프셋이 0이 아닌, 1로 기록되는데, 이는 high watermark 라는 용어의 뜻을 알면 이해가 된다
      - high watermark는 수위계와 관련이있다. 즉, 처음 물이들어와서 0에서 할당된 수위가 찼을때 눈금은 1을 가리킬 것이다. 그렇기에 이를 0번이라고 표현하지않고 1로 표현하는거로 보인다. 
      - 하이워터마크라는 용어 유래 (챗gpt)
        ```md
          하이워터마크(High Watermark) 용어의 이름은 그 기능과 역할에서 유래되었습니다. 이 용어는 실제로 수위계와 관련이 있습니다. 아래와 같은 비유를 사용하여 이해할 수 있습니다.
          1. **수위계와 유사성:** 하이워터마크는 데이터 스트림 또는 큐에서 어떤 지점까지 데이터가 도달했는지를 추적합니다. 이것은 물의 수위를 추적하는 수위계와 유사한 개념입니다. 수위계가 강이나 호수의 수위를 측정하듯이, 하이워터마크는 데이터 처리의 진행 상태를 측정합니다.
          2. **고수위와 저수위:** 하이워터마크는 "고수위"와 "저수위"로 나뉩니다. "고수위"는 데이터 스트림에서 가장 높은 위치를 나타내며, 데이터 처리의 가장 최근 상태를 나타냅니다. "저수위"는 데이터 스트림에서 가장 낮은 위치를 나타내며, 처리가 완료된 데이터를 나타냅니다.
          3. **데이터 처리의 흐름:** 하이워터마크는 데이터 스트림이나 큐를 따라 이동하면서 업데이트됩니다. 데이터가 처리되면 하이워터마크가 이동하며, 가장 최근 데이터가 어디까지 처리되었는지를 나타냅니다.
          이처럼 하이워터마크는 데이터 처리의 상태를 시각화하고 추적하는 데 사용되며, 그 이름은 이 개념에서 수위를 추적하는 것과 유사성 때문에 붙여졌습니다.
        ``` 
  - commit은 어떻게 이루어지나?
    - ISR내에서 모든 팔로워의 복제가 완료되면, 리더는 내부적으로 커밋되었다는 표시를 수행
    - **커밋되었다는것은 리플리케이션 팩터 수의 모든 리플리케이션이 전부 메세지를 저장했다는것을 의미한다!**
      - 복제는 했지만, 팔로워들이 커밋을 완료한 시점과는 다른것 같다..
    - 그리고 이런 마지막 커밋 오프셋 위치는 high-watermark라고 부른다
    - commit된 메세지만 consumer는 가져올 수 있다! (정확히는 리더가 commit된 시점!)
      - 왜?!
        - commit이 안된 즉, 리더만 메세지를 전달받았고 다른 팔로워들이 복제가 되지않은 상태에서 해당 메세지를 컨슈머A가 가져간 시점에서 리더파티션의 브로커가 죽었다. 그때에, 팔로워가 리더로 승격하게될텐데, 이때 새로운 컨슈머B가 나타나서 메세지를 가져오기 시작하면, 승격된 리더는 기존 리더보드 메세지 하나가 부족한 상태이기에 컨슈머B는 컨슈머A가 지닌 메세지를 갖지못하고있게된다.. (메세지 불일치!!)
    - 어디까지 커밋이 이루어졌는지는 `replication-offset-checkpoint` 파일을 통해서 확인가능하다!
      - `replication-offset-checkpoint`는 high-watermark 이기도한데, 
      - 각 브로커마다 `replication-offset-checkpoint` 가 있는데, 이는 offset이 **커밋된 위치**를 알려주므로 각 브로커마다 올라가는 타이밍이 다르다. 
        - 리더가 제일 먼저 커밋이 완료될 것이고, 나머지는 팔로워가 잇는 브로커들은 리더가 커밋 완료된 이후에 진행
        - 메세지 Producing 후 해당파일을 체크하면 꼭 리더가 속한 브로커의 오프셋이 가장 먼저 올라가진 않는데, 이는 페이지캐시에 저장한내용을 디스크에 기록하기때문에 차이가 있지 않을까 추정됨 
  - replication은 어떻게?
    - 리더와 팔로워는 ISR(InSyncReplica) 라는 논리적 그룹으로 묶여있음
      - 해당 그룹안에서만 리더가 될 수 있다
      - 리더는 팔로워가 특정 주기의 시간만큼 복제 요청을 하지않으면, ISR에서 박탈시킴
    - replication 순서
      - 리더에 메세지 저장 (offset 1)
      - 팔로워들은 리더에게 계속 fetch 요청(offset 1)하다가 리더로부터 전달받고 복제함
      - 팔로워들은 다음 오프셋 fetch 요청(offset 2)
      - 리더는 팔로워들로부터 다음 오프셋(offset 2) 요청들어온 것보고 이전까진 모두 복제되었음을 확인하며 커밋진행
      - 커밋완료후 팔로워들이 계속해서 fetch 요청(offset 2)을 보낼 때 이전까지 커밋됐음을 전달
      - 팔로워들도 리더의 응답을 통해서 커밋 진행
      - => 핵심은 카프카가 복제시에 어디까지 복제했는지 추가적인 통신이 이루어지는것이 아닌, 메세지를 가져오기위한 polling 방식에서 어디까지 복제했는지를 알 수 있도록 잘 만들어놓았다...
  - 리더 에포크(Leader Epoch)?
    - 카프카의 파티션들이 복구 동작을 할 때 메시지의 일관성을 유지하기 위한 용도로 이용
    - 리더에포크는 컨트롤러에 의해 관리되는 32비트의 숫자로 표현. 해당 리더에포크 정보는 리플리케이션 프로토콜에 의해 전파되고, 새로운 리더가 변 경된 후 변경된 리더에 대한 정보는 팔로워에게 전달. 리더에포크는 복구 동작 시 하이워터마크를 대체하는 수단으로도 활용
    - leader epoch와 해당 epoch때 어디까지 커밋되었는지를 확인할 수 있다
      - ex. (12 13) 으로 적혀있다면, leader epoch가 12일때 13까지 커밋이 되어있었다는걸 알 수 있고, leader(13 16) 이라면 새로운 리더가 선출되거나 리더가 변경되면서 leader epoch는 하나가 올라갔으며, 그때에 HWM가 16이므로 16까지 커밋되어있음을 알 수 있다.
      - 참고로 `leader-epoch-checkpoint` 에서 오른쪽에 남기는 오프셋을 해당 epoch가 되었을때의 HWM를 남긴것으로 이해하였으나 아래 코드상으로는 startOffset으로 나옴. (물론, startOffset은 아직 데이터가 들어가있지 않은 오프셋이므로 HWM와 같긴함..)  (**20231116 추가**)
        - https://stackoverflow.com/questions/50342961/what-is-a-role-of-leader-epoch-checkpoint-in-log-segment-of-kafka-partition
    - => 브로커 복구시 하이워터마크로만의 한계점을 리더에포크로 보완했다고 보면 된다
      - 어떤 한계?
        - 리더와 팔로워의 하이워터마크가 불일치 되는 시점에서 브로커의 복구시 리더에포크가 사용
        - ex. 리더와 팔로워간에 데이터 복제는 되었지만, 리더만 커밋이 되었고 아직 팔로워는 커밋이 되지못한 상태일때, 만약 팔로워 브로커가 다운되어 복구되면 팔로워는 기동시 커밋된 부분까지만 메세지를 살린다. 즉, 이미 복제되었던 메세지를 날린다. 그때에, 커밋이 완료된 리더로부터 메세지를 받지못한 채 리더 브로커가 죽었다면, 팔로워 브로커는 리더가 될 것이고 새로 선출된 리더는 메세지 하나가 없는 상태가 된다. 하지만, 리더 에포크를 사용하게되면 팔로워 브로커가 기동시에 커밋된 부분까지만 메세지를 살리는게아닌, 리더 에포크를 통해 현재 리더의군집(리더에포크)가 어떤 메세지까지 커밋되었는지를 보고(리더의 HWM) 해당 부분까지 커밋을 수행. 즉, 팔로워가 기동되면서 현재 리더의 커밋까지 커밋읆 맞춘다. (하이워터마크 상향조정) 이를 통해 유실을 방지할 수 있게된다. (이 예시에서는 리더에포크의 시작오프셋은 그리 중요하진 않것네)
        - 추가 ex
          ```log
            1) 1번 브로커(리더) (leader-epoch-checkpoint 3, 3 / hwm 9)
               2번 브로커(팔로워) (leader-epoch-checkpoint 3, 3 / hwm 9)

            2) 메세지 프로듀스

            3) 1번 브로커 (leader-epoch-checkpoint 3, 3 / hwm 10)
               2번 브로커 (leader-epoch-checkpoint 3, 3 / hwm 9) - 아직 hwm가 올라가지 못한상태..

            4) 1번 브로커 죽음

            5) 2번브로커 리더 승격 (leader-epoch-checkpoint 4, 9 / hwm 9)

            6) 메세지 프로듀스

            7) 2번 브로커 (leader-epoch-checkpoint 4, 9 / hwm 10)

            8) 1번 브로커 회복

            9) 1번 브로커는 뉴리더인 2번 브로커에게 리더에포크 값을 기반으로 4세대에서 시작오프셋이 9인데 hwm가 10으로 올라갔음을 알게되고(9번 오프셋의 내용이 커밋이 되었음) 자신의 원래 9번 오프셋내용을 지금 리더가 가지고있는 9번 오프셋내용으로 변경 (leader-epoch-checkpoint 4, 9 / hwm 10)
               2번 브로커(뉴리더) (leader-epoch-checkpoint 4, 9 / hwm 10)

            (참고: hwm가 10이라는것은 9번 오프셋까지 커밋되었다는것!)
          ```
    - 테스트간에 발견한 내용들
      - 브로커 한대 죽으면(어떤거든 상관없음) 일단, 리더의 `leader-epoch-checkpoint`의 leader-epoch가 올라간다..
        - 즉, 리더가 변경되거나 선출작업이 나타나면, epoch가 올라간다
      - kill로 죽으면, 리더의 `leader-epoch-checkpoint` 파일의 갱신이 약간은 걸리지만, 무튼 leader-epoch가 올라가고 기존 leader-epoch에 기록된 hwm보다 현재 더 높은 hwm라면 그것또한 반영된다.
        - 우아하게 내려가면 바로바로 반영됨
      - 죽었던 브로커가 다시 살아나게될때, 죽기전의 leader-epoch 상태와 동일하다면 다시 살아난 브로커의 `leader-epoch-checkpoint` 는 새로운 leader-epoch 값으로 갱신되지않는다
        - 새로운 메세지가 들어오면 그때부터 나머지 팔로워들 모두 갱신된다 (시간 지난다고 알아서 맞춰지지않음)
        - 왜 팔로워들의 `leader-epoch-checkpoint`는 기동시에 바로 안바꾸고 메세지가 새로 들어오면 바뀔까?
          - leader-epoch가 더 변경될 가능성이 있어서 그런가
        - 로그참고 (내용이 정리는 안되었고.. 이를 너무 딥하게 들어가는것도 현재 상황에서 큰 의미가 없을 것 같아서 다만 로그만 남겨놓음)
          - 토픽 생성시 로그
            ```log
              kafka-kafka-1-1  | [2023-11-17 00:29:12,210] INFO [Broker id=1] Leader my-topic-0 starts at leader epoch 0 from offset 0 with high watermark 0 ISR [1,3,2] addingReplicas [] removingReplicas []. Previous leader epoch was -1. (state.change.logger)
            ```
          - 팔로워 브로커가 죽었을때 리더 브로커 로그
            ```log
              kafka-kafka-1-1  | [2023-11-17 00:38:22,778] INFO [Broker id=1] Leader my-topic-0 starts at leader epoch 1 from offset 1 with high watermark 1 ISR [1,3] addingReplicas []removingReplicas []. Previous leader epoch was 0. (state.change.logger)
            ```
          - 팔로워의 leader-epoch-checkpoint는 변경이 되지않았으나, 로그상에는 이미 알고있는것으로 보임
            - 리더 파티션있는 브로커: 1, 팔로워 파티션있는 브로커: 3
            ```log
              kafka-kafka-3-1  | [2023-11-17 01:00:03,034] INFO [Broker id=3] Follower my-topic-0 starts at leader epoch 2 from offset 3 with high watermark 3. Previous leader epoch was 1. (state.change.logger)
            ```
      - **아래 내용 좀더 확인필요..**
        - 확실한건 leader-epoch 옆에 잇는값인 startoffset은 리더가 된 뒤에 아직 커밋이 안이루어진 첫 offset값이다 (이건 맞음)
        - 이후에 데이터가 producing되어 나머지 팔로워의 복제가 모두 완료되면 팔로워의 `leader-epoch-checkpoint`가 리더와 동일하게 변경된다 (이건 확실하지않음)
          - ex. 리더가 선출되어 (12 13) -> (13 16)이 되었을때, 데이터가 들어오기전 나머지 팔로워들은 (13 16) 이전인 (12 13)이 마지막으로 셋팅된다. 이후 데이터가 producing이 이루어져서 16 offset이 들어오고 이를 나머지 팔로워들이 복제완료한 상태라면, 나머지 팔로워들의 `leader-epoch-checkpoint` 값도 (13 16)으로 통일된다  
          
  - log-end-offset vs high-watermark
    - https://stackoverflow.com/questions/39203215/kafka-difference-between-log-end-offsetleo-vs-high-watermarkhw
    - log-end-offset은 단순히 Producer에서 마지막으로 발행한 메시지의 offset이고, high water mark는 모든 replica가 복제된 offset이다. 그렇기에, log-end-offset은 아직 복제 되기전까진 high  water mark보다 앞서 있을 수 있다. 참고로 Lag은 consumer-group에서의 offset인 CURRENT-OFFSET을 log-end-offset에서 뺀값이다

    
---

- 5장 프로듀서의 내부동작 원리와 구현
  - 스트키 파티셔닝 전략
    - 프로듀서는 배치 처리를 하게되는데, 라운드로빈 전략을 사용할때, 아직 배치 처리를 위한 버퍼에서 최소 레코드 수를 채우지 못했을 때 대기하고있게됨. (물론, 특정 시간을 초과하면 즉시 카프카로 레코드를 전송하도록 설정 할 수 있지만, 배치의 효과를 놓침)
    - 이를 해결하기위해서 스티키 파티셔닝 전략이 나옴
      - 하나의 파티션에 레코드 수를 먼저 채워서 카프카로 빠르게 배치 전송하는 전략
  - 처리량을 높이려면 batch.size 와 linger.ms의 값을 크게 설정해야 하고, 지연 없는 전송이 목표라면 batch.size 와 linger.ms 의 값을 작게 설정해야한다
    - buffer.memory: 카프카로 메시지들을 전송하기 위해 담아두는 프로듀서의 버퍼 메모리. 기본값 은 32MB로 설정되어 있으며, 관리자는 필요에 따라 설정값을 조정가능. (토픽단위)
    - batch.size: 배치 전송을 위해 메시지(레코드)들을 묶는 단위를 설정하는 배치 크기 옵션. 기본값은 16KB로 설정되어 있으며，관리자는 배치 크기를 더 높이거나 줄 일 수 있습니다. (파티션단위)
    - linger.ms:배치 전송을 위해 버퍼 메모리에서 대기하는 메시지들의 최대 대기시간을 설정하는 옵션. 단위는 밀리초(ms)이며 기본값은 0. 즉 기본값 0으로 설정하면, 배치 전송을 위해 기다리지 않고 메시지들이 즉시 전송. (파티션단위)
  - 배치 사용시 주의점
    - 버퍼 메모리 크기가 커야한다!
      - buffer.memory 크기는 반드시 batch.size 보다 커야함
      - ex. 토픽A가 3개의 파티션을 가지고있다면, 16KB * 3 보다 커야하는데, 전송에 실패시 재시도 수행을 위한 메모리 또한 필요하기떄문에 48보다 좀 더 커야한다
    - 압축기능 사용하면 더욱 효과적
      - 높은 압축률 선호: gzip, zstd
      - 낮은 지연시간 선호: lz4, snappy
  - 메세지 시스템들의 메시지 전송방식
    - 적어도 한 번 전송 (at-least-once)
      - 카프카의 기본동작 방식
    - 최대 한 번 전송 (at-most-once)
    - 정확히 한 번 전송 (exactly-once)
      - 중복없는 전송
        - 카프카에서 전송시 PID(Producer ID)와 메세지번호를 전달하게되는데, 이를 기반으로 kafka에서 이미 전달받은 메시지라면 중복되어 저장하지않고 단순 ACK만 보내는 방식이다.(Producer는 ACK를 못받았다고 생각하고 다시 보낼테니..)
          - 관련정보는 PID와 메세지번호(시퀀스번호)는 ~~.snapshot 파일에 저장된다
          - 실습필요!!
      - 정확히 한번 전송과 트랜잭션과의 관계가뭘까?
        - => 트랜잭션 처리를 위해서 중복없는 전송의 개념을 도입할 수 밖에 없었고, 이에 더해 트랜잭션 API를 활용하여 트랜잭션 처리가 가능하도록 만들었다! 느낌 
    - 카프카에도 트랜잭션을 사용할 수 있다
      - 컨셉
        - 프로듀서가 트랜잭션 처리를 수행하기위해, 트랜잭션 코디네이터라는것이 서버 측에 존재
          - 트랜잭션 코디네이터는 프로듀서에 의해 전송된 메시지를 관리하고 커밋 또는 중단 등을 표시. 말그대로 트랜잭션을 관리하기위해 존재하는놈
        - 트랜잭션 로그를 `__transaction_state` 라는 토픽에 저장
          - 프로듀서가 해당 토픽에 트랜잭션 로그를 직접 기록하는게 아님 (프로듀서는 트랜잭션 코디네이터에게 알리기만)
          - 트랜잭션 코디네이터가 관련 토픽에 직접 기록
        - 트랜잭션에서 커밋된 것인지 실패한 것인지 식별하기위해 `컨트롤 메시지`라는 특별한 메시지를 사용
          - 컨트롤 메시지는 애플리케이션들에게 노출되지않고, 브로커와 클라이언트 통신에서만 사용
      - 어떻게 동작하나?
        - 트랜잭션 코디네이터 찾기
          - 브로커중 하나가 트랜잭션 코디네이터 역할이기때문에, 코디네이터의 위치를 찾아서 트랜잭션 관리에 대한 요청을 보냄
            - 코디네이터의 주 역할은 PID(producer id)와 transactional.id 를 매핑하고 해당 트랜잭션 전체를 관리해준다
          - __transaction_state 토픽에서 transactional.id 를 기반으로 해시하여 파티션 번호가 결정되고, 해당 파티션의 리더가 있는 브로커가 트랜잭션 코디네이터의 브로커
            - 즉, transactional.id가 "a"면 이를 해싱하여 파티션 번호를 매기고, 이 파티션의 리더를 가지고있는 브로커가 해당 트랜잭션 코디네이터 브로커임
            - 그리고 transactional.id가 고유해야하므로, 정확히 하나의 코디네이터만 가지고있게됨 
              - <span style="color:red">(__transaction_state 토픽의 파티션 갯수가 하나라면 모든 트랜잭션이 동일한 파티션으로 가지않을까..?)</span>
        - 프로듀서 초기화 동작
          - TID(Transactional.id)가 트랜잭션 코디네이터에게 전송
          - PID와 TID를 매핑하고 해당 정보를 트랜잭션 로그에 기록
          - 이때 PID 에포크가 하나 올라감.(이전 에포크에 대한 쓰기요청은 무시)
        - 트랜잭션 시작 동작
          - 프로듀서는 내부적으로 트랜잭션이 시작됐음을 기록하지만, 트랜잭션 코디네이터 관점에서는 첫번째 레코드가 전송될 때까지 트랜잭션이 시작된것은 아님
          - 트랜잭션 상태 추가도 일어남
            - 프로듀서는 전송할 토픽 파티션 정보를 트랜잭션 코디네이터에게 전달
            - 그리고 트랜잭션 현재 상태를 **Ongoing**으로 표시
            - 타이머기 시작되어, 1분(기본값)동안 트랜잭션 상태에 대한 업데이트가 없다면, 해당 트랜잭션은 실패로 처리
        - 메시지 전송 동작
          - 특정 토픽(파티션)에 메시지 전송
            - PID, 에포크, 시퀀스번호가 함께 포함되어 전송 (시퀀스 번호는 중복처리 막기위함..)
        - 트랜잭션 종료 요청 동작
          - 프로듀서는 커밋(`commitTransaction()`) 메서드 혹은 취소(`abortTransaction()`) 메서드 중 하나를 반드시 호출해야함
          - 여기서 두가지 동작 진행
            1. 트랜잭션 로그에 해당 트랜잭션에 대한 **PrepareCommit** 또는 **PrepareAbort** 를 기록
               - <span style="color:red">사진상으로는 **Prepare** 라고만 적혀잇는데, 확인필요</span>
            2. 사용자 토픽에 표시하는단계
               - 트랜잭션 코디네이터의 상태는 **Prepare**
               - 트랜잭션 코디네이터는 두번째 단계로, 프로듀서가 보낸 topic에 트랜잭션 커밋 표시를 기록하는데, 여기서 기록하는 메시지가 컨트롤 메시지
               - 프로듀서가 보낸 메시지의 오프셋이 1이였다면, 컨트롤 메시지로 인하여 오프셋 2로 증가함
                 - 이는 메시지가 제대로 전송됐는지 여부를 컨슈머에게 나타내는 용도로도 사용된다함
               - 트랜잭션 커밋이 끝나지 않은 메시지는 컨슈머에게 반환하지않고, 오프셋 순서 보장을 위해 트랜잭션 성공 또는 실패를 나타내는 LSO(last stable offset)라는 오프셋을 유지
                 - **이거 무슨말???**
        - 트랜잭션 완료
          - 트랜잭션 코디네이터는 **Committed** 라고 트랜잭션 로그에 기록
          - 프로듀서에게 트랜잭션 완료됨을 알린 뒤에 트랜잭션 마무리
          - 트랜잭션을 이용하는 컨슈머는 read_commited 설정을 하면 트랜잭션에 성공한 메시지들만 읽을수있다
    - 주의 사항일것 같은거?
      - 카프카에서 트랜잭션을 사용한 원자적 전송은 토픽의 특정 파티션으로 보내줘야 처리에 순서가 보장될 것으로 보임.. 즉, 순차적인 처리가 반드시 보장되어야한다면 2개 이상의 파티션을 사용하면 안됨 (물론, 파티션에 key를 지정하면 상관없겠지)

- 트랜잭션 전송 테스트
  - transaction commit시 로그
    - transaction-topic(트랜잭션을 적용하고자 하는 토픽) 로그
      ```log
        baseOffset: 60 lastOffset: 60 count: 1 baseSequence: 0 lastSequence: 0 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 4627 CreateTime: 1700726758751 size: 86 magic: 2 compresscodec: NONE crc: 1272500548 isvalid: true 
        | offset: 60 CreateTime: 1700726758751 keysize: -1 valuesize: 18 sequence: 0 headerKeys: [] payload: Hello Kafka TX - 0
        baseOffset: 61 lastOffset: 61 count: 1 baseSequence: 1 lastSequence: 1 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 4713 CreateTime: 1700726831332 size: 86 magic: 2 compresscodec: NONE crc: 2714197131 isvalid: true 
        | offset: 61 CreateTime: 1700726831332 keysize: -1 valuesize: 18 sequence: 1 headerKeys: [] payload: Hello Kafka TX - 1
        baseOffset: 62 lastOffset: 62 count: 1 baseSequence: 2 lastSequence: 2 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 4799 CreateTime: 1700726836350 size: 86 magic: 2 compresscodec: NONE crc: 4164575494 isvalid: true 
        | offset: 62 CreateTime: 1700726836350 keysize: -1 valuesize: 18 sequence: 2 headerKeys: [] payload: Hello Kafka TX - 2
        baseOffset: 63 lastOffset: 63 count: 1 baseSequence: 3 lastSequence: 3 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 4885 CreateTime: 1700726836882 size: 86 magic: 2 compresscodec: NONE crc: 1609077180 isvalid: true 
        | offset: 63 CreateTime: 1700726836882 keysize: -1 valuesize: 18 sequence: 3 headerKeys: [] payload: Hello Kafka TX - 3
        baseOffset: 64 lastOffset: 64 count: 1 baseSequence: 4 lastSequence: 4 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 4971 CreateTime: 1700726837407 size: 86 magic: 2 compresscodec: NONE crc: 107707236 isvalid: true 
        | offset: 64 CreateTime: 1700726837407 keysize: -1 valuesize: 18 sequence: 4 headerKeys: [] payload: Hello Kafka TX - 4
        baseOffset: 65 lastOffset: 65 count: 1 baseSequence: -1 lastSequence: -1 producerId: 13 producerEpoch: 11 partitionLeaderEpoch: 0 isTransactional: true isControl: true position: 5057 CreateTime: 1700726838085 size: 78 magic: 2 compresscodec: NONE crc: 1068357547 isvalid: true 
        | offset: 65 CreateTime: 1700726838085 keysize: 4 valuesize: 6 sequence: -1 headerKeys: [] endTxnMarker: COMMIT coordinatorEpoch: 0

        baseOffset: 66 lastOffset: 66 count: 1 baseSequence: 0 lastSequence: 0 producerId: 13 producerEpoch: 12 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 5135 CreateTime: 1700726951174 size: 86 magic: 2 compresscodec: NONE crc: 2908803872 isvalid: true 
        | offset: 66 CreateTime: 1700726951174 keysize: -1 valuesize: 18 sequence: 0 headerKeys: [] payload: Hello Kafka TX - 0
        baseOffset: 67 lastOffset: 67 count: 1 baseSequence: -1 lastSequence: -1 producerId: 13 producerEpoch: 12 partitionLeaderEpoch: 0 isTransactional: true isControl: true position: 5221 CreateTime: 1700727006678 size: 78 magic: 2 compresscodec: NONE crc: 3781935704 isvalid: true 
        | offset: 67 CreateTime: 1700727006678 keysize: 4 valuesize: 6 sequence: -1 headerKeys: [] endTxnMarker: COMMIT coordinatorEpoch: 0
      ```
    - __transaction_state 로그
    ```log
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=11, txnTimeoutMs=60000, state=Empty, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=-1, txnLastUpdateTimestamp=1700726737432)                                   // producer.inltTransactlons(); 호출시 찍힘
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=11, txnTimeoutMs=60000, state=Ongoing, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700726792453, txnLastUpdateTimestamp=1700726792453) // producer.flush(); 호출시 찍힘 (즉, 레코드 처음 전송했을때 기록.. producer.beginTransaction() 메서드를 호출할떄 찍히는것이 아니다!!)
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=11, txnTimeoutMs=60000, state=PrepareCommit, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700726792453, txnLastUpdateTimestamp=1700726838082) // producer.commitTransaction(); 호출시 찍히고 
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=11, txnTimeoutMs=60000, state=CompleteCommit, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=1700726792453, txnLastUpdateTimestamp=1700726838085)                    // transaction 코디네이터에 transaction-topic에 트랜잭션 완료했다는 컨트롤메세지 전송한 뒤 남기는듯함


      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=12, txnTimeoutMs=60000, state=Empty, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=-1, txnLastUpdateTimestamp=1700726910801)
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=12, txnTimeoutMs=60000, state=Ongoing, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700726968386, txnLastUpdateTimestamp=1700726968386)
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=12, txnTimeoutMs=60000, state=PrepareCommit, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700726968386, txnLastUpdateTimestamp=1700727006672)
      Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=12, txnTimeoutMs=60000, state=CompleteCommit, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=1700726968386, txnLastUpdateTimestamp=1700727006677)
    ```
  - Transaction abort시 로그
    - transaction-topic(트랜잭션을 적용하고자 하는 토픽) 로그
      ```log
        baseOffset: 68 lastOffset: 68 count: 1 baseSequence: 0 lastSequence: 0 producerId: 13 producerEpoch: 13 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 5299 CreateTime: 1700727227285 size: 86 magic: 2 compresscodec: NONE crc: 2786302459 isvalid: true 
        | offset: 68 CreateTime: 1700727227285 keysize: -1 valuesize: 18 sequence: 0 headerKeys: [] payload: Hello Kafka TX - 0
        baseOffset: 69 lastOffset: 69 count: 1 baseSequence: -1 lastSequence: -1 producerId: 13 producerEpoch: 13 partitionLeaderEpoch: 0 isTransactional: true isControl: true position: 5385 CreateTime: 1700727241674 size: 78 magic: 2 compresscodec: NONE crc: 2908531184 isvalid: true 
        | offset: 69 CreateTime: 1700727241674 keysize: 4 valuesize: 6 sequence: -1 headerKeys: [] endTxnMarker: ABORT coordinatorEpoch: 0    
      ```
    - __transaction_state 로그
      ```log
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=13, txnTimeoutMs=60000, state=Empty, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=-1, txnLastUpdateTimestamp=1700727219675)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=13, txnTimeoutMs=60000, state=Ongoing, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727234082, txnLastUpdateTimestamp=1700727234082)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=13, txnTimeoutMs=60000, state=PrepareAbort, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727234082, txnLastUpdateTimestamp=1700727241671)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=13, txnTimeoutMs=60000, state=CompleteAbort, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=1700727234082, txnLastUpdateTimestamp=1700727241673)    
      ```

  - init 한번 여러번 begin transaction & commit 로그
    - transaction-topic(트랜잭션을 적용하고자 하는 토픽) 로그
      ```log
        baseOffset: 70 lastOffset: 70 count: 1 baseSequence: 0 lastSequence: 0 producerId: 13 producerEpoch: 14 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 5463 CreateTime: 1700727434517 size: 86 magic: 2 compresscodec: NONE crc: 861819146 isvalid: true 
        | offset: 70 CreateTime: 1700727434517 keysize: -1 valuesize: 18 sequence: 0 headerKeys: [] payload: Hello Kafka TX - 0
        baseOffset: 71 lastOffset: 71 count: 1 baseSequence: -1 lastSequence: -1 producerId: 13 producerEpoch: 14 partitionLeaderEpoch: 0 isTransactional: true isControl: true position: 5549 CreateTime: 1700727438461 size: 78 magic: 2 compresscodec: NONE crc: 4123285463 isvalid: true 
        | offset: 71 CreateTime: 1700727438461 keysize: 4 valuesize: 6 sequence: -1 headerKeys: [] endTxnMarker: COMMIT coordinatorEpoch: 0
        baseOffset: 72 lastOffset: 72 count: 1 baseSequence: 1 lastSequence: 1 producerId: 13 producerEpoch: 14 partitionLeaderEpoch: 0 isTransactional: true isControl: false position: 5627 CreateTime: 1700727444247 size: 86 magic: 2 compresscodec: NONE crc: 3445833227 isvalid: true 
        | offset: 72 CreateTime: 1700727444247 keysize: -1 valuesize: 18 sequence: 1 headerKeys: [] payload: Hello Kafka TX - 0
        baseOffset: 73 lastOffset: 73 count: 1 baseSequence: -1 lastSequence: -1 producerId: 13 producerEpoch: 14 partitionLeaderEpoch: 0 isTransactional: true isControl: true position: 5713 CreateTime: 1700727448419 size: 78 magic: 2 compresscodec: NONE crc: 612415112 isvalid: true
        | offset: 73 CreateTime: 1700727448419 keysize: 4 valuesize: 6 sequence: -1 headerKeys: [] endTxnMarker: COMMIT coordinatorEpoch: 0    
      ```
    - __transaction_state 로그
      ```log
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=Empty, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=-1, txnLastUpdateTimestamp=1700727429250)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=Ongoing, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727436443, txnLastUpdateTimestamp=1700727436443)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=PrepareCommit, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727436443, txnLastUpdateTimestamp=1700727438458)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=CompleteCommit, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=1700727436443, txnLastUpdateTimestamp=1700727438460)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=Ongoing, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727445676, txnLastUpdateTimestamp=1700727445676)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=PrepareCommit, pendingState=None, topicPartitions=HashSet(transaction-topic-0), txnStartTimestamp=1700727445676, txnLastUpdateTimestamp=1700727448415)
        Lee-Dong-Hee::TransactionMetadata(transactionalId=Lee-Dong-Hee, producerId=13, producerEpoch=14, txnTimeoutMs=60000, state=CompleteCommit, pendingState=None, topicPartitions=HashSet(), txnStartTimestamp=1700727445676, txnLastUpdateTimestamp=1700727448418)    
      ```


- 카프카 스트림즈도 exactly once를 보장하도록 할 수 잇는데, transaction api를 사용하여 가능
  - https://velog.io/@youngerjesus/Kafka-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%B2%98%EB%A6%AC-Exactly-Once

---

- 6장 컨슈머의 내부동작 원리와 구현
  - __consumer_offsets 토픽에 컨슈머가 어디까지 읽었는지 정보를 저장 (consumer 그룹별 commit한 offset)
    - 여기서 저장되는 오프셋값은 컨슈머가 마지막까지 읽은 위치가 아니라 컨슈머가 다음으로 읽어야할 위치 (하이워터마크..?)
  - 컨슈머그룹
    - 컨슈머들의 변화를 인지하여 컨슈머들에게 작업을 고르게 분배해줌 (컨슈머 리벨런싱)
      - **컨슈머들의 변화인지는 그룹코디네이터가 해주는거아닌가?**
  - 그룹 코디네이터
    - 안정적인 컨슈머 그룹 관리를 위해 존재
    - 컨슈머 그룹이 구독한 토픽의 파티션들과 그룹의 멤버들을 트래킹하여 컨슈머그룹이 리밸런싱 하도록 도와줌
    - 그룹 코디네이터는 각 컨슈머 그룹별로 존재하며, 이러한 그룹 코디네이터는 카프카 클러스터 내의 브로커 중 하나에 위치
  - 그룹코디네이터를 통해서 컨슈머그룹의 컨슈머들이 토픽의 파티션을 할당받는 절차
    - 등록된 bootstrap.broker 리스트에 있는 브로커에게 컨슈머 클라이언트와 초기 커넥션을 위한 요청을 보냄
    - 해당 요청을 받은 브로커는 그룹코디네이터를 생성 (첫번째 컨슈머가 등록될 때까지 아무작업 일어나지않음)
    - 컨슈머는 자신을 등록해달라는 요청을 그룹 코디네이터에게 보내는데, 이때 그룹코디네이터에게 처음 요청한 컨슈머가 컨슈머 그룹의 리더가됨
      - 이때, 그룹코디네이터는 응답으로 토픽의 파티션 정보를 컨슈머 리더에게 전달해줌
    - 컨슈머 리더가 전달받은 토픽의 파티션정보들을 가지고 파티션 할당 전략에 따라 컨슈머들에게 파티션할당한 뒤 그룹 코디네이터에게 이렇게 할당하기로했다고 알려줌
    - 그룹 코디네이터는 이를 캐싱하고, 컨슈머들에게 성공을 알림 (**각 컨슈머들에게 어떤 파티션을 받으면되는지 상세주소를 알려줌?** => 좀더 찾아볼것)
    - 각 컨슈머들은 지정된 토픽 파티션들로부터 데이터 컨슘
    - => 이후 컨슈머가 그룹코디네이터에게 leave나 join 요청을 보내면서 컨슈머에 변동이 생길때마다 컨슈머들을 조정하는 리밸런싱이 일어난다
      - leave 요청못보내고 죽으면?
        - 컨슈머들의 변경을 감지하기 위해 그룹 코디네이터와 컨슈머들은 서로 하트비트를 주고받는다
        - 하트비트 관련 참고 옵션 (consumer 용)
          - heartbeat.interval.ms
            - 기본값은 3000이며, 그룹 코디네이터와 하트비트 인터벌 시간입니다. 해당 시간은 session.timeout.ms보다 낮게 설정해야 하며, 3분의 1수준이 적절
          - session.timeout.ms
            - 기본값은 10000입니다. 어떤 컨슈머가 특정 시간 안에 하트비트를 받지 못하면 문제가 발생했다고 판단해 컨슈머 그룹에서 해당 컨슈머는 제거되고 리밸런싱 동작
          - max.poll.interval.ms
            - 기본값은300000입니다. 컨슈머는주기적으로 poll()을 호출해 토픽으로부터 레코드들을 가져오는데, poll() 호출 후 최대 5분간 poll() 호출이 없다면 컨슈머가 문제가 있는 것으로 판단해 리밸런싱 동작이 일어납니다.
            - => 이렇게 별도로 이런 옵션을 두는것은, heartbeat를 보내는것은 별도 스레드로 동작하기때문에 heartbeat는 문제없지만, poll을 어떤 이유로인하여 오랜시간 안하게되었을때 문제가 있다고 판단하여 리밸런싱을 하기위함.



---


20231116 과제
- 모두 복제가 완료되어야만 consumer가 consuming을 진행하나?
  - 챗gpt 답변
    ```md
        Kafka에서 컨슈머가 메세지를 topic으로부터 Fetch해올 수 있는 시점은 리더 브로커가 메세지를 커밋한 시점입니다. 팔로워 브로커와 리더 브로커가 복제와 커밋을 분리하여 수행하기 때문에, 리더 브로커에서 메세지를 커밋한 후에 해당 메세지가 컨슈머에게 제공됩니다.
        팔로워 브로커에서의 커밋은 데이터를 복제하기 위한 과정에 관련되어 있으며, 컨슈머에게 직접적으로 영향을 미치지 않습니다. 컨슈머가 메세지를 Fetch하는 시점은 리더 브로커가 커밋한 시점이며, 이것이 컨슈머에게 전달되는 메세지의 시점이 됩니다.
    ```
- replication-offset-checkpoint 는 왜 늦게 올라가나?
  - 페이지캐시
- 5장 읽기
- 스터디 하다가 유레카!!! 있으면 공유

20231123 과제
- 5장 읽기 + 실습





---

카프카 도입시 고려해야할 부분

- 중복 메시징 이슈
  - 중복메시지 걸러내는 로직구현
    - subscribe_id와 msg_id 로 구성되어있는 PROCESSED_MESSAGE 테이블(이미 처리된 대상이 있는 테이블)을 만들어준다!
- 메시지 순서 보장을 위해 고려해야할 점
  - 순서보장이 필요한 이벤트에는 동일한 메세지 key를 사용하여 발행
- 트랜잭셔널 메시징을 지킬 수 있는 개발 방법
  - 비동기 메시징을 활용한 서비스 구현에서, 비지니스 로직 완료 후(완료 이벤트 발행) 그 다음 진행해야할 로직이 있을때 사용
  - 트랜잭셔널 아웃박스 패턴 (Transactional Outbox Pattern)
    - Transactional Outbox Pattern은 데이터베이스와 메시지 큐를 사용하는 분산 시스템에서 데이터 일관성을 유지하는 패턴. 이 패턴은 데이터베이스 작업과 메시지 전송을 하나의 트랜잭션으로 묶어 처리
    - outbox 테이블 생성하여 실제 상태변경에 따른 DB 수정과 같은 작업을 완료하여야 outbox 테이블에 데이터가 들어가게되고, 이를 relay 해주는 작업(하나의 어플리케이션이 될 수도 있으며, outbox 테이블을 주기적으로 조회하고 완료된 대상에 대해서 메세지 발행)을 수행하여, db에 변경된 작업이 온전히 수행된것을 보장한 대상에 대해서 이벤트를 발행(메시지 큐)한다.
    - 이를 통해 메시지 큐와 분산 시스템에서 데이터 일관성을 유지함
    - `transactional outbox pattern.md` 참고
  - 변경 데이터 캡쳐 (Change Data Capture - CDC)
    - Debezium은 CDC 지원하는 대표적인 플랫폼인데, mysql의 binlog와 같은 트랜잭션 로그를 활용해서 변경된 데이터를 확인하고 이후 이를 메세지를 변환하여 타겟으로 설정한 카프카로 발행가능
    - 별도의 Debezium 학습과 운영을 위한 비용필요
    - 스키마 변경될때..?


- 참고사이트
  - https://medium.com/@greg.shiny82/%EC%8B%A4%EB%AC%B4-%EA%B4%80%EC%A0%90%EC%97%90%EC%84%9C%EC%9D%98-apache-kafka-%ED%99%9C%EC%9A%A9-023d468f9182
  - https://medium.com/@greg.shiny82/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%94%EB%84%90-%EC%95%84%EC%9B%83%EB%B0%95%EC%8A%A4-%ED%8C%A8%ED%84%B4%EC%9D%98-%EC%8B%A4%EC%A0%9C-%EA%B5%AC%ED%98%84-%EC%82%AC%EB%A1%80-29cm-0f822fc23edb
  - https://microservices.io/patterns/data/transactional-outbox.html
  - [왜 트랜잭셔널 아웃박스 패턴이 필요한지 상세하게 잘 설명](https://blog.gangnamunni.com/post/transactional-outbox/)
    - 좀더 보고 위에 정리해놓을것




- 카프카 활용
  - 실시간 좌석 입장 화면 보여주기
  - 구조
  - 구조에 들어가는 기술들
    - kafka consumer vs kafka streams
    - vert.x? 다른 웹소켓?
    - https://junuuu.tistory.com/729
    - 웹소켓 말고 SSE(Server-Sent Events)도 있음
      - https://hamait.tistory.com/792