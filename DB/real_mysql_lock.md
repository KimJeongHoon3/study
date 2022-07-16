# Lock 과 트랜잭션

- 트랜잭션 : 작업의 완전성을 보장.(데이터의 완전성을 보장)
  - 하나의 논리적흐름(여러 쿼리일수도, 아닐수도)이 다 되든지(commit), 아예 안되든지(rollback).
  - 주의사항
    - 트랜잭션은 범위를 최소화 해야한다
    - 어플리케이션 단에서 네트워크 작업과 같은 부하가 있는것은 트랜잭션 작업에서 반드시 배제할것!
    - 불가피한 일이 아니라면 트랜잭션 실행시 프로그램 내부처리하는 코드는 줄이자!
    - 만약 그렇지않으면, 커넥션 풀에 반납을 하지않아 커넥션을 얻기 위해 한없이 기다려야할수도 있다..

- 잠금 : 동시성을 제어하기 위한 기능.
  - 동시에 여러 커넥션이 있으면, 하나의 커넥션만 변경할 수 있게 해주는 역할
  
- 격리수준(Isolation level) : 하나의 트랜잭션 내에서 또는 여러 트랜잭션 간의 작업 내용을 어떻게 공유하고 차단할 것인지를 결정하는 레벨. 즉, 특정 트랜잭션이 다른 트랜젹션에서 변경하거나 조회하는 데이터를 볼 수 있도록 허용할지 말지!
  - SERIALIZABLE 말고는 성능상에 크게 차이는 없다!
  - 종류(격리수준이 낮은 순서임.. 그렇기에 하위에서 발생하는것이 상위에서는 발생하지않지만, 상위에서 발생하는것이 하위에서는 당연히 발생! ex. READ-UNCOMMITTED는 READ-COMMITTED, REPEATABLE-READ에서 발생하는 문제가 동일하게 발생됨)
    - READ-UNCOMMITTED : 
      - COMMIT 되기전의 데이터를 볼수있음
      - DIRTY READ 발생
        - DIRTY READ : 한 트랜잭션에서 COMMIT도 안된 데이터를 다른 트랜잭션이 볼수있음 => 만약 롤백된 데이터라면 사라진 데이터를 가지고 작업하는 꼴이됨
    - READ-COMMITED : 
      - COMMIT된 데이터만 볼수있음
      - 한 트랜잭션에서 update를 하고 commit을 하지않았을때 테이블에는 변경이 기록되지만 UNDO로그(변경 전의 데이터가 있음)에서 데이터를 읽어오기때문에 COMMIT하지않으면 이전 데이터를 계속 가져오는것!
      - NON REPEATABLE READ 발생
        - NON REPEATABLE READ : 한 트랜잭션이 시작되어서 끝날때까지 동일한 쿼리문을 날리면 동일한 데이터가 조회되어야하는데, 그 사이에 다른 트랜잭션에서 대상 테이블을 변경하고 commit하면 변경된 데이터가 보임
    - REPEATABLE-READ : 
      - 한 트랜잭션이 시작되어서 끝날때까지 동일한 쿼리문을 날리면 동일한 데이터 조회가능
        - 이는 트랜잭션이 실행되면 트랜잭션 ID가 증가되어 생성되는데, 테이블의 데이터들을 조회할때 해당 데이터를 건드린 혹은 생성한 트랜잭션ID보다 크면 언두로그에서 실행 트랜잭션ID 보다 작은 데이터를 찾기 때문에 가능하다.
        - 그리고 이렇기 때문에 BEGIN으로 트랜잭션 시작하고 장시간동안 트랜잭션을 종료하지 않으면 언두 영역이 백업되 ㄴ데이터로 무한정 커질수도 있음..
      - 기본적으로 InnoDB는 팬텀이 발생하지않으나, select .. for update와 같은 쿼리는 발생할수있음(언두로그에 x락을 잡을수 없어서 현재 테이블로 가져옴)
      - 팬텀발생
        - 팬텀 : 트랜잭션 실행후 처음 조회했을때 없었던 레코드가 다시 조회하니 생김(보통 해당 격리수준에서는 데이터 조회시 공유락을 건다함.. 그래서 다른 트랜잭션에서 배타락 거는 작업은 진행하지못하지만, 데이터를 추가하는것은 가능..) 
    - SERIALIZABLE : 
      - 가장 엄격한 격리수준
      - 동시성 중요하면 사용하지않음.
      - select 작업을하게되면 공유락을 잡음
  

- MySQL 엔진 잠금
  - MySQL 엔진레벨의 잠금은 모든 스토리지에 영향을 미친다(스토리지 엔진 잠금은 스토리지엔진간 상호 영향을 미치지 않는다)
  - 종류
    - 글로벌락
      - FLUSH TABLES WITH READ LOCK 명령으로 사용가능
      - 다른 세션에서 SELECT 제외하고는 DDL과 DML 접근 불가
      - MySQL 서버 전체에 영향을 가기때문에, 웹 서비스용으로 사용되는 MySQL에서는 사용하지안흔게좋음
    - 테이블락
      - 테이블 단위의 잠금.
      - MyISAM 스토리지는 데이터 변경시 테이블락이 잡힘(InnoDB는 DDL에서만 적용, DML은 레코드락)
      - LOCK TABLES table_name [ READ | WRITE ] (락)
      - UNLOCK TABLES (해제)
    - 유저락
      - 사용자가 지정한 문자열에 대해 획득하고 반납하는 잠금
      - 많은 레코드를 한번에 변경하는 트랜잭션의 경우에 유용
      - 데드락 해결에 유용하게 사용될수 있음
      - > ` SELECT GET_LOCK('mylock',2); // 이미 잠금이 사용중이면 2초 동안만 대기 (락 획득) `
      - > ` SELECT RELEASE_LOCK('mylock'); (락 해제) `
      - >> 정상적으로 명령 수행(락 잡거나 해제)되면 1 아니면 0
    - 네임락
      - 테이블 이름 변경하는 경우 자동으로 획득하는 잠금
      - 기존에 운영하고 있는 테이블을 새로운 테이블로 변경하는데 있어서 유용
      - > ` RENAME TABLE rank TO rank_backup, rank_new TO rank // rank라는 테이블을 rank)backup이라는 테이블로 변경하고 rank_new 라는 테이블을 rank라는 테이블로 변경한다.. 이렇게 한번에 해줘야 순간적으로 테이블이름 찾을수없다는 오류를 안볼수있다!!`



- MyISAM과 MEMORY 스토리지 엔진의 잠금
  - 하나의 쿼리 실행에 테이블락 걸리고 완료시 바로 해제 
  - 테이블 단위의 잠금이라 동시성이 떨어지고 트랜잭션 지원안됨(즉, 본인이 알아서 트랜잭션 처리를 해줘야함)
  - 데드락이 발생안함!

- InnoDB 스토리지 엔진 잠금
  - 레코드 기반 잠금으로 동시성처리 굿
  - > ` SHOW ENGINE INNODB STATUS; // 락 확인가능 `
  - 아래는 데드락 확인 쿼리문(mysql 5.1 이상)
---
```java
    SELECT
        r.trx_id waiting_trx_id, -- 잠금하기위해 기다리고있는 놈
        r.trx_mysql_thread_id waiting_thread,
        r.trx_query waiting_query,
        b.trx_id blocking_trx_id, -- 잠금해서 막고있는 놈
        b.trx_mysql_thread_id blocking_thread,
        b.trx_query blocking_query
    FROM information_schema.innodb_lock_waits w
    JOIN information_schema.innodb_trx b ON b.trx_id=w.blocking_trx_id
    JOIN information_schema.innodb_trx r ON r.trx_id = w.requesting_trx_id;
```


innodb_lock_waits : 잠금에 의한 프로세스간의 의존관계 확인 가능   
innodb_trx : 어떤 트랜잭션이 어떤 클라이언트로부터 기동중인지 확인가능한 테이블   
innodb_locks : 어떤 잠금이 있는지 있는 테이블

---


  - InnoDB의 잠금방식
    - 비관적 잠금 : InnoDB가 채택하고있는방식. 내가 변경하고 있는 데이터를 다른 누군가가 변경시킬수도 있으니, 락이 미리 필요하다!(먼저잠금)
    - *낙관적 잠금 : 다 괜찮고, 문제 있으면 그때 가서 처리하자! 마지막에 잠금 충돌있었는지 확인하여 rollback

  - 잠금의 종류   
    - 잠금 정보가 상당히 작은 공간으로 관리되기때문에 레코드락이 페이지 락으로 또는 테이블 락으로 레벨업되는 경우는 없다!
    - 레코드락 : 특정 레코드에 대해서 잠금을 수행. Innodb 스토리지엔진은 ***레코드 자체가 아니라***, ***인덱스의 레코드를 잠근다!*** 인덱스를 사용하지않는 테이블이더라도 자동 생성된 클러스터 인덱스를 이용해 잠금을 설정! 
      - *참고로 보조 인덱스는 넥스트키락을 사용하나 프라이머리키나 유니크 인덱스에 의한 변경은 갭락을 사용안하고 해당 레코드만 잠근다
    - GAP락 : 레코드의 바로 인접한 레코드 사이의 간격을 잠금하는것. 갭 락의 역할은 새로운 레코드 insert를 막는것
    - 넥스트 키 락 : 레코드락 + 갭락. 데드락 발생의 주요원인..(한 트랜잭션은 update를 위해 보조인덱스 잠그고 다른 트랜잭션은 update를 위해 PK 잠금인데 겹칠때..)
      - REPEATABLE READ 에서 사용
      - 격리수준을 READ-COMMITTED 로 변경하면 데드락 문제는 해결될수 있음!
      - 복제를 위한 바이너리 로그때문에 사용됨!!
      - 작은(<) 혹은 작거나 같은(<=) 조건을 범위로 주었을때 인덱스 조건이 테이블에 존재하는 레코드와 레코드사이라면 조건의 다음 레코드의 값까지 넥스트 키 락이 걸린다.
      --- 
        - 예를들어 PK의 값으로 90, 100, 110이 있을때
        - trx1 : select .. where pk<95 for update (1)
        - trx2 : update .. where pk=100 (2)
        - (1) 이후 (2) 진행하면 (1)은 100을 포함하여 작은값에 잠금을 건다..(2)는 잠금을 기다린다. 100보다 작거나 같은 값은 insert도 못함
        - 반대로 
        - trx1 : select ... where pk>100 for update (1)
        - trx2 : update ... where pk=100 (2)
        - (1) 이후 (2) 진행하면 잘된다.. 배타적 넥스트 키 잠금이 range를 비교대상보다 큰것으로 진행하면 100이라는 레코드는 잠그지않는듯함.. 왜그러는지는.. 좀더 공부가 필요할듯하다... 
        ---

    ` 레코드락이나 넥스트키락은 검색을 수행한 인덱스를 잠근후, 테이블의 레코드를 잠근다!! `
    - 자동증가 락 : Auto_increment를 위한 잠금
  

  - 인덱스와 잠금 
    - 검색한 인덱스의 레코드를 모두 잠근다!
    - 만약 인덱스가 안잡혀서 풀스캔 때리면 모든 레코드가 잠길수있음..

- InnoDB의 기본 잠금 방식
  - INSERT, UPDATE, DELETE는 기본적으로 쓰기잠금을 사용하고 필요시 읽기 잠금을 사용할수도있음
  - Autocommit이라도 락을 사용하지않는것은 아니며, 하나의 쿼리문장단위로 트랜잭션이 일어나는것으로 보면된다
    - Autocommit이라도 명시적으로 begin 키워드나 start transaction 명령을 통해서 트랜잭션을 시작할수있는데, 이때는 autocommit이 비활성화된것과 동일하게 commit이나 rollback명령을 통해서 수동으로 트랜잭션을 종료해야한다
  - UPDATE나 DELETE 쿼리문을 사용할때는 언제나 인덱스를 고려해야한다! 인덱스 문장에 따라 잠금의 범위가 정해질수있기 때문이다!!
    - 여기서 주의할점은 **READ-COMMITTED** 격리수준에서는 넥스트 키 락과 같이 사용된 인덱스의 레코드 모두를 잠그는게 아니라, 실제 변경 대상이 되는 레코드만 잠그게되는데, 이는 인덱스에 따라 잠금을 걸었다가 나머지 조건에 일치하지않아서 다시 잠금을 해제하는 방식인것이다!<br><br>
  - SELECT
    - 격리수준에 따라 다른데, REPEATABLE READ 이하로는 잠금을 사용하지않는다
    - SERIALIZABLE은 s-lock 걸림
    - > ` SELECT ... FOR UPDATE `
      - 배타 넥스트 키 락이 사용됨
      - 다른 트랜잭션에서 읽기 쓰기 모두 접근 못함
      - 같은 트랜잭션 내에서 DDL 문장이 사용되면 잠금해제됨
      - 해당 쿼리는 언두로그를 읽는것이 불가하므로(언두로그에는 락을 잡을수 없어서) 일관된 읽기가 어렵다(팬텀일어남)
    - > ` SELECT ... LOCK IN SHARE MODE `
      - 공유 넥스트 키 락이 사용됨
      - 같은 공유 락 끼리는 접근가능. 즉, 다른 트랜잭션에서 select 하면 문제없이 읽어감 (쓰기는 접근 안됨)
      - 같은 트랜잭션 내에서 DDL 문장이 사용되면 잠금해제됨
    - => 위의 작업은 rollback이나 commit이 되어야 락이 해제가 되는데, 그렇기에 어플리케이션 단에서 해당 쿼리를 호출하였다면, TRY~ FINALLY와 같은 구문을 사용하여 처리를 해주어야한다
  - INSERT
    - PK나 UK가 존재한다면 s-lock을 획득하여 중복검사를 함
    - MySQL은 테이블에 데이터를 Insert전에 INSERT INTENTION LOCK을 사용
      - INSERT INTENTION LOCK : 갭 락의 일종으로 INSERT INTENTION LOCK 끼리는 상호 배타적이지않다
        - 만약 기존 테이블에 PK로 등록되어있는값이 1,7,10 일때, 3,4,5 데이터를 동시에 넣는다고 가정한다면 
        - INSERT INTENTION LOCK이 없으면, 3을 INSERT할때 1~7까지 배타적 갭락이 잡히면서 4,5는 대기해야한다
        - 그러나 INSERT INTENTION LOCK으로 잡으면, 3을 INSERT할때 4나 5또한 INSERT INTENTION LOCK을 수행하고 동시에 데이터를 집어넣을수 있다
    - Insert 뒤에 x-lock 을 가짐
    - 순서
      - 중복데이터없을때 : s-lock -> 중복문제x -> INSERT INTENTION LOCK -> 데이터 insert -> x-lock
      - 중복있을때 : s-lock -> 중복발생 -> s-lock 유지
        - 여기서 s-lock이 지속되는것은 테이블의 해당 레코드가 rollback이나 commit되기이전에 값이 변경이나 삭제되어서는 안되기때문!
    - > ` INSERT INTO ... ON DUPLICATE KEY UPDATE `
      - 먼저 중복확인을 위해 s-lock 걸고, 존재한다면 x-lock 걸고 update 수행
      - 레코드 존재하지않는다면 일반 insert와 같이 인서트 인텐션 락 걸고 INSERT 실행한뒤 x-lock 획득
    - > ` INSERT INTO tb_new ... SELECT ... FROM tb_old ..`
      - tb_old 테이블에 공유 넥스트 키 락이 걸림.. 이 쿼리가 실행되는 동안 tb_old 테이블에서 복사하는 원본 레코드가 변경되지 않도록 보장해주기 위해서! 팬텀레코드 막기위함!
      - 이로인한 데드락을 피하기위한것으로는 만약 MySQL이 복제를 하는ㄱ ㅔ아니라면 READ-COMMITTED로 변경하면된다
      - 그러나 그렇지않다면, SELECT한데이터를 파일로빼서 다시 읽는것이 최선이라함..(P721)
  - UPDATE
    - 배타적 넥스트 키 락을 걸게됨(보조 인덱스겟지..?)
    - > ` UPDATE tb_test1 a tb_test2 b ON ... SET a.column=b.column ... `
      - 이렇게 조인 update와 같은경우에 tb_test1 테이블과 같은경우는 컬럼이 변경되므로 배타적 잠금이 걸리고, 참조만 하는 b테이블과 같은경우는 공유 락이 걸린다. 이는 INSERT ~ SELECT의 원리와 같다(팬텀막기위함)
  - DELETE
    - update와 동일함! 삭제한다는것만 다름!
---
- x-lock(배타잠금) : 내가 쓰는동안 어느 누구도 못건들게(당연 그냥 select은 가능.. 락이랑 상관없으니깐.. 공유락 배타락 접근 불가)
- s-lock(공유잠금) : 내가 읽는 동안 데이터 변경 못하도록(읽는것은 가능)
- INTENTION LOCK : 테이블 레벨의 락. 특정 로우에 S-lock 혹은 X-lock 걸기전에 테이블에 걸어주는 락.. (s-lock걸기전에는 Intention shared lock, x-lock 걸기전에는 Intention exclusive lock). insert intention lock 과는 다르다!
---

---
- 데드락 사례

  - 데드락1(상호거래관련)
  
    | 트랜젝션 1 | 트랜젝션 2 |
    |---|---|
    |BEGIN;||
    ||BEGIN;|
    |UPDATE tb_user <br>SET point_balance = point_balance-10<br>WHERE user_id='A' ||
    ||UPDATE tb_user <br>SET point_balance = point_balance-10<br>WHERE user_id='B'|
    |UPDATE tb_user <br>SET point_balance = point_balance+10<br>WHERE user_id='B'||
    ||UPDATE tb_user <br>SET point_balance = point_balance+10<br>WHERE user_id='A'|
    |데드락 발생||
    |commit;||
    ||commit;||
    
    => 이는 어플리케이션 단에서 고쳐주어야할 데드락인데, user_id의 순서대로 데이터를 처리하면해결가능.. 트랜잭션2에서 user_id를 A먼저!

  - 데드락2(유니크 인덱스관련)
    | 트랜젝션 1 | 트랜젝션 2 | 트랜젝션 3 |
    |---|---|---|
    |BEGIN;|BEGIN;|BEGIN;|
    |INSERT INTO tb_test VALUES(9);|||
    ||INSERT INTO tb_test VALUES(9);||
    |||INSERT INTO tb_test VALUES(9);|
    |ROLLBACK;|||
    |데드락발생|||

    => 트랜젝션1번이 rollback함과 동시에 배타락은 사라지고 트랜젝션2번과 3번은 대기하고있다가 공유락으로 해당 지점을 잠금한다. 그리고 배타락을 서로가 획득하려하지만 공유락이 걸려있어서 어느누구도 획득하지못하고 데드락<br>
    => 프로그램상에서 여러 스레드를 통해 동시에 실행되면 이런 현상이 나타날수있음.. 이에 대한 해결책으로는 불필요한 유니크 인덱스를 줄이는것과 프로그래밍 코드상에서 데드락을 핸들링해주는 방법이있다..

  - 데드락3(서로 다른 인덱스를 통한 잠금)
    | 트랜젝션1 | 트랜젝션2 |
    |---|---|
    |SET AUTOCOMMIT=1;|SET AUTOCOMMIT=1;|
    |UPDATE tb_user<br>SET user_status=4<br>WHERE user_status=1<br>ORDER BY user_id LIMIT 1;|UPDATE tb_user<br>SET user_status=2<br>WHERE user_id=2;|
    |데드락|
    ||
    *PK : user_id<br>
    *보조INDEX : user_status

    | 트랜젝션1 | 트랜젝션2 |
    |---|---|
    |ix_status 인덱스를 레인지 스캔해서 user_status 1인 레코드의 배타적 잠금획득(이때 ix_status인덱스에 잠금 설정) 그리고 프라이머리 키를 읽어오려함||
    ||프라이머리 키를 검색해서 user_id=2인 레코드의 배타적 잠금 획득(PK인덱스에 잠금설정) 그리고 user_status 값을 변경하기 위한 작업에 돌입하려함|
    |변경 작업을 수행하기 위해 프라이머리 키 값이 2인 레코드의 배타적 잠금을 획득해야하는데, 이미 2번 트랜잭션이 점유 상태이므로 대기|user_stuatus 값을 1에서 2로 변경하기 위해서 인덱스(ix_status)레코드의 배타적 잠금을 획득해야하는데, 이미 1번 트랜잭션이 점유중이므로 대기(**인덱스 값이 변경되는작업**이므로 인덱스를 배타적잠금해서 변경이 필요함)|

    => 쿼리구문을 하나 날렸을때 한번의 잠금획득으로 모든것이 처리되는것이 아니다! 그래서 이런상황이 발생할거를 모두 예측할수는 없으므로 발생하였을때 이를 해결할수잇는 방어로직들이 추가로 들어가야한다! 프로그램 코드에서 데드락 발생시 재처리하는 로직을 넣는것은 결코 편법이아니다!!!

---

* 참고사항
  * 저장 프로시저의 begin end와 트랜잭션(begin)이랑은 별개의 개념이다..
  * 저장 프로시저안에서 트랜잭션을 사용해야한다면 tranaction start를 사용해라!
  * innodb_lock_wait_timeout 이라는 MySQL의 시스템 설정 변수에 지정된 시간(default 50초) 동안 레코드 잠금을 기다렸는데, 획득하지 못할때 에러를 발생시킨다.. 이는 레코드 레벨의 잠금에서만 사용되며, 테이블 레벨의 잠금을 기다릴때는 적용되지 않는다. 즉, ALTER TABLE 명령으로 테이블의 구조를 변경하는 작업은 아무리 오랫동안실행되도 기다린다..
  * 데드락 관련 로그 내용 
    - transaction : 트랜잭션에 대한 정보를 보여준다.
    - waiting for this lock to be granted : 트랜잭션이 실행하기 위해 lock을 걸어야 하는 데이터에 대한 정보, 즉 row에 대한 정보를 보여준다.
    - holds the lock(s) : 현재 잡고 있는 lock에 대한 정보를 보여준다.
    - [출처] mysql-innodb에서 데드락 정보 확인하기|작성자 하나자바
    - [상세로그분석](https://www.percona.com/blog/2014/10/28/how-to-deal-with-mysql-deadlocks/)

  
BEGIN and BEGIN WORK are supported as aliases of START TRANSACTION for initiating a transaction. START TRANSACTION is standard SQL syntax, is the recommended way to start an ad-hoc transaction, and permits modifiers that BEGIN does not.

The BEGIN statement differs from the use of the BEGIN keyword that starts a BEGIN ... END compound statement. The latter does not begin a transaction. See Section 13.6.1, “BEGIN ... END Compound Statement”.
-출처 : mysql docs (+ https://dba.stackexchange.com/questions/261194/mysql-difference-between-begin-and-start-transaction)


---

- 넥스트 키 락 왜쓰나?
  - [mysql 5.6 한글번역](http://211.245.104.72/?depth=140206)
  - Repeatable read에서 팬텀row를 막기위함 
    - 즉, A 트랜잭션이 select 한 뒤에 다시 select하는 사이에 B 트랜잭션에서 작업하여 커밋까지 완료하였는데, 그게 A트랜잭션이 select하는범위에 영향을 미치게되어 A 트랜잭션에서 항상 동일한 select 데이터를 보장하지못하는것을 막기위함!
    - https://idea-sketch.tistory.com/46
- update 쿼리 수행시 정확하게 어떻게 동작하나
  - 만약 변경해야할 데이터를 보조인덱스를 사용해서 찾았고, 이를 변경한다면, 우선 보조인덱스로 락을 잡고 변경할 레코드를 클러스터드 인덱스로 또한 잠그고 작업한다..
  - If a secondary index is used in a search and the index record locks to be set are exclusive, InnoDB also retrieves the corresponding clustered index records and sets locks on them.
    - https://dev.mysql.com/doc/refman/8.0/en/innodb-locks-set.html



---
읽어보면 좋은자료
- https://www.letmecompile.com/mysql-innodb-lock-deadlock/