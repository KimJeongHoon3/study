- 스프링
  - 스프링 애플리케이션 컨텍스트라는 컨테이너가 컴포넌트(Bean)들을 생성하고 관리.
    - 여기서 bean을 상호연결시켜주는것이 의존성 주입(Dependency Injection) 이다
  - 스프링의 큰 강점은 자동구성(autoconfiguration) 인데, 이는 알아서 자동연결(autowiring) 과 컴포넌트 검색이라는 스프링 기법을 기반으로 한다
  - 이러한 자동구성의 기능이 더욱 확장된것이 스프링부트이다..
  - 스프링 Initializer
    - 프로젝트 디렉토리 구조생성과 빌드명세를 자동으로 정의해줌
    - 여러 외부 dependency도 선택하면 바로 적용
    - 빌드스크립트(maven 기준)를 보면, "starter" 라는 단어를 포함하는 라이브러리를 볼수있는데, 이는 자체적으로 라이브러리 코드를 갖는것이아니라, 부트 스타터의 의존성을 사용한다
      - 이렇게되면 빌드파일이 훨씬 줄어들고 관리하기가 쉬워진다
      - 그리고 부트 스타터가 관리해주기때문에 각각의 라이브러리들의 버전들이 상호호환되는것에 대한 걱징이 필요없어진다 
    - 스프링부트 devtools (개발할때만 사용됨. 배포시 적용x)
      - 코드변경시 자동으로 애플리케이션 다시시작
      - 브라우저로 전송되는 리소스가 변경될때 자동으로 브라우저를 새로고침
      - 템플릿 캐시를 자동으로 비활성화
    - 프레임워크를 사용하기위해 정의해야하는 코드들의 부담이 현저히 적어진다!(부트에서!) 그래서 좀더 어플리케이션 자체의 로직에 좀더 초점을 맞추어 개발이 가능하다
    - 톰켓내장..!

  - 빈 유효성 검사 API지원 (Hibernate 컴포넌트)  