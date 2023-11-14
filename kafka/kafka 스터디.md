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


- 확인내용
  - `replication-offset-checkpoint` 에서 특정 토픽확인시, 리더가 팔로워들보다 더 늦게 번호가 올라간다.. 팔로워들이 복제가 완료되었다는것을 알게된 뒤에 올리는듯? 그럼, 팔로워들은 복제하자마자 커밋번호를 올리나..
  - `leader-epoch-checkpoint` 는 메세지 보낼때마다 올라가지않음.. 
    - leader-epoch를 언제사용하나?
      - leader-epoch는 이전 leader-epoch의 데이터가 보내질때 무시하기위한 용도로 사용.. 즉, 현재 leader-epoch가 아닌 이전의 숫자로 어떤 데이터가 들어오게되면 이는 무시된다..
      - https://stackoverflow.com/questions/50342961/what-is-a-role-of-leader-epoch-checkpoint-in-log-segment-of-kafka-partition
    - 브로커 한대 죽으면(어떤거든 상관없음) 일단, 리더의 `leader-epoch-checkpoint`의 leader-epoch가 올라간다.. ~~그리고 startoffset도 하나 증가~~ (팔로워의 `leader-epoch-checkpoint`는 변동없음. 오직 리더만!)
    - 또한 내부적으로 죽었다가 살아나면, 알아서 다시 기존 leader였던 브로커로 돌아가게되는데, 그때도 `leader-epoch-checkpoint`의 leader-epoch는 올라가게된다
      - 확실한건 leader-epoch 옆에 잇는값인 startoffset은 리더가 된 뒤에 아직 커밋이 안이루어진 첫 offset값이다
      - 이후에 데이터가 producing되어 나머지 팔로워의 복제가 모두 완료되면 팔로워의 `leader-epoch-checkpoint`가 리더와 동일하게 변경된다
        - ex. 리더가 선출되어 (12 13) -> (13 16)이 되었을때, 데이터가 들어오기전 나머지 팔로워들은 (13 16) 이전인 (12 13)이 마지막으로 셋팅된다. 이후 데이터가 producing이 이루어져서 16 offset이 들어오고 이를 나머지 팔로워들이 복제완료한 상태라면, 나머지 팔로워들의 `leader-epoch-checkpoint` 값도 (13 16)으로 통일된다       
    - 하.. 책에 나온설명.. 좀 이상하다.. 좀더 공식문서 찾아봐야할듯..





- high-watermark의 동작원리
- commit은 어떻게 이루어지나
- replication은 어떻게 이루어지나
  - 카프카 내부에서 파일로 어떻게 관리되나
- `replication-offset-checkpoint` 
  - 가장 최근 커밋된 즉, 복제된 메세지의 오프셋 high watermark이다
    - https://medium.com/@darefamuyiwa/kafka-storage-internals-36606917629c#:~:text=replication%2Doffset%2Dcheckpoint%3A%20Where,the%20last%20committed%2C%20replicated%20message.
  - producer가 특정 topic 메세지를 보내면, 해당 파일에서 하나가 증가된다.
  - 그런데, 0번 offset에 데이터가 들어가게되면, 저장되는건 `{파티션번호} 1` 이렇게 들어간다. 즉, 0이 들어가지않는다..
    - 처음 0으로 셋팅이 되어있고.. 데이터를 producing하고 난 뒤, 모든 replica(팔로워)들이 복제가 완료되었다고하면, 복제완료되었으니 1을 올리게되는데, 그러다보니 실제 저장된 offset과 high-watermark라 불리는 replication-offset-checkpoint간에 1의 차이가 있는건가..?
    - [HWM를 replication과 consumer에서 사용됨을 잘 설명](https://levelup.gitconnected.com/high-water-mark-hwm-in-kafka-offsets-5593025576ac)
    - 하이워터마크라는 용어 유래 (챗gpt)
      ```md
        하이워터마크(High Watermark) 용어의 이름은 그 기능과 역할에서 유래되었습니다. 이 용어는 실제로 수위계와 관련이 있습니다. 아래와 같은 비유를 사용하여 이해할 수 있습니다.
        1. **수위계와 유사성:** 하이워터마크는 데이터 스트림 또는 큐에서 어떤 지점까지 데이터가 도달했는지를 추적합니다. 이것은 물의 수위를 추적하는 수위계와 유사한 개념입니다. 수위계가 강이나 호수의 수위를 측정하듯이, 하이워터마크는 데이터 처리의 진행 상태를 측정합니다.
        2. **고수위와 저수위:** 하이워터마크는 "고수위"와 "저수위"로 나뉩니다. "고수위"는 데이터 스트림에서 가장 높은 위치를 나타내며, 데이터 처리의 가장 최근 상태를 나타냅니다. "저수위"는 데이터 스트림에서 가장 낮은 위치를 나타내며, 처리가 완료된 데이터를 나타냅니다.
        3. **데이터 처리의 흐름:** 하이워터마크는 데이터 스트림이나 큐를 따라 이동하면서 업데이트됩니다. 데이터가 처리되면 하이워터마크가 이동하며, 가장 최근 데이터가 어디까지 처리되었는지를 나타냅니다.
        이처럼 하이워터마크는 데이터 처리의 상태를 시각화하고 추적하는 데 사용되며, 그 이름은 이 개념에서 수위를 추적하는 것과 유사성 때문에 붙여졌습니다.
      ``` 



      kafka-topics --bootstrap-server kafka-1:29092 --alter --topic my-topic3 --config min.insync.replicas=1


      kafka-configs --bootstrap-server kafka-1:29092 --alter --entity-type topics --entity-name my-topic3 --add-config min.insync.replicas=1


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
    - => 브로커 복구시 하이워터마크로만의 한계점을 리더에포크로 보완했다고 보면 된다
      - 어떤 한계?
        - 리더와 팔로워의 하이워터마크가 불일치 되는 시점에서 브로커의 복구시 리더에포크가 사용
        - ex. 리더와 팔로워간에 데이터 복제는 되었지만, 리더만 커밋이 되었고 아직 팔로워는 커밋이 되지못한 상태일때, 만약 팔로워 브로커가 다운되어 복구되면 팔로워는 기동시 커밋된 부분까지만 메세지를 살린다. 즉, 이미 복제되었던 메세지를 날린다. 그때에, 커밋이 완료된 리더로부터 메세지를 받지못한 채 리더 브로커가 죽었다면, 팔로워 브로커는 리더가 될 것이고 새로 선출된 리더는 메세지 하나가 없는 상태가 된다. 하지만, 리더 에포크를 사용하게되면 팔로워 브로커가 기동시에 커밋된 부분까지만 메세지를 살리는게아닌, 리더 에포크를 통해 현재 리더의군집(리더에포크)가 어떤 메세지까지 커밋되었는지를 보고 해당 부분까지 커밋을 수행. 즉, 팔로워가 기동되면서 현재 리더의 커밋까지 커밋읆 맞춘다. (하이워터마크 상향조정) 이를 통해 유실을 방지할 수 있게된다.
    - 테스트간에 발견한 내용들
      - 브로커 한대 죽으면(어떤거든 상관없음) 일단, 리더의 `leader-epoch-checkpoint`의 leader-epoch가 올라간다..
        - 즉, 리더가 변경되거나 선출작업이 나타나면, epoch가 올라간다

  - log-end-offset vs high-watermark
    - https://stackoverflow.com/questions/39203215/kafka-difference-between-log-end-offsetleo-vs-high-watermarkhw
    - log-end-offset은 단순히 Producer에서 마지막으로 발행한 메시지의 offset이고, high water mark는 모든 replica가 복제된 offset이다. 그렇기에, log-end-offset은 아직 복제 되기전까진 high  water mark보다 앞서 있을 수 있다. 참고로 Lag은 consumer-group에서의 offset인 CURRENT-OFFSET을 log-end-offset에서 뺀값이다

11/16 (목)
- 모두 복제가 완료되어야만 consumer가 consuming을 진행하나?
  - 챗gpt 답변
    ```md
        Kafka에서 컨슈머가 메세지를 topic으로부터 Fetch해올 수 있는 시점은 리더 브로커가 메세지를 커밋한 시점입니다. 팔로워 브로커와 리더 브로커가 복제와 커밋을 분리하여 수행하기 때문에, 리더 브로커에서 메세지를 커밋한 후에 해당 메세지가 컨슈머에게 제공됩니다.
        팔로워 브로커에서의 커밋은 데이터를 복제하기 위한 과정에 관련되어 있으며, 컨슈머에게 직접적으로 영향을 미치지 않습니다. 컨슈머가 메세지를 Fetch하는 시점은 리더 브로커가 커밋한 시점이며, 이것이 컨슈머에게 전달되는 메세지의 시점이 됩니다.
    ```
- replication-offset-checkpoint 는 왜 늦게 올라가나?
- 5장 읽기
- 스터디 하다가 유레카!!! 있으면 공유