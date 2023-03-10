SLF4J_정리
- [sl4j logger 사용방법](http://dveamer.github.io/backend/HowToUseSlf4j.html)
  - 되도록 "{}"를 잘 활용하라! 
    - log.info("hi {}","hello") 이렇게 남기면, "hi {}" 라는 문자열은 생성되지만, ~~"hello"는 해당 info 레벨일떄만 생성된다~~
      - "hello"는 리터럴 문자열이니 바로 생성이 될듯함
      - 다만 User user 와 같은 오브젝트를 넘겨줄때, 이에대한 toString 호출의 시점이 현재 셋팅된 로그레벨과 info 레벨이 맞는지 확인한 이후에 진행되기에 warn과 같은 로그레벨로 셋팅되어있다면 toString을 호출하지않게되어 성능상의 이점이 있다고 하는것 같음
  - 3개이상의 파라미터를 넘긴다면, 로그레벨 체크전에 object[]를 생성한다함 (Object ... => Obejct[])
    - 최대한 클래스의 toString()을 잘 활용하여 파라미터를 줄여라!
  - Throwable 객체를 마지막에 넘기게되면, stack trace를 로깅해준다!
    - logger.error("User : {}", user, ex); 
    - 이거쓸때도 파라미터 3개 이상되지않도록해야한다!
  
- [slf4j란](https://livenow14.tistory.com/63)