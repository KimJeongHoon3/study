cqrs

- [활용방법](https://wiki.yowu.dev/ko/Knowledge-base/Spring-Boot/the-command-query-responsibility-segregation-cqrs-pattern-in-spring-boot)

- [왜 쓰는지에 대한정리](https://learn.microsoft.com/en-us/azure/architecture/patterns/cqrs)
  - CQRS를 구현하면 성능, 확장성, 보안을 극대화
    - => 성능과 확장성이 좋을듯함.. 성능은 기존 db 조회시에 문제있는부분을 몽고db와 같은걸 사용해서 가져옴.. 이는 분리가 되어있기때문에 가능. 조회가 분리되어있지않고 명령 도메인과 섞여있으면 복잡해짐. 필요한 데이터는 몇개 안되는데, 엔티티를 사용해서 모든 데이터를 가져온다고하면 성능에도 안좋음. 그리고 조인되어야하는 쿼리들 작업해야하는거면 더욱 복잡해짐. 그래서 필요한 데이터가 추가되었을때 명령부분은 건드리지않고 조회모델만 변경하면되기때문에 명령모델의 오염도 없다! 확장도좋고~