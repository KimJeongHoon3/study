effective_java_객체생성과파괴

- 아이템1_생성자대신 정적 팩토리 메서드를 고려하라
  - 기타 팁
    - 플라이 웨이트 패턴?
      - 캐싱과 동일개념
      - 팩토리에서 인스턴스를 반환해줄때 팩토리에 저장되어있는 객체면 새로 생성하지않고, 기존에 저장되어있는것 반환. 그렇지않으면 새로생성하여 내부에 저장한뒤 반환
      - 참고사이트
        - https://velog.io/@hoit_98/%EB%94%94%EC%9E%90%EC%9D%B8-%ED%8C%A8%ED%84%B4-Flyweight-%ED%8C%A8%ED%84%B4
        - https://refactoring.guru/ko/design-patterns/flyweight
    - ServiceLoader?
      - https://velog.io/@adduci/Java-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%A1%9C%EB%8D%94ServiceLoader


- 아이템3_
  - 기타 팁 
    - [객체 직렬화시 readResolve와 readObject](https://madplay.github.io/post/what-is-readresolve-method-and-writereplace-method)
    - 리플렉션 공격??
      - 

- 아이템7_
  - 기타 팁
    - weakHashMap
      - JVM GC는 reference라는 참조방식에 따라 GC 하는 시점이 달라진다
        - strong refernce
        - soft reference
        - weak reference
          - weakHashMap
      - [내용설명 굿](https://blog.breakingthat.com/2018/08/26/java-collection-map-weakhashmap/)
      - [내용설명 굿](https://jake-seo-dev.tistory.com/11)