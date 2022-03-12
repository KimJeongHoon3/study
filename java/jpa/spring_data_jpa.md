spring data jpa

- `JpaRepositry<T,ID>` 인터페이스를 상속하는 인터페이스를 만들면 준비끝 
  - `T` : 엔티티
  - `ID` : 기본키 타입
  - 스프링부트가 기동시 JpaRepository인터페이스를 구현하고있는 놈들을 알아서 찾고 인스턴스를 생성함.. (별도의 어노테이션필요없음.. 최상의 JpaRepository의 최상위 인터페이스를 찾아가보면 Repository라는 인터페이스가있는데, 요게 마커역할을해서 스프링부트가 알아서 찾아준다)
    - 구현체는 `SimpleJpaReposity`
  - 공통 인터페이스 구성
    - ![](spring_data_jpa_interface_hierachy.png)
  - 주요 메서드
    - `save(S)` : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
    - `delete(T)` : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출
    - `findById(ID)` : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출
    - `getOne(ID)` : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출 
      - `EntityManager.getReference()` 요거는 실제 해당 엔티티를 사용하기전까지는 가짜객체(프록시)로 가지고있는것..(자세한것은 `jpa정리.md` 참고)
    - `findAll(...)` : 모든 엔티티를 조회한다. 정렬( Sort )이나 페이징( Pageable ) 조건을 파라미터로 제공할 수 있다.
    - > 참고: JpaRepository 는 대부분의 공통 메서드를 제공한다.
      - 그렇다면 공통외의 기능이 필요하다면..?
        - 이를 spring-data-jpa는 해결했다.. (쿼리메소드기능)
- 쿼리 메소드 기능
  - 메소드이름으로 쿼리 생성
    - intelliJ 플러그인에서 "JPA Buddy"를 설치하면 "JPA Palette"를 사용할수 있는데, 이를 통해서 쉽게 생성 가능
    - 만드는 방법이나 종류는 [reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods) 참고
      - by뒤에 아무것도 안넣으면 조건없이 전체조회
    - 조건이 두개정도까지는 이걸쓰는게 괜춘.. 넘어가거나 쿼리가 복잡해지면 너무 길어지고 알아보기힘드니 직접만들던지..할것
    - 이 기능에 또한 큰 장점은 엔티티의 컬럼명을 변경했을때, 해당 컬럼명을 가진 메서드 이름을 변경하지않았다면 애플리케이션 로드시에 에러를 잡아준다..
  - JPA NamedQuery
    ```java
      @Query(name="Member.findByUsername") //해당 name은 @NamedQuery로 Member 엔티티위에 선언되어있어야..
      List<Member> findByUsername(@Param("username") String username); //@Param은 NamedQuery에서 지정한 Param명과 동일해야함

      //여기서 @Query부분을 생략해도되는데, 이는 spring datra jpa가 실행시 아래와 같은 순서로 찾아감
      1. "[엔티티명].[메소드명]" 으로 @NamedQuery를 찾음
      2.  메서드이름으로 쿼리 생성
      3.  ...

      근데 이는 실무에서 잘 사용안한다.. 그냥 @Query 어노테이션을 사용해서 메소드명위에 바로 선언할수있는 기능이 있기때문! 굳이.. 엔티티에 @NamedQuery 안해도되니..

    ```

  - `@Query`, 리포지토리 메소드에 쿼리 정의하기
    ```java
      @Query("select m from Member m where m.username=:username and m.age=:age")
      Member findMember(@Param("username") String username, @Param("age") int age);
    ```
    - 해당 기능을 수행하게되면 메소드이름으로 쿼리생성하는것처럼 메소드 이름이 길어지지않는 장점이 있다
    - 뿐만아니라, 만약 쿼리내부의 컬럼명이 오류가났을때, 어플리케이션 로드시에 위의 JPQL문을 파싱해서 정상적으로 작동하는지 확인하기때문에 컬럼명 오류를 잡아줄수있다! 요게 매우큰장점!!!
    - 동적쿼리는.. ***QueryDSL***로...
  - `@Query`, 값, DTO 조회하기
    ```java
      @Query("select m.username from Member m") //이렇게 string 여러개 가져오게되면 List<String> 쓰면끝
      List<String> findUsernameList(); 

      @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t") //MemberDto의 패키지명을 다 적어줘야함.. => QueryDSL이 해결책..
      List<MemberDto> findMemberDto();
    ```
  - 파라미터 바인딩
    - 위치기반
      - 사용하지말자.. 위치바뀌면 모두 바꿔줘야하니깐..
    - 이름기반
      - 위의 `@Param` 이 이름기반 바인딩..
    - `Collection` 타입으로 in절 지원
      ```java
        @Query("select m from Member m where m.username in :names")
        List<Member> findByNames(@Param("names") Collection<String> names);
      ```
  - 반환타입
    - 컬렉션
      - 만약 데이터없으면 null이 아니라 빈 컬렉션을 리턴함
    - 단건(엔티티타입)
      - jpa는 기본적으로 데이터 없으면 NoResultException 예외를 던지는데, spring data jpa는 이를 잡아서 그냥 null 반환해준다
      - 두건 이상이 조회되면 에러터짐..
        - 여기서 중요한것은 그냥 jpa 에러를 반환해주는게 아니라, 내부적으로 spring 예외로 변환해서 던져준다! 그래야 기술에 종속되지않을수 있으니!
    - Optional
    - [Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types)
  - 페이징과 정렬
    - 순수 JPA
    - spring data
      - 페이징처리를 표준화해놓음 (spring data jpa가 아니라 data 패키지에서 공통화해놨다..!! data기술이면 모두 사용가능..ㄷㄷ)
        - 페이징과 정렬 파라미터
          - `org.springframework.data.domain.Sort` : 정렬 기능 
          - `org.springframework.data.domain.Pageable` : 페이징 기능 (내부에 Sort 포함)
        - 특별한 반환타입
          - `org.springframework.data.domain.Page` : 추가 count 쿼리 결과를 포함하는 페이징 
          - `org.springframework.data.domain.Slice` : 추가 count 쿼리 없이 다음 페이지만 확인 가능 (내부적으로 limit + 1조회)
            - 이는 모바일에서 내려보다보면 더보기로 데이터 가져오게되는거.. 이는 TotalCount가 필요없음
            - +1을 더 조회하는것은 이를 통해서 더보기를 할지말지 정하는것.. 어찌보면 눈속임..? 즉, 10개씩 보여준다면 11개 가져와서 마지막 1개는 더보기 버튼기능이고, 더보기 클릭시 11~21 가져옴.. 이런식으로 반복... +1이 안되면 더보기 버튼이 안나오겠지
          - `List` (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
        ```java
          Page<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 
          Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
          List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함.. 그냥 딱 결과만 가져오는것
          List<Member> findByUsername(String name, Sort sort);

          //사용방법
          PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.[ASC|DESC]), ...properties);
          //PageRequest는 Pageable의 구현체
          //pageNumber는 0부터 시작함! 중요!!!@!@!@!@
          //size는 한 페이지에 몇개씩 보여줄것인지..
          //Sort.by이후 파라미터는 옵션
          

          //Page를 통해서 사용할수있는 페이징처리
          Page<Member> page = memberRepository.findByAge(10, pageRequest);
          List<Member> content = page.getContent(); //조회된 데이터 
          assertThat(content.size()).isEqualTo(3); //조회된 데이터 수 
          assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수 
          assertThat(page.getNumber()).isEqualTo(0); //페이지 번호 
          assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호 
          assertThat(page.isFirst()).isTrue(); //첫번째 항목인가? 
          assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?

          //참고로 Page<Member>는 바로 api로 반환하면 당연안됨! 엔티티를 바로 반환하면안된다!! 왜냐하면 api수정하면 엔티티가 변경되어야하는 말도안되는 상황이 펼쳐지기때문..
          //이를 위해서 Page는 map함수를 제공..
          Page<MemberDTO> memberDTO = page.map(MemberDTO::new); //요걸 api에 제공하는거 좋음

        ```
        - 페이징을 위해서 실제데이터가져오는 쿼리말고, 전체 갯수를 가져오는 쿼리를 분리할수있음
          - 분리가 필요한이유는, left outer 쿼리같은경우 기준이 되는 테이블의 전체 갯수만 알면되는데, 별도로 분리하지않을 경우 조인을 다 수행한뒤에 카운트를 하기때문에 성능에 좋지않게됨..
          - ex 
            ```java
              @Query(value ="가져올쿼리", countQuery="카운트 가져올때 사용할 쿼리") //이걸 사용한다는것은 결국 내가 쿼리를 짜야한다는 이야기긴함..
              Page<Member> findByUsername(String name, Pageable pageable);
            ```
  - 벌크성 수정쿼리
    ```java
      @Modifying //요게 있어야 벌크성 쿼리를 날리는 executeUpdate() 를 호출하고, 이게 없으면 getSingleResult나 getResultList를 호출해서 InvalidDataAccessApiUsageExcepetion 던져짐
      @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
      int bulkAgePlus(@Param("age") int age);
    ```
    - 조심할점!
      - 보통 Jpa는 엔티티중심이기때문에 엔티티의 변경을 기반으로 쿼리들이 수행된다. 그래서 jpa관련 메서드를 호출하는 시점과 실제 db에 반영하는 시점은 다르게되는데, 벌크성 수정쿼리를 수행하면, 기존의 변경점들은 모두 Flush면서 수정쿼리를 db로 날린다.(이는 명확하게는 jpql이 실행되었기때문에 flush되는것! jpql을 수행할때는 flush!) 이때, 영속성 컨텍스트는 벌크성 수정쿼리의 내용을 알수가없기때문에 영속성 컨텍스트와 실제 DB의 데이터와 다른 경우가 발생하게된다..
      - 위의 예제같은경우 bulkAgePlus를 실행하고, 바로 repository에서 변경된 Member를 조회하면 update된 값이 나오지않게된다!
      - 이를 해결하기 위해서는 EntityManager를 clear 해주어서 엔티티매니저가 1차캐시(영속성컨텍스트)가 아닌 db로 가져올수있도록 해주어야한다
        - 물론, 이게 api호출에 마지막동작이라면 굳이 clear까지 할 필요가없지만, 한 트랜잭션내에서 다른작업이 남아있다면 꼭 clear를 해주자!
        - `@Modifying(clearAutomatically=true)`로 해주면 EntityManager 로 clear해줄필요없음!
        - <span style="color:red">이월쪽관련해서 delete -> insert(save) 했을때 clear안하면 문제되는지 테스트해보자</span>

  - `@EntityGraph`
    - fetch join을 하기위함
      - fetch join은 기본적으로 연관관계가 있는 엔티티의 데이터를 지연되어서 가져오지않고 한번에 땡겨오기위함..
      - 즉, Member와 Team의 다대일 관계(+ FetchType.LAZY)에서 Member를 패치조인쓰지않고 그냥 가져오게되면 Team객체는 가짜객체(프록시객체 - Team의 값을 접근할떄 비로소 Team 데이터 조회)로 있는데, 이때 N+1문제가 발생할수있기때문에 한번에 땡겨올 필요가있을때 사용!(당연 바로 땡겨올때는 프록시 사용x) 
    - `@Query("패치조인쿼리")`로 쓰기 귀찮으니 `@EntityGraph(attributePaths = {"team"})`으로 해결가능
      ```java
        @Override
        @EntityGraph(attributePaths = {"team"}) //기본제공쿼리에 @EntityGraph 추가한것
        List<Member> findAll();

        @EntityGraph(attributePaths = {"team"}) 
        @Query("select m from Member m") //직접 jpql 작성한것도 가능
        List<Member> findMemberEntityGraph();

        @EntityGraph(attributePaths = {"team"}) //메소드이름으로 쿼리 생성한것도 가능.. 신기하네..
        List<Member> findEntityGraphByUsername(@Param("username") String username); //@Param없어도됨! find...ByUserName에서 ...은 아무거나 들어가도됨

        //참고로 기보적으로 패치조인시에 left outer join으로 수행됨
      ```  
    - 간단한건데, jpql짜기 번거로운느낌이면 `@EntityGraph` 사용하면되고, 많아진다싶으면 jpql 직접짜서 사용해라(QueryDSL)
  - JPA Hint & Lock
    - JPA쿼리힌트 (***SQL힌트가 아니라***, JPA 구현체에게 제공하는 힌트)
      - 보통 영속성컨텍스트는 변경감지를 위해서 내부적으로 해당 엔티티의 스냅샵값을 저장하고있게된다.. 그렇게되면 아무래도 변경감지를 아예 사용하지않는것보단 비용이 많이들게될텐데, 이러한 비용을 줄일수있도록 hibernate에서는 readonly라는 기능을 제공.. (근데 이런 최적화는 생각보다 영향이 미미할수있으니.. 테스트해보고 적용해볼것)
    - Lock
      - select .. for update 와 같은 LOCK 쿼리 사용가능
  - 사용자정의 리포지토리 구현
    - 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성 
    - 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음 
    - 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
      - JPA 직접 사용( EntityManager ) 
      - 스프링 JDBC Template 사용 
      - MyBatis 사용
      - 데이터베이스 커넥션 직접 사용 등등... 
      - Querydsl 사용
    - 사용방법
      ```java
        public interface MemberRepository
            extends JpaRepository<Member, Long>, MemberRepositoryCustom { //spring data jpa가 이런식으로 쓸수 있도록 해준다!
              ...
        }

        public interface MemberRepositoryCustom {
          List<Member> findMemberCustom();
        }

        @RequiredArgsConstructor
        public class MemberRepositoryImpl implements MemberRepositoryCustom { //구현체 이름을 [이름]+"Impl"을 맞춰야한다! (너무 싫으면 설정 바꾸면됨)
            private final EntityManager em;
            @Override
            public List<Member> findMemberCustom() {
                return em.createQuery("select m from Member m")
                        .getResultList();
            } 
        }
      ```
    - 기본적으로 spring data jpa가 제공해주는 인터페이스기능을 잘 활용하되, 불가하면 이를 활용할것! 여기서 또한  중요한것은 비지니스로직에 맞춘 repository와 특정 화면에만 맞춰진 쿼리들은 클래스를 쪼개는게 좋다! 그리고 아키텍처에 맞추어서 적절하게 사용하자! 즉, 커스텀만들어서 꼭 하나의 repository에 때려넣지말자는것!

  - Auditing
    - 엔티티를 생성, 변경할때 변경한 사람과 시간을 추적
      - 등록일
      - 수정일
      - 등록자
      - 수정자
    - 순수 JPA로 위 문제를 해결할때
      ```java
        @MappedSuperclass //상속관계시 속성만 내려서 사용할수있도록해줌
        @Getter
        public class JpaBaseEntity {
            @Column(updatable = false) //update시 작동안함.. update 실수를 방지
            private LocalDateTime createdDate;
            private LocalDateTime updatedDate;

            @PrePersist // persist 수행전에 실행되는 이벤트 (ex. save메서드 호출하면 수행됨! flush아님)
            public void prePersist() {
                LocalDateTime now = LocalDateTime.now();
                createdDate = now;
                updatedDate = now;
            }

            @PreUpdate // update 수행전에 실행되는 이벤트 (ex. update수행될때니깐 flush를 통해서 변경이 일어나서 Update를 호출하면 수행됨)
            public void preUpdate() {
                updatedDate = LocalDateTime.now();
            }
        }   
      ```
    - 스프링 데이터 JPA 사용
      - 설정에 `@EnableJpaAuditing` 추가 필요!
      ```java
        @EntityListeners(AuditingEntityListener.class) //이벤트 Listener 라는것을 명시해줘야함
        @MappedSuperclass
        @Getter
        public class BaseEntity {
            @CreatedDate
            @Column(updatable = false)
            private LocalDateTime createdDate;

            @LastModifiedDate
            private LocalDateTime lastModifiedDate;

            @CreatedBy // create 수행한자의 정보 => 별도의 auditAware를 등록해주어야함(provider)
            @Column(updatable = false)
            private String createdBy;

            @LastModifiedBy // update 수행한자의 정보 => 별도의 auditAware를 등록해주어야함(provider)
            private String lastModifiedBy;
        }

        @Configuration
        public class Config{
          @Bean
          public AuditorAware<String> auditorProvider() {

              //여기에 SecurityContextHolder와 같은놈들을 통해서 세션정보를 가져와서 ID를 추출해낸값을 넣으면됨..
              return () -> Optional.of(UUID.randomUUID().toString()); //여기 값이 ~~By 어노테이션에 선언된놈으로 들어가게됨
          }
        }

      ```
  - Web 확장 - 페이징과 정렬 (이거 잘 활용하면 진짜 좋은듯)
    - client로부터 Pageable 인터페이스를 파라미터로 받을수있다..
    ```java
      @GetMapping("/members")
      public Page<Member> list(Pageable pageable) { //요걸 알아서 맵핑해줌.. PageRequest라는 객체로 채워서 여기에 injection을 해준다.. 대단한듯..
          Page<Member> page = memberRepository.findAll(pageable);
          return page;
      }

      /**
        사용자요청 방법

        /members?page=0&size=3&sort=id,desc&sort=username,desc
          - page: 현재 페이지, 0부터 시작한다.
          - size: 한 페이지에 노출할 데이터 건수
          - sort: 정렬 조건을 정의한다. 예) 정렬 속성,정렬 속성...(ASC | DESC), 정렬 방향을 변경하고 싶으면 sort
          - 파라미터 추가 ( asc 생략 가능)
      */
      
    ```
    - 글로벌설정으로 기본 size값과 같은 내용을 변경가능! (application.yml)
    - `@PageableDefault`를 특정 요청 파라미터에 넣어서도 지정가능 (글로벌보다 이게 더 우선순위높음)
    - 페이징정보가 둘 이상일때 접두사로 구분가능
      - `@Qualifier`에 접두사명 추가 "{접두사명}_page"
    - 엔티티를 바로 돌려주면안된다! 지금 위의예시의 Member는 엔티티니깐 저건 잘못된것! 꼭 Page.map메서드를 통해서 DTO로 변경하자!
    - page 인덱스가 0번부터 시작인것을 잊지마라!
      - 이를 1로 변경하기위해서는 리턴하는 Page를 직접 커스텀하게 변경해야함..
      - 설정으로 시작인덱스를 1로 변경도 가능한데, client의 요청파라미터만 봐서는 잘 동작하는것 같지만, 실제 응답값(Page)을 보면 인덱스가 0번부터 시작하는것과 동일하게반환되는 부분이있음..
  - 스프링 데이터 JPA 구현체 분석
    - `SimpleJpaRepository<T, ID>` 가 구현체
      - `@Repository`가 선언되어있음
        - `@Repository`의 의미?
          - Spring bean으로 등록된다
          - 예외가 터질때, 스프링 내부에서 사용하는 예외로 다 변환시켜준다
            - 이를통해서 세부 기술이 바껴도 service layer에서는 변경이 없다!(세부 기술로 예외처리를 하지않고, 스프링에서 변환해서 날라오는 예외로 처리했을것을 당연 전제로함..)
      - `@Transactional(readonly = true)`가 선언되어있음
        - 일단 spring data jpa의 모든 기능은 트랜잭션 걸고시작..!
        - save와 같은 기능은 readonly가 되면안되니깐 별도로 `@Transactional`을 메서드 위에 선언해놓는다
          - 그렇기때문에 별도의 트랜잭션을 서비스단에서 걸지않아도 동작이 되기는 하지만, 영속성 컨텍스트는 트랜잭션 단위이므로, 지금 상황에서 save를 호출하고나면 save한 엔티티가 영속성 컨텍스트에 남아있지않다!(save 메서드 호출 끝나면 트랜잭션이 종료되니깐)
      - `save()`
        - 새로운 엔티티면 저장( persist ) 
        - 새로운 엔티티가 아니면 병합( merge )
          - `merge`를 사용하게되면 우선적으로 데이터가있는지 확인해서 없으면 insert, 있으면 update를 진행
            - 즉, 어쨋든 merge는 무조건 데이터확인을 위해 Select하는 쿼리가 한번 나가게되어있다
          - 준영속(영속이었다가 분리된..)이던 비영속(아예 영속된적이 없는..)이던 merge는 식별자 값으로 영속성 컨텍스트에 있는지 확인해보고 없으면 DB를 조회한다. 그리고 DB에서 가져온값을 기준으로 있으면 병합해서(update)하게되는것이고, 없으면 새로이 추가(insert)된다!
          - update하기 위해서 merge를 사용하는것이 아니라, 영속상태인놈이있었는데, 영속상태를 벗어난놈을 다시 영속상태로 만들기위해서 사용하게된다!
            - 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다.
            - 병합시 값이 없으면 null로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.)
              - => 요게 핵심인듯! 영속상태인놈이 준영속이되었다면 당연 갖고있는 데이터가있으므로 생각치못한곳에서 null로 업데이트 하지않을것이다.. 혹여 준영속상태에 있는놈이 merge시에 변경이일어난부분이 있다면 그부분만 변경이될것..!
            - 병합은 편리하게 사용할 수 있다는 장점이 있지만, 모든 필드를 교체한다는 위험성 때문에 사용을 지양해야 한다.
          - ***update해야한다면 merge가 아니라 꼭 변경감지를 생각해라! ***
        - 새로운 엔티티를 구별하는 방법
          - 새로운 엔티티를 판단하는 기본 전략
            - 식별자가 객체일 때 null 로 판단 
            - 식별자가 자바 기본 타입일 때 0 으로 판단
            - Persistable 인터페이스를 구현해서 판단 로직 변경 가능
              - JPA 식별자 생성 전략이 `@GenerateValue` 면 `save()` 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다. 그런데 JPA 식별자 생성 전략이 `@Id` 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 `save()` 를 호출한다. 따라서 이 경우 `merge()` 가 호출된다. `merge()` 는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다. 따라서 Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
              - Persistable을 엔티티에 구현하면되는데, 해당 엔티티가 새로운엔티티인지를 구별하는 `boolean isNew()`를 오버라이딩 할때 엔티티생성시 보통 값을 넣어주지않는 createdDate와 같은 필드를 활용해서 해당 값이 null이면 새로운 엔티티이므로 true를 반환하는것으로 활용하면좋다함(from 김영한)
        ```java

          @Repository
          @Transactional(readOnly = true)
          public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {
                @Transactional
                @Override
                public <S extends T> S save(S entity) {

                  Assert.notNull(entity, "Entity must not be null.");

                  if (entityInformation.isNew(entity)) { //새로운 엔티티인지를 확인
                    em.persist(entity);
                    return entity;
                  } else { //기존에 있는 객체.. Persistable 인터페이스를 재정의하지않고 식별자에 값을 직접할당하면 새로운 엔티티임에도 여기가 호출됨..
                    return em.merge(entity);
                  }
                }
          }
          
        ```

    
- 기타팁
  - gradle 의존관계보기
    - `./gradlew dependencies --configuration compileClasspath`
  - AssertJ 는 chaining 방식으로 테스트를 손쉽게, 직관적으로 할수있다~
  - 로깅 SLF4J & LogBack
    - SLF4J : 인터페이스
    - LogBack : 구현체중 하나
  - `@Transactional(readOnly = true)`
    - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 jpa가 플러시를 생략해서 약간의 성능 향상을 얻을 수 있음
      - readOnly니깐 데이터 변경이 없다고 생각하니 굳이 flush를 하지않음으로 성능최적화!