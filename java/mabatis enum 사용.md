mabatis enum 사용

- [정리매우 깔끔, 반복을 막아주는 셋팅도 설명](https://amagrammer91.tistory.com/115)
- [깊이 있고 깔끔한 정리](https://umbum.dev/1122)


- typehandler 사용시 주의할점
  - javatype이 enum인 대상은 mybatis가 내부적으로 EnumTypeHandler를 사용해서 처리한다..
    - 기본적으로 EnumTypeHandler는 enum클래스의 name으로 read 또는 write함
      - By default, MyBatis uses EnumTypeHandler to convert the Enum values to their names.
  - 그러나, 해당 typehandler를 별도로 지정했다면, EnumTypeHandler는 자동으로 셋팅되지않는듯함
    - 그래서 `defaultEnumTypeHandler` 설정이 잇는듯 (mybatis 3.4.5 이상부터)
      - Specifies the TypeHandler used by default for Enum. (Since: 3.4.5)
  
  - 그래서 3.3x와 3.5x는 다르게동작한다..
    - 3.5x는 enum을 위해 따로 커스텀 타입핸들러를 만들어놓고 셋팅했다면 이를 사용하고
    - 3.3x는 enum 타입 연동시 직접적으로 타입핸들러를 명시하지않는한 자동으로생성한 EnumTypeHandler를 사용하는듯
- [참고사이트](https://mybatis.org/mybatis-3/configuration.html#configuration)