@Primary 사용주의사항

- spring에서 @Bean 생성시 @Primary 사용하는데 해당 객체가 추상클래스를 구현했다면, 추상클래스로 주입받는 모든 빈들은 모두 @Primary로 선언된 객체를 주입받는다.

```java
    @Component
    public class Test {
        @Autowired
        private AbstractClazz normalClazz;

        @Autowired
        private AbstractClazz specialClazz;

        @Autowired
        private AbstractClazz hotClazz;

        @Autowired
        private AbstractClazz heyClazz;

        // ...
    }

    @Configuration
    public class Config {
        @Bean
        AbstractClazz normalClazz() {
            return new NormalClazz();
        }

        @Bean
        @Primary // 
        AbstractClazz specialClazz() {
            return new SpecialClazz();
        }

        @Bean
        AbstractClazz hotClazz() {
            return new HotClazz();
        }

        @Bean
        AbstractClazz heyClazz() {
            return new HeyClazz();
        }
    }

    public class NormalClazz extends AbstractClazz {

    }

    public class SpecialClazz extends AbstractClazz {
        
    }
    
    public class HotClazz extends AbstractClazz {
        
    }

    public class HeyClazz extends AbstractClazz {
        
    }

    // 보통은 Test 클래스에 타입이 같기떄문에 bean 이름 기반으로 주입이 될텐데, specialClazz 빈생성시 @Primary를 선언하게되면 AbstractClazz로 선언된 모든 클래스는 specialClazz 빈으로 주입된다

```