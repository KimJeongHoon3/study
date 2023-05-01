Transaction Management

- 포괄적인 트랜잭션 지원은 Spring Framework를 사용하는 가장 강력한 이유 중 하나
- 스프링은 트랜잭션 관리에 대한 추상화를 제공했는데, 그로인한 이점은 아래와 같음
  - 일관된 프로그래밍 모델 제공
    - JTA(Java Transactin API), JDBC, Hibernate, JPA(Java Persistence API)
  - 선언전 트랜잭션 관리 제공
  - 사용하기에 심플함
  - Spring Data Access 추상화와 훌륭한 통합!
- JTA를 사용하기위해서는 JTA 성능을 감당할만한 서버를 준비해야하는듯함.. 그래서 예전에 트랜잭션을 사용하기위해서는 굳이 많은 성능이 필요없음에도 그런 전용서버가 필요했던거같음.. 그러나, spring에서 제공해주는 트랜잭션은 설정하나로 JTA와 같은 글로벌 트랜잭션과 그냥 심플한 로컬트랜잭션으로 설정하나만 수정하면 손쉽게 변경이 가능하기때문에 처음부터가 아닌, 완전히 로드된 애플리케이션 서버로 확장할 시기를 선택할 수 있게 해줍니다. 
  - > The Spring Framework gives you the choice of when to scale your application to a fully loaded application server. Gone are the days when the only alternative to using EJB CMT or JTA was to write code with local transactions (such as those on JDBC connections) and face a hefty rework if you need that code to run within global, container-managed transactions. With the Spring Framework, only some of the bean definitions in your configuration file need to change (rather than your code).


- Understanding the Spring Framework Transaction Abstraction
  - 트랜잭션 전략은 `TransactionManager` 에 의해 정의된다 (spring에서 이를 ***필수로 등록해줘야함***)
    - `DataSourceTransactionManager`, `JtaTransactionManager`, `HibernateTransactionManager` 등이 구현체
  - `TransactionDefinition` 인터페이스는 아래와 같은 내용을 지정한다
    - Propagation
      - 트랜잭션에 대한 전파.. 예를들어, 기존에 트랜잭션이 있을때 이를 계속 사용할것인가, 아니면 잠시 멈추고 새로운 트랜잭션을 생성할것인가 등
      - ![Propagation 종류](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#tx-propagation)
    - Isolation
      - 격리수준
    - Timeout
      - 트랜잭션이 수행되는 시간을 지정. 만약 해당 시간을 넘어버리면 rollback
    - Read-only status
      - 데이터 변경 못하도록.. 하이버네이트를 사용할때의 경우 최적화가능


- Understanding the Spring Framework’s Declarative Transaction Implementation
  - 트랜잭션 지원은 AOP 프록시를 통해 활성화 되며, 트랜잭션 어드바이스는 메타데이터(xml 또는 어노테이션)로 구동
  - 트랜잭션 메타데이터를 가지고 만든 AOP 프록시는 `TransactionInterceptor` 와 적당한 `TransactionManager` 구현체를 사용해 메소드 호출을 둘러싸고 트랜잭션을 실행한다
    - 명령형 TransactionManager : PlatformTransactionManager
    - 반응형 TransactionManager : ReactiveTransactionManager
      - 요건 주의해야할점들이 있는것 같다.. 나중에 적용시 꼭 확인할것
        - ex. > Publisher는 트랜잭션이 진행되는 사이 데이터를 방출할 수는 있지만 반드시 트랜잭션이 완료됐다고 볼 순 없다. 따라서 트랜잭션이 완전히 끝나야 하는 메소드는, 완료 여부를 확인하고 호출 결과를 어딘가에 버퍼링해야 한다.
  - 참고사항
    - `@Transactional` 은 PlatformTransactionManager가 관리하는 스레드에 바인딩된 트랜잭션으로 동작하기때문에, 현재 스레드 내에서 실행하는 모든 데이터 접근 연산에 트랜잭션을 노출한다. 하지만, ***메소드 안에서 새로 시작한 스레드로는 전파되지않는다..!***
      - 트랜잭션정보를 들고있는 인스턴스는 ThreadLocal로 관리되기떄문에, 현재스레드에서만 기존에 사용하고있던 트랜잭션을 가져올수있다. 그래서 새로운 스레드로는 접근이 불가..
    ```java
        public abstract class TransactionAspectSupport implements BeanFactoryAware, InitializingBean {
            private static final Object DEFAULT_TRANSACTION_MANAGER_KEY = new Object();
            private static final ThreadLocal<TransactionAspectSupport.TransactionInfo> transactionInfoHolder = new NamedThreadLocal("Current aspect-driven transaction"); // 여기
            protected final Log logger = LogFactory.getLog(this.getClass());
            //...

             protected final class TransactionInfo {
                private final PlatformTransactionManager transactionManager;
                private final TransactionAttribute transactionAttribute;
                private final String joinpointIdentification;
                private TransactionStatus transactionStatus;
                private TransactionAspectSupport.TransactionInfo oldTransactionInfo;
                //...
             }
        }
    ```

- Rolling Back a Declarative Transaction
  - 트랜잭션 작업을 롤백해야 함을 스프링 프레임워크 트랜잭션 인프라에 알리는 권장 방법은 트랜잭션 컨텍스트내에서 현재 실행중인 코드로 Exception을 던지는것! (스프링에서는 디폴트가 RuntimeException 또는 그 하위클래스일때 롤백)
  - 아래와 같이 선언적 방식이아닌, 프로그래밍 방식으로 사용할 수 있긴 하지만.. 되도록 사용하지말자
    ```java
        public void resolvePosition() {
            try {
                // some business logic...
            } catch (NoProductInStockException ex) {
                // trigger rollback programmatically
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
    ```

- Using `@Transactional`
  - 클래스 레벨에 사용하는 어노테이션은 선언하는 클래스(하위클래스도)의 모든 메소드에 적용할 기본값을 나타낸다
    - 하위클래스에 적용한게 상위클래스에 적용되진않음
  - `TransactionManager` 는 항상 필수로 등록되어야한다!!!!!!@!@!
    - 당연 `@EnableTransactionManagement` 해당 어노테이션도 `@Configuration` 에 같이 등록되어야함!
  - `public` 메서드만 적용가능.. protected나 private 을 사용하고싶으면 정상동작안하기에 여기에 트랜잭션을 먹이고싶으면 AspectJ를 사용해야함