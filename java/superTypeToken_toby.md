슈퍼 타입 토큰

- `String.class`는 ***클래스 리터럴***이라고하며, 이를 인자로 넘겨받은(`Class<T>`로 선언) `Class<String>` 을 ***타입토큰***이라 할수있음
  - 타입토큰은 쉽게말해 타입을 나타내는 토큰.. 클래스 리터럴이 타입토큰으로서 사용되는것!
- 타입토큰을 활용하여 타입 안정성을 보장할 수 있다!

- 슈퍼타입 토큰이 왜 필요한가?
  - `Class<T>`로 나타내는 타입토큰으로는 제네릭을 표현할수 없음..(ex. `List<String>.class` 는 에러..) 
- `Collection<String>` 과 같이 제네릭을 사용하여 선언한것을 parametrizedType이라함
  - 이 인터페이스는 다이아몬드 연산자 안의 타입들을 가져올수있는 함수도 가짐(getActualTypeArguments 호출시 배열반환하는데, 다이아몬드연산자에 타입들 선언한거 순서대로 배열로반환하는것!)

- 슈퍼타입토큰은 대략적으로 이렇게 구현하면됨
  ```java
      static class TypeReference<T>{  //superTypeToken
          Type type;

          public TypeReference() {

              Type sType=this.getClass().getGenericSuperclass(); //결국 요렇게 쓴것은 상속을 받아서 사용해야만한다는것을 의미!(그리고 상속을 받아야지만 제네릭으로 받은 타입이 컴파일시에 지워지지않음.. 그래서 이렇게 가져올수있는것!)
              if(sType instanceof ParameterizedType){  //ParamterizedType으로 형변환 할 수 있다는것은 제네릭으로 되어있다는뜻! 즉, Class가 아니다! 만약, String과 같은 타입이면 sType은 그냥 Class<String> 으로 캐스팅 가능하다
                  this.type=((ParameterizedType) sType).getActualTypeArguments()[0];
              }else{
                  throw new RuntimeException("this is not ParameterizedType..");
              }
          }

          @Override
          public boolean equals(Object o) {
              if (this == o) return true;
              if (o == null || getClass().getSuperclass() != o.getClass().getSuperclass()) return false; //익명 클래스로 만들어지니깐.. 슈퍼 클래스가 같은지로 체크해야함..
              TypeReference<?> that = (TypeReference<?>) o;
              return type.equals(that.type);
          }

          @Override
          public int hashCode() {
              return Objects.hash(type);
          }
      }

      // 사용은 new TypeReference<List<String>>(){} 이런식으로 써야함.. "{}" 꼭 있어야함!
  ```
- 수퍼 타입 토큰을 사용방법을 이해하기위해서 알아야할 내용
  ```java
    public class SuperTypeToken {
        //NESTED STATIC CLASS
        static class Sup<T>{
            T value;
        }

        //NESTED STATIC CLASS
        static class Sub extends Sup<List<String>>{

        }

        void method(){
            // LOCAL CLASS
            // => 메소드 안에서 한번 사용하는 클래스..
            class Sub extends Sup<List<String>>{

            }

            // ANONYMOUS CLASS
            // => LOCAL CLASS에서 이름도 필요없다!
            // => Sub라는 클래스 이름도 없고, 그냥 바로 인스턴스를 만든것임! Sup b 라고 타입과 변수를 선언한것은 그냥 Sup이라는 수퍼클래스 타입(다형성)으로 b라는 변수에 넣은것일뿐, 이름이 있는것은아니다.. 암튼 실제로는 Sup을 상속받은상태임!
            // => 이를 활용하여 수퍼 타입토큰을 가져올수있다!!!!
            Sup b=new Sup<List<String>>(){}; // "{}" 를 놓치지마라! Sup을 만든게 아니라, Sup을 상속한 이름없는 클래스를 만든것!
        }
    }
  ```
- 결론
  - 슈퍼타입토큰을 사용할수있도록 spring에서는 ParameterizedTypeReference 클래스를 제공하는데, 이는 사용할때 `new ParameterizedTypeReference<List<String>>() {}` 이렇게 뒤에 `{}`가 붙는다
    - 핵심은 body인 `{}` 를 빼먹으면안된다! 
      - 왜? 익명클래스 인스턴스를 만들어서 익명클래스가 상속하고있는 수퍼클래스의 타입파라미터(ParameterizedType)를 전달하기위해 사용한다!
        - `Class<?>` 요놈은 타입파라미터를 표현할수가 없거든! (런타임시에 제네릭 타입들은 제거)