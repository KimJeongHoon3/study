# java SE와 EE 차이

- JAVA SE (Java Standard Edition) : 자바 표준 에디션은 가장 기본이 되는 에디션입니다.흔히 자바 언어라고 하는 대부분의 패키지가 포함된 에디션이며 주요 패키지로는 java.lang.\*, java.io.\*, java.util.\*, java.awt.\*, javax.rmi.\*, javax.net.\* 등이 있습니다.

- JAVA EE (Java Enterprise Edition) : 자바로 구현되는 웹프로그래밍에서 가장 많이 사용되는 JSP, Servlet을 비롯하여, 데이터베이스에 연동하는 JDBC, 그 외에도 JNDI, JTA, EJB 등의 많은 기술들이 포함되어 있습니다. 자바 EE 플랫폼은 자바 SE 플랫폼을 기반으로 그 위에 탑재.
  - 대표구성요소
    - Servlet
      - 클라이언트가 보내는 HTTP 요청을 처리하는 서버측 자바 프로그램.
    - JSP(Java Server Pages)
      - HTML이나 Java 코드를 써서 사용자에게 정보를 출력. JSP가 처음 실행될 때 Servlet 엔진이 이것을 Servlet 으로 컴파일시켜서 내부적으로는 Servlet으로 동작한다.
    - EJB(Enterprise Java Beans) 
      - Java에서 제공하는 분산 컴포넌트 기술로 비즈니스 로직이나 데이터, 메시지를 처리할 수 있다.
    - Java Database Connector(JDBC)
      - 여러 종류의 데이터베이스 시스템에 접근하는 단일한 인터페이스를 제공. 각각의 데이터베이스에 맞는 JDBC 드라이버가 있어야 한다.
    - Remote Method Invocation(RMI)
      - 프록시를 써서 원격에 있는 Java 객체의 메소드를 실행시키기 위한기술.
    - Java Naming DirectoryInterface (JNDI)
      - 자바 기술로 만들어진 객체에 이름을 붙여 찾을 수 있도록 단일한인터페이스를 제공.
    - Java Connector Architecture(JCA)
      - 이기종 플랫폼을 통합할 수 있도록 플랫폼 독립적인 인터페이스를 제공.
    - Java Message Service (JMS)
      - 여러 가지 메시징 시스템에 대한 플랫폼 독립적인 인터페이스를 제공.


* 엔터프라이즈 시스템: 서버에서 동작하며 기업과 조직의 업무를 처리해주는 시스템. 많은 사용자들의 요청을 동시 처리 해야하므로 서버지원을 효율적으로 공유하고 분배해서 사용할 수 있어야 한다. 또한 중요한 기업 핵심정보 다루기때문에 보안,안정성,확장성 이 요구된다.
웹을 통한 유저인터페이스 뿐만 아니라 타 시스템과의 자동화된 연계과 웹 이외의 클라와의 접속 위한 리모팅 기술도 요구된다.



- 참고사이트
  - https://cheershennah.tistory.com/74 [Today I Learned. @cheers_hena 치얼스헤나]
  - https://210life.tistory.com/entry/Java-EE와-Java-SE의-차이점 [210 Life]
 