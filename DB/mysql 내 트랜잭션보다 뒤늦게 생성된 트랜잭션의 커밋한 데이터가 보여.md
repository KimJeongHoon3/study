mysql 내 트랜잭션보다 뒤늦게 생성된 트랜잭션의 커밋한 데이터가 보여요

- mysql 5.7, repeatable read(격리수준)

- tldr;
  - repeatable read level에서 read operation이 수행되는 시점의 스냅샷(데이터가 중간에 변경되었다면, 일관된 읽기(consistent read)를 위해 언두로그를 활용하기도함)에 접근해서 데이터를 일관되게 읽게해준다. 즉, `조회했을때`이다! transaction이 시작될때가 아님!!!!
    - 그래서 동일한 쿼리날리면 동일한 데이터를 계속 보여준다! (Repeatable read)
    - https://dev.mysql.com/doc/refman/5.7/en/glossary.html#glos_consistent_read
      - `With REPEATABLE READ isolation level, the snapshot is based on the time when the first read operation is performed. `
        - 스냅샷은 read 명령이 수행될때 만들어짐..

```sql
-- test table
create table temp_test_table(
    col1 int,
    col2 varchar(30)
);

-- case 1 => 다른 커넥션이 commit 한 데이터를 볼 수 잇는 경우

-- connection 1
start transaction; -- (1)
insert into temp_test_table values (1, 'abc'); -- (3)
commit; -- (4)

------------------------------

-- connection 2
start transaction; -- (2)
select * from temp_test_table; -- (5) 여기서 (3)에서 수행한 데이터가 보임. select 하는 시점에 데이터가 다른 트랜잭션에서 commit이 된 데이터가 있다면 볼 수 있음.
commit; -- (6)


-- ########################################################################

-- case 2 => 다른 커넥션이 commit 한 데이터를 볼 수 없는 경우

-- connection 1
start transaction; -- (1)
insert into temp_test_table values (1, 'abc'); -- (3)
commit; -- (5)

------------------------------

-- connection 2
start transaction; -- (2)
select * from temp_test_table; -- (4) 여기서 (3)에서 수행한 데이터가 보이지않음. 아직 connection 1에서 commit을 수행하지않았기때문에 볼 수 없다! 그리고 여기서 언두로그를 통해 스냅샷이 만들어졌기때문에 connection 1이 commit 되더라도 (3)에서 insert한 데이터를 볼 수 없다.
commit; -- (6)
select * from temp_test_table; -- (7) connection2의 트랜잭션이 commit되었기에 최신 데이터 볼 수 있다. 즉, 여기서는 (3)에서 insert한 데이터가 보인다~

```


--- 

`@Transactional` 수행시에 트랜잭션을 시작하는것은 맞으나(SET AUTOCOMMIT = 0), 데이터를 최조 조회하는 시점에 트랜잭션 수행 이후에 다른 트랜잭션에서 commit된 데이터가 있다면 이 또한 읽을 수 있다(볼 수 있다)



```java
@Test
void TestMain(EntranceService entraceService) {
    int nThreads = 10;
    ExecutorService es = Executors.newFixedThreadPool(nThreads);
    CyclicBarrier cyclicBarrier = new CyclicBarrier(nThreads + 1);
    Data data = new Data();

    for (int i = 0; i < nThreads; i++) {
        es.submit(() -> {
            try {
                cyclicBarrier.await();
                entraceService.enter(data);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
    cyclicBarrier.await();

    es.shutdown();
    es.awaitTermination(60, TimeUnit.SECONDS);
}



@Service
public class EntranceService {
    @Autowired
    private ImplService implService;

    @Transactional
    public void enter(Data data) { 
        implService.doEntner(data);
    }
}

@Service
public class ImplService {
    @Autowired
    private Locker locker

    public void doEntner(Data data) {
        locker.executeWithLock(data.getTicketNo(),5, () -> { // (1) 
            validateTicket(entranceProcessParam); // (2)
            process(entranceProcessParam); // (3) 
        });
    }
}

/*
(1) 락 수행 => mysql named lock 사용, 현재 진행하고있는 transaction의 datasource와는 다른 datasource 사용 (즉, 기존 transaction에 영향 없음)
(2) A테이블에 데이터 조회하는 쿼리 수행 => 여기서 문제발생!!!
  - validateTicket 메서드에서 A테이블의 갯수를 조회해오고, 그 갯수가 일정 수 넘지 않아야하는 validation이 있음
    - process 메서드에서 A테이블에 data를 Insert함
  - 어떤문제?
    - 테스트코드 수행시 validation시 조회해오는 특정 테이블에서 데이터가 하나씩 증가하는게 보임
      - 테스트코드상 별도 스레드를 사용하니 롤백을 수행하진 않는다
    - 즉, 다른 트랜잭션에서 commit된 데이터들이 보인다. 트랜잭션은 EntranceService.enter 에서부터 start되었기에 시작할때 볼 수 있는 A테이블의 데이터는 최초에 없어야하고, 나머지 모든 트랜잭션에서도 당연히 validateTicket 메서드에서 A테이블에 데이터 조회시 아무것도 없어야하지않을까 생각함 (트랜잭션이 시작하면 Repeatable Read 이므로 볼 수 없을 거라 생각했음)
    - => 하지만, 위 정리된 내용처럼 트랜잭션 시작할때 시점의 스냅샷을 보는게 아니라 최초 read operation이 수행됐을때 (내 트랜잭션시작 이후 다른 트랜잭션에서 커밋된 데이터 포함)를 기준의 스냅샷이기때문에 한건씩 보였던것임.. (락이 잡혀있었기에 한 스레드만 진입하여 commit)
      - 1. tx1, tx2, tx3, tx4, tx5 ... 시작
      - 2. tx1 락 점유 및 작업수행 (A 테이블 0개 select + A 테이블 1개 insert)
      - 3. tx1 락 해제
      - 4. tx2 락 점유 및 작업수행 (A 테이블 0개 select + A 테이블 1개 insert)
      - 5. tx1 commit
      - 6. tx2 락 해제
      - 7. tx3 락 점유 및 작업 수행 (A 테이블 1개 select + A 테이블 1개 insert)
      - 8. tx2 commit
      - 반복...

(3) A테이블에 db insert 쿼리 수행

참고로 위 코드는 잘못된 코드.. 
/*
```


