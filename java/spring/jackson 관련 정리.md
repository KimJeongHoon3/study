jackson 관련 정리

- mapping하는 클래스에 추상클래스 타입의 멤버변수가 있으면 제대로 생성못함
  - 해당 추상클래스에 @JsonDeserialize(as = Cat.class) 이렇게 선언해줘야함
  - 구현클래스가 많다면 [여기참고](https://www.baeldung.com/jackson-inheritance)
- default 생성자가 필요함.. 없으면 안댐
  - 여기서 lombok 사용시 주의사항
    - @Builder 를 사용하였을때, default생성자 만들기위해서 @NoArgsConsutructor를 선언해버리면, @Builder가 모든 변수를 받는 생성자를 만들지않는다.. 그렇기때문에 Builder를 사용하기위해서는 @AllArgsConstructor를 따로 선언해주어야한다!
    - 정리하면, @Builder를 사용하고잇을때 jackson으로 맵핑가능하려면, @NoArgsConsutructor,@AllArgsConstuctor 모두 선언해주어야함!
      - lombok 공식사이트 builder 설명 
        > Finally, applying @Builder to a class is as if you added @AllArgsConstructor(access = AccessLevel.PACKAGE) to the class and applied the @Builder annotation to this all-args-constructor. This only works if you haven't written any explicit constructors yourself.
- json의 rootName을 제거할필요가 있을경우네느 @JsonRootName을 사용
    ```java
        String str="{"user":{"id":1,"name":"John"}}" //아래의 @JsonRootName이 셋팅되어있어야 정상적으로 매핑가능

        @JsonRootName(value = "user")
        class User{
            long id;
            String name;
        }

    ```

- list<?> 로 받고 싶을때 아래처럼
    ```java
    @Test
    public void givenJsonOfArray_whenDeserializing_thenCorrect() 
    throws JsonProcessingException, IOException {
    
        String json
        = "[{"id":1,"name":"John"},{"id":2,"name":"Adam"}]";
    
        ObjectMapper mapper = new ObjectMapper();
        List<User> users = mapper.reader()
        .forType(new TypeReference<List<User>>() {})
        .readValue(json);

        assertEquals(2, users.size());
    }
    ```

- 매핑할수없는 key가 있을때
    ```java
    @Test
    public void givenJsonStringWithExtra_whenConfigureDeserializing_thenCorrect() 
    throws IOException {
    
        String json = "{"id":1,"name":"John", "checked":true}"; //checked는 매핑할수없는 key

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        User user = mapper.reader().forType(User.class).readValue(json);
        assertEquals("John", user.name);
    }

    OR
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class User {...}
    ```
- null이 아닌 값만 json으로 말고싶을때 : @JsonInclude
    ```java
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @AllArgsConstructor
        public static class MyBean {
            public int id; 
            public String name;  //null일 경우 json으로 말때 제외
        }
    ```

- `LocalDateTime` 을 원하는 포맷으로 변경하고싶을때
  - `@JsonFormat` 사용
    - ex. `@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")`
  - 그냥 단순히 `ObjectMapper`를 사용하면 `LocalDateTime` 파싱안됨
    - 원하는 포맷으로 셋팅하기위해서는 `jackson-datatype-jsr310` 모듈과 `ObjectMapper`에 아래와 같은 셋팅필요
    ```java
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule()); // jsr310 모듈에 있음
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 직렬화할때 한줄로 깔끔하게 찍어줌 Ex. "2020-01-01T00:00:00.123"
    ```
  - [참고](https://umanking.github.io/2021/07/24/jackson-localdatetime-serialization/)
- 보통 boolean은 isXXX 라고 필드명을 명시하는 경우가 많은데, 이와같은 경우 jackson은 정상파싱안됨 (메서드를 찾는것으로 인식하는것같음.. 좀 더 정확하게 찾아보자..)
  - `@JsonPropery`를 사용해서 역직렬화할때 참고할수있도록 이름을 명시해줘야함
    ```java
      @JsonProperty("isAvailable")
      private boolean isAvailable;
    ```

- jackson 기본생성자 없이 deserialize 하는 방법
  - `ParameterNamesModule` 모듈 추가하면됨
    - `objectMapper.registerModule(new ParameterNamesModule());` 
    - java 컴파일러에 `-parameters` 옵션을 셋팅해야함
      - 이 옵션은 생성자나 메서드의 파라미터가 컴파일시에 arg0, arg1 이런식으로 변경되지않고 코드상에서 네이밍한 그대로 보여준다
      - 그래서 이를 활용하면 별도로 @JsonCreator를 명시한 생성자에 @JsonProperty("변수명")을 쓰지않아도됨..
  - 근데 주의사항은 단 하나의 파라미터만 받는 생성자에는 `@JsonCreator` 이거를 해당 생성자에 추가해야한다.
    - 이렇게 만든것은 LEGACY를 보존하기위해서라함
    - https://github.com/FasterXML/jackson-modules-java8/tree/2.14/parameter-names

- 참고사이트
  - https://www.baeldung.com/jackson-exception

- Enum type 의 serialize, deserialize 정리 매우굿 : 
  - https://velog.io/@recordsbeat/JacksonObjectMapper%EB%A1%9C-Enum%EC%BB%A8%ED%8A%B8%EB%A1%A4-%ED%95%98%EA%B8%B0
  - https://www.baeldung.com/jackson-serialize-enums

- jackson 기본적인 사용법들 정리
  - https://www.baeldung.com/jackson-annotations
    - 기본 사용법은 거의 여기 참고하면될듯
    - https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
  - https://interconnection.tistory.com/137  
- localdate로 직렬화
  - https://velog.io/@recordsbeat/Jackson%EC%9C%BC%EB%A1%9C-LocalDate-%EC%9E%90%EB%8F%99-%EB%A7%A4%ED%95%91%ED%95%98%EA%B8%B0

- jackson custom serialization 
  - deserialization 도 동일하기에 참고하면 좋음
  - https://homoefficio.github.io/2016/11/18/%EC%95%8C%EA%B3%A0%EB%B3%B4%EB%A9%B4-%EB%A7%8C%EB%A7%8C%ED%95%9C-Jackson-Custom-Serialization/

- json 상속관련 설명 굿
  - https://kobumddaring.tistory.com/51

- jackson 공식문서
  - https://github.com/FasterXML/jackson-docs

- jackson 어떤 필드가 직렬화되는지 설명
  - https://www.baeldung.com/jackson-field-serializable-deserializable-or-not
    - 기본적으로 public 인 필드는 직렬화가능
    - getter 사용시에 private 필드 직렬화 가능 (getter가 있으면 역직렬화도 알아서 된다. 필드가 있을거라 가정하여..)
    - setter는 역직렬화만 가능..(getter이름이 일반적이지 않은 경우..) 직렬화는 불가 
    - 클래스의 소스코드를 수정할 수 없는경우(ex. getter를 추가하지못한다거나..) objectMapper의 셋팅을 수정하라
      - private 상관없이 모든 필드에 대해서 직렬화가능하도록 셋팅
        ```java
          ObjectMapper mapper = new ObjectMapper();
          mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
          mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        ```
    - `@JsonProperty` 를 사용해서 직렬화/역직렬화시 필드의 이름(키) 변경가능
    - `@JsonIgnore` 를 사용해서 직렬화시 해당 필드를 제외하도록 할 수 있음