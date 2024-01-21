[Redis 개념정리](https://medium.com/@jyejye9201/%EB%A0%88%EB%94%94%EC%8A%A4-redis-%EB%9E%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80-2b7af75fa818)

[약간은 깊이있는 redis정리(공부하기좋음)](https://daddyprogrammer.org/post/1601/redis-cluster/)

[명령어정리](https://jhhwang4195.tistory.com/187)

[spring boot 사용방법정리](https://dydtjr1128.github.io/redis/2019/04/03/Redis.html)

[spring boot 사용방법정리(좀더 이해하기쉽게 정리)](https://ssoco.tistory.com/19)


- nosql,, insert,select 겁나빠름.. master-slave 기능이있어서 이중화가능.. 수많은 언어의 api제공.. 성능이 좋다..
- redisTemplate이란놈이 다양한 redis 작업과 예외변환, 직렬화지원..
- cluster 지원한다함..


- "Redis ERR Client sent Auth, But no password is set" 해결책
  - => https://blusky10.tistory.com/276

- 여러키 한번에 삭제
   - xargs 리눅스명령어 사용
   - 참고사이트 : https://johnmarc.tistory.com/125

- 기본적인 내용 잘 나와있음 : https://cheese10yun.github.io/redis-getting-started/



- Redis client인 jedis, lettuce 비교 : https://jojoldu.tistory.com/418
  - 이거보면서 성능어떻게 체크하는지도 참고할것

  
- 우아한 레디스 (by 강대명)
  - Redis 소개
    - in-memory data structure
    - support data structure
      - strings, set, sorted-set, hashes, list
      - Hyperloglog, bitmap, geospatial index
      - stream
  - 어디서 사용?
    - 캐시!
      - 우리의 사용자 요청은 다 새로운 사람이 아니라, 20%의 사용자가 전체 요청의 80%이기 때문에 캐시가 중요!
      - 캐시의 종류
        - Look aside cache
          - DB에서 데이터 가져오기전에 캐시에서 확인하고 없으면 DB에서 읽은뒤 캐시에 넣고 반환
        - Write Back
          - 캐시에 모든 데이터를 저장해놓고 특정 주기에 맞추어 DB에 insert
          - DB애 insert안한 데이터들이 Redis에 남아있을때, 메모리가 문제 생기면 유실의 위험이있음
  - Redis는 Collection을 제공!
    - 개발의 편의성 굿
      - 랭킹 구현을 redis에서 제공해주는 sorted set으로 구현하면끝
    - 개발의 난이도
      - 동시에 접근할때 Race Condition(동시 접근에 따른 데이터 안정성 문제..) 이 나타날수있는데, Redis의 경우 자료구조가 atomic 하기때문에 이를 피할수있다!
        - 그래도 잘못짜면 나타난다함
  - Redis Collections
    - Strings
      - key value 로 저장
      - key를 어떻게 잡을것인가?
        - 보통은 prefix를 붙임 (key를 어떻게 잡느냐에따라 분산이 나눠진다네..)
    - List
      - 앞에 데이터를 넣거나 뒤에 넣을수있음
      - `LPUSH <key> <data>`
      - `RPUSH <key> <data>`
      - `LPOP <key>`
      - `RPOP <key>`
    - Set
      - 중복안하려고
      - `SADD <key> <value>`
        - 이미 key에 데이터 있으면 추가 안함
      - `SMEMBERS <key>`
        - 모든 value 돌려줌
        - 이런거 조심.. 데이터많으면 느려질수도
      - `SISMEMBER <key> <value>`
        - value가 존재하면 1, 없으면 0
    - Sorted Set
      - 순서보장가능
      - `ZADD <key> <score> <value>`
        - value가 이미 key에 있으면 해당 score로 변경
        - score는 숫자값 (asc로 정렬)
        - score 같으면 value로 sort
      - `ZRANGE <key> <startIndex> <endIndex>`
        - 해당 index 범위 값을 모두 돌려줌
        - Zrange testkey 0 -1
          - 모든 범위 가져옴
        - Zrange testkey 0 2
          - 0, 1 값 가져옴
      - *score는 실수형이기때문에 주의가필요..
        - 실수형은 값이 정확하지않을수있다고 하는데 그럼 어떻게 해야하는지..?
    - Hash
      - key value(key value) 로 사용
      - `Hset <key> <subkey> <value>`
      - `Hmset <key> <subkey1> <value1> <subkey2> <value2>`
        - 여러개 등록
      - `Hgetall <key>` 
        - 해당 key의 모든 subkey와 value 가져옴
      - `Hget <key> <subkey>`
      - `Hmget <key> <subkey1> <subkey2> ... <subkeyN>`
      - Hash vs strings
        - Hashes을 이용하여 매핑 만들기 ( Strings VS Hashes ) - https://sabarada.tistory.com/m/135
  - Collection 주의사항
    - 하나의 컬렉션에 너무많은 아이템을 담으면 좋지않음
      - 1만개 이하 몇천개 수준이 저걸..
    - expire는 Collection item 개별로 걸리지않고 전체 Collection에 대해서만 걸림
      - 1만개의 아이템을 가진 collection에 expire가 걸려있다면 그 시간 후에 1만개 아이템 모두 삭제
  - Redis 운영
    - 메모리 관리를 잘하자
      - Redis는 in-memory data structure
      - physical memory 이상을 사용하면 문제발생
        - swap이 있다면 swap 사용으로 해당 메모리 Page 접근시마다 늦어짐
          - swap은 메모리 페이지를 디스크에 저장해놓고 필요시 다시 읽어오는방법.. 그래서 느려짐 (이제 완전 메모리를 사용하는것이아니니깐..)
        - swap 없으면..?
          - OOM
      - Maxmemory를 설정하더라도 이보다 더 사용할 가능성이 큼
        - 이 메모리 보다 더 쓰게되면, expire 목록을 지우거나 랜덤하게 key 지워버림
      - Redis는 메모리 파편화가 발생할수있음..
        - 메모리를 할당받을때 페이지 단위로 할당받기때문에, 1byte가 필요해도 4096(1페이지가 4096임을 가정)을 가져온다.. 이런 현상이 많이 발견되면 실제 redis에서 사용하는 메모리는 많지않은데 물리 메모리를 바닥을 칠수가있음.. 이것을 메모리 파편화라고 함
          - 3.x대 버전은 자주 발생했다함.. 
      - 메모리 줄이기 위한 설정
        - 기본적으로 Collection들은 다음과 같은 자료구조 사용
          - Hash -> HashTable을 하나 더 사용
          - SortedSet -> SkipList와 HashTable을 이용
          - Set -> HashTable 사용
          - 해당 자료구조들은 메모리를 많이 사용
        - Ziplist를 이용하자!
          - 이를 사용하는것은 명령어를 바꾸는것이아니라, redis에 내부 저장하는 방식을 바꾸는것임.. 즉 설정값을 바꿔주면됨
          - Ziplist는 선형탐색을 하게되는데, 인메모리 특성상 어느정도의 개수는 선형탐색을 하더라도 빠르다!
    - O(N) 관련 명령어 주의!
      - Redis는 싱글스레드
        - 한번에 하나의 명령만 수행가능
          - 긴 시간이 필요한 명령쓰면 망한다..!
            - 대표적인 O(N) 명령들
              - keys 
                - key가 100만개 이상인데 확인을 위해 keys 명령사용..
                - => scan을 사용하라!
                  - scan은 끊어서 가져오도록해줌.. 
                - => Collection의 일부만 가져오도록
                  - Sorted Set은 score에서 일부 가져올수있으니..
                - => 큰 Collection을 작은 여러개의 Collection으로 나눠서 저장
                  - 하나당 몇천개 안쪽으로 저장하는게 좋음
              - flushall, flushdb 
              - Delete Collections
              - Get All Collections
        - 참고로 단순한 get/set은 초당 10만 TPS 이상 가능(당연 CPU 영향받음..)
    - Redis Replication
      - Async Replication
        - Replication lag 이 발생할수있음
          - slave가 master의 데이터를 못따라가는 현상
    - redis.conf 권장설정 tip
      - Maxclient 설정 50000
      - RDB/AOF 설정 off
      - 특정 커맨드 disable
        - keys
      - 장애의 90프로이상이 keys명령어 사용과 Save 설정 때문임..
        - Save 설정 : 1분 안에 key가 1만개 바뀌면 덤프해.. 뭐 이런.. 그래서 RDB/AOF는 꺼라! 

  - Redis 데이터분산
  - Redis cluster
    - VIP/DNS 기반 사용시 장점 및 주의할점
      - 클라이언트의 추가적인 구현이 필요없음(에러 떨어지면 재시도해서 연결하니께)
      - DNS 기반은 DNS Cache TTL을 잘 관리해야함
        - 사용하는 언어별 DNS 캐싱정책을 잘 알아야함
        - 툴에 따라서 한번 가져온 DNS 정보를 다시 호출하지않는 경우도 존재
    - Coordinator
      - 주키퍼
    - redis cluster
  - 모니터링
    - redis info를 통한 정보
      - RSS : Physical memory 쓰고있는것
      - Used memory : redis가 사용하고있는 메모리 (RSS와 차이 많이나면 위험!)
      - Connection 수
        - 치솟다가 떨어지는게 반복되면 위험
      - 초당 처리 요청 수
    - System
      - CPU
      - Disk
      - Network rx/tx
    - cpu가 100% 칠 경우
      - O(N) 계열의 명령이 많은경우
        - Monitor 명령을 통해 패턴을 파악해야함
        - 그러나 중요한것은 monitor를 잘못쓰면 서버에 더 큰 문제가 일어나니, 빠르게 보고 꺼야함..
- [출처](https://www.youtube.com/watch?v=mPB2CZiAkKM&t=1882s)
- [redis 내부구조](https://m.blog.naver.com/hanajava/220895464821)


- 동시성 제어(여러대의 서버있을때..)를 위한 분산락
  - redisson 활용 
    - https://velog.io/@hgs-study/redisson-distributed-lock


- [Spring data redis, lettuce 사용법 관련 정리굿](https://assu10.github.io/dev/2023/09/24/springboot-redis-1/)