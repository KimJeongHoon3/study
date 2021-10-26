Springboot_gracefulshutdown


- 기본적으로 부트2.3부터는 아래와 같이 application.properties에 등록가능
```java
    server.shutdown=graceful //기본값은 immediate
    spring.lifecycle.timeout-per-shutdown-phase=1m 
```
  - 그러나 내부적으로 코드상에 직접 종료시켜야하는 부분이 있다면 아래와 같이 쓸것
  - 참고로 아래 콜백과 @PreDestroy는 종료 시그널 이후에 실행되지만, 구체적으로 종료시그널이후에 호출되는 시점의 차이가 있으니 잘 확인해서 적용할것!
    ```java
        // 1. 
        @SpringBootApplication
        public class Application {

            public static void main(String[] args) {
                SpringApplication application = new SpringApplication(Application.class);
                application.addListeners((ApplicationListener<ContextClosedEvent>) event -> { //종료 시그널 받으면 바로 실행됨
                    log.info("Shutdown process initiated...");
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                    } catch (InterruptedException e) {
                        log.error("Exception is thrown during the ContextClosedEvent", e);
                    }
                    log.info("Graceful Shutdown is processed successfully");
                });
                application.run(args);
            }
        }

        // 2.
        @SpringBootApplication
        @Slf4j
        public class SpringBootShutdownHookApplication {

            public static void main(String[] args) {
                SpringApplication.run(SpringBootShutdownHookApplication.class, args);
            }

            @PreDestroy // SpringBootShutdownHookApplication 빈의 자원해제시 실행되므로 종료시그널받고, 다른 빈들 거의 다 내부적으로 shutdown할거 다 한뒤에 마지막에 실행됨
            public void onExit() {
                log.info("###STOPing###");
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    log.error("", e);;
                }
                log.info("###STOP FROM THE LIFECYCLE###");
            }
        }
    ```