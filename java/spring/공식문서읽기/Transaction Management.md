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
    - `public interface PlatformTransactionManager extends TransactionManager`
    - `DataSourceTransactionManager`, `JtaTransactionManager`, `HibernateTransactionManager` 등이 구현체
  - `PlatformTransactionManager.getTransaction(TransactionDefinition)` 는 TransactionStatus를 리턴해주는데, TransactionStatus는 새로운 트랜잭션이거나 기존에 존재하는 트랜잭션을 나타낸다. TransactionStatus는 실행중인 스레드와 연관이 있다(이는 Java EE tranasaction context와 같다)
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

- Synchronizing Resources with Transactions
  - Low-level Synchronization Approach
     - spring에서 제공하는 래핑된 클래스 (ex. DataSourceUtils.getConnection(datasource))를 사용하면, spring에서 사용하고있는 풍성한 정보를 지닌 예외를 전달해줄뿐 아니라 이를 통해서 일관성을 유지할수도 있다. 또한, connection을 가져올때 기존에 사용하고있는 트랜잭션이 있다면 해당 트랜잭션을 가져오기도하고, 없다면 새로운 트랜잭션을 만든다. (새로운 트랜잭션을 만들어서 사용했다면, 이후에 만나는 tranaction 코드는 트랜잭션 전파속성에 따라서 재사용할수도있다. 같은 스레드에서 동작한다는 전제하에..)


- Understanding the Spring Framework’s Declarative Transaction Implementation
  - 트랜잭션 지원은 AOP 프록시를 통해 활성화 되며, 트랜잭션 어드바이스는 메타데이터(xml 또는 어노테이션)로 구동
  - 트랜잭션 메타데이터를 가지고 만든 AOP 프록시는 `TransactionInterceptor` 와 적당한 `TransactionManager` 구현체를 사용해 메소드 호출을 둘러싸고 트랜잭션을 실행한다 (`TransactionInterceptor`가 `TransactionManager`사용)
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
    - `protected`나 package-visible 메서드(디폴트 메서드)에서 사용하고싶다면 아래와 같이 Configuration에 추가필요
      - 그러나 인터페이스 기반 proxy는(cglib 아님) 항상 `public`만 가능
      ```java
        @Bean
        TransactionAttributeSource transactionAttributeSource() {
            return new AnnotationTransactionAttributeSource(false);
        }
      ```



---

- 코드분석
  - `TransactionInterceptor`
    - advice로 등록되어 부가기능 수행하는 주체
      - Advice : 타깃 오브젝트에 적용하는 부가기능을 담은 오브젝트. 타깃 오브젝트에 종속되지않는다! 순수한 부가기능!(ex. MethodInterceptor를 구현한 오브젝트)
    ```java
      public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {
          @Nullable
          public Object invoke(MethodInvocation invocation) throws Throwable {
              Class<?> targetClass = invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
              return this.invokeWithinTransaction(invocation.getMethod(), targetClass, new CoroutinesInvocationCallback() { // 요기가 이제 실제 실행하는 핵심
                  @Nullable
                  public Object proceedWithInvocation() throws Throwable {
                      return invocation.proceed();
                  }

                  public Object getTarget() {
                      return invocation.getThis();
                  }

                  public Object[] getArguments() {
                      return invocation.getArguments();
                  }
              });
          }
        
      }

      
      public abstract class TransactionAspectSupport implements BeanFactoryAware, InitializingBean {
          @Nullable
          protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass, final TransactionAspectSupport.InvocationCallback invocation) throws Throwable {
              // 간단하게 요약하면..

              // 1. TransactionManager를 결정 (ex. PlatformTransactionManager의 구현이 뉘귀..?)
              TransactionAttributeSource tas = this.getTransactionAttributeSource();
              TransactionAttribute txAttr = tas != null ? tas.getTransactionAttribute(method, targetClass) : null;
              TransactionManager tm = this.determineTransactionManager(txAttr);
              PlatformTransactionManager ptm = this.asPlatformTransactionManager(tm);

              // 2. 트랜잭션 셋팅
              TransactionAspectSupport.TransactionInfo txInfo = this.createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

              Object retVal;
              try {
                  // 3. 비지니스 로직 수행
                  retVal = invocation.proceedWithInvocation(); 
              } catch (Throwable var20) {
                  this.completeTransactionAfterThrowing(txInfo, var20); // 여기서 예외에 따라 롤백이 될수도 있고 커밋이 될수도 있고..
                  throw var20;
              } finally {
                  this.cleanupTransactionInfo(txInfo);
              }

              // ...

              // 4. 커밋수행
              this.commitTransactionAfterReturning(txInfo); 
              return retVal;

          }

          protected TransactionAspectSupport.TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm, @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {
              // ...
              TransactionStatus status = null;
              if (txAttr != null) {
                  if (tm != null) {
                      status = tm.getTransaction((TransactionDefinition)txAttr); // 트랜잭션 만들고 (transaction start도 함)
                  } else if (this.logger.isDebugEnabled()) {
                      this.logger.debug("Skipping transactional joinpoint [" + joinpointIdentification + "] because no transaction manager has been configured");
                  }
              }

              return this.prepareTransactionInfo(tm, (TransactionAttribute)txAttr, joinpointIdentification, status);
          }

          protected void commitTransactionAfterReturning(@Nullable TransactionAspectSupport.TransactionInfo txInfo) {
              if (txInfo != null && txInfo.getTransactionStatus() != null) {
                  if (this.logger.isTraceEnabled()) {
                      this.logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
                  }

                  txInfo.getTransactionManager().commit(txInfo.getTransactionStatus()); // 커밋 수행시에 TransactionManager에게 commit요청 (TransactionManager는 트랜잭션관련하여 모든걸 관리하니.. 롤백도 마찬가지다..)
              }

          }
      }

      public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager, Serializable { // 트랜잭션관리 해주는놈 (TrnasactionManager)
          // ...
          public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
              TransactionDefinition def = definition != null ? definition : TransactionDefinition.withDefaults();
              Object transaction = this.doGetTransaction();
              boolean debugEnabled = this.logger.isDebugEnabled();
              if (this.isExistingTransaction(transaction)) {      // 기존 트랜잭션이 있는지도 확인함. 이때 TransactionManager의 구현체에따라 확인방법이 다름, DatasourceTransactionManager냐 JpaTransactionManager냐 등등..
                  return this.handleExistingTransaction(def, transaction, debugEnabled);
              } else if (def.getTimeout() < -1) {
                  throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
              } else if (def.getPropagationBehavior() == 2) {
                  throw new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'");
              } else if (def.getPropagationBehavior() != 0 && def.getPropagationBehavior() != 3 && def.getPropagationBehavior() != 6) {
                  if (def.getIsolationLevel() != -1 && this.logger.isWarnEnabled()) {
                      this.logger.warn("Custom isolation level specified but no actual transaction initiated; isolation level will effectively be ignored: " + def);
                  }

                  boolean newSynchronization = this.getTransactionSynchronization() == 0;
                  return this.prepareTransactionStatus(def, (Object)null, true, newSynchronization, debugEnabled, (Object)null);
              } else {
                  AbstractPlatformTransactionManager.SuspendedResourcesHolder suspendedResources = this.suspend((Object)null);
                  if (debugEnabled) {
                      this.logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
                  }

                  try {
                      return this.startTransaction(def, transaction, debugEnabled, suspendedResources); // 여기가 트랜잭션 시작! 
                  } catch (Error | RuntimeException var7) {
                      this.resume((Object)null, suspendedResources);
                      throw var7;
                  }
              }
          }

      }

    ```

- 추가로 찾아볼 것
  - 런타임 위빙, 로드타임 위빙
    - chatgpt 답변 (4.0)
    ```md
      런타임 위빙(Runtime Weaving)과 로드타임 위빙(Load-Time Weaving, LTW)은 어스펙트 지향 프로그래밍(AOP)에서 사용되는 두 가지 주요 기술입니다:

      1. **런타임 위빙(Runtime Weaving)**: 
        - 런타임 위빙은 프로그램 실행 중에 어스펙트(Aspect)를 적용하는 방식입니다.
        - 프록시 기반 AOP가 여기에 속하며, Spring AOP가 이를 사용합니다.
        - 런타임에서 대상 객체의 프록시를 생성하고, 이 프록시를 통해 어드바이스(Advice)를 적용합니다.

      2. **로드타임 위빙(Load-Time Weaving)**:
        - 로드타임 위빙은 클래스가 JVM에 로드될 때 어스펙트를 적용하는 방식입니다.
        - 이를 위해서는 특별한 클래스 로더가 필요하며, AspectJ가 대표적인 예입니다.
        - 로드타임 위빙은 더 강력하고 유연하지만, 설정과 사용이 더 복잡할 수 있습니다.

      런타임 위빙은 성능 측면에서 오버헤드가 있을 수 있으나, 구현이 간단하고 직관적입니다. 반면, 로드타임 위빙은 더 광범위한 적용이 가능하고 성능상 이점을 가질 수 있으나, 설정과 사용이 복잡할 수 있습니다.

    ```