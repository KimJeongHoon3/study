maven
- maven dependency 매커니즘
  - 상당히 많은 dependency를 통해서 라이브러리들을 불러오게되는데, 이에따라 transitive(이행성) dependency가 생긴다.
    - 이행성?
      - 어떤 이항관계에서 갑이 을에 관계되고 을이 병에 관계되는 경우 반드시 갑이 병에 관계되는 것을 요구하는 조건.
      - 즉 A->B 를 의존하고, B->C를 의존할떄, A->C를 의존하는 관계.. 
  - 이때에, 여러 dependency간에 어떻게 사용할것인지를 정의하는 정책들이 있는데, 아래와 같음
    - Dependency mediation
      - "nearest definition" 정책을따른다.
        - depth가 똑같으면 먼저 선언된게 win
        - depth가 덜 깊은쪽이 win (dependency tree에서 가까운쪽!)
        - 만약 모듈을 통해서 의존하게된 라이브러리 버전이 있을때, 루트에서 직접 지정한 라이브러리가 있다면, 당연 루트에서 지정한 라이브러리 버전이 win
        ```java
            A
            ├── B
            │   └── C
            │       └── D 2.0
            └── E
                └── D 1.0
                "여기서 D 1.0이 win"
        ```
    - Dependency management
      - 이 섹션을 통해서 transitive dependency를 만났을때 버전을 지정할수있고, 같은 pom내에서 버전을 지정하지않았다면, 해당 섹션에서 지정한 버전을 사용한다
    - Dependency scope
      - dependency의 scope를 지정할수있음
        - compile
        - provided
          - compile과 매우 유사하나, runtime시에 가져오지않음.. 말 그대로 (다른곳에서) 이미 제공되었으니, 해당 dependency는 런타임시엔 추가하지말라는뜻
        - runtime
        - test
        - system
        - import

  - 참고 사이트
    - [Maven – Introduction to the Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
    - [Maven의 Transitive Dependency 길들이기 | The Sapzil](https://blog.sapzil.org/2018/01/21/taming-maven-transitive-dependencies/)
- `pom.xml`에서 모듈간 혹은 모듈 Import시 루트 프로젝트와 겹치는 라이브러리가 있다면?
  1. 프로젝트 실행시 maven에 exclusion 해놓으면 다른 모듈에 있어도 읽어오지않는다
  2. 모듈로 가져오는 버전보다 당연 로드되는 프로젝트 버전이 우선
  3. 만약 모듈끼리 겹치면..?
      - `mvn dependency:tree` pom.xml 선언되어있는 라이브러리 트리구조 확인가능
        - `maven helper` 플러그인 다운받아서 하는게 깔끔..
          - 루트 프로젝트에 있는 동일한 라이브러리 모듈에 있다면, 이는 충돌로 나타나진않는다.. 그냥 무조건 루트 프로젝트에 있는 라이브러리 모듈 적용 (ex. spring-boot-starter-undertow:1.5.13.RELEASE 를 로드하는 프로젝트에서 spring-boot-starter-undertow:2.7.3 을 사용하는 모듈을 import할때, 충돌이 아닌 무조건 1.5.13 버전으로.. )
            - 그래서 참고로 `spring-boot-autoconfigure`에서 `spring.factories` 가 충돌날수없다.. 어차피 한 버전만 실행되므로..
      - 충돌 라이브러리를 포함한 라이브러리를 확인하여 별도로 해당 라이브러리를 Exclude하고 충돌일으키는 라이브러리는 원하는 버전을 명시하는 식으로 관리하면 좋을듯
      
      - https://lng1982.tistory.com/309

   - gradle 충돌인데, 나중에 필요하면 사용해보자..
     - https://velog.io/@mu1616/spring-dependency-management%EB%A1%9C-%EC%9D%B8%ED%95%9C-gradle-%EB%B2%84%EC%A0%84-%EC%B6%A9%EB%8F%8C%EB%AC%B8%EC%A0%9C

- [parent를 통한 boot 의존성관리 설명](https://recordsoflife.tistory.com/393)
- [모듈구성시참고](https://eblo.tistory.com/144)
- [maven 간단정리](https://thalals.tistory.com/345)
- [dependencyManagement와 dependecies 차이점](https://darkstart.tistory.com/238)
  - dependencyManagement를 쓰면 그냥 dependencies에서 의존성을 추가할때 dependencyManagement에 정의된 의존성이라면, 버전명시를 안해도 dependencyManagement에 있는 버전을 가져옴.. dependencyManagement 여기에 있는 모든 의존성을 가져오진않음