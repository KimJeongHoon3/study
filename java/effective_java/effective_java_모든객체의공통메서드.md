effective_java_모든객체의공통메서드

- Object에서 final이 아닌 메서드(equals, hashCode, toString, clone, finalize) 는 모두 재정의에 염두에 두고 설계된 것이라 재정의 시 지켜야하는 일반 규약이 명확히 정의되어있다

---

- 아이템10_equals는 일반 규약을 지켜 재정의하라
  - equals를 재정의하지않는게 좋은경우
    - 각 인스턴스가 본질적으로 고유
      - 값을 표현하는게아니라, 동작하는 개체를 표현하는 클래스 (ex. Thread)
    - 인스턴스의 논리적 동치성을 검사할 일이 없다
      - 두 Pattern 객체간에 굳이 같은 정규표현식인지를 확인해야하는가?
    - 상위클래스에서 재정의한 equals가 하위클래스에도 딱 들어맞는다
      - Set 구현체는 AbstractSet이 구현한 equals를 그대로사용
        - List, Map 모두 동일
    - 클래스가 private 이거나 package-private이고, equals 메서드를 호출할 일이 없을 경우
  - 언제 재정의하는게 좋나?
    - 두 객체가 물리적(메모리주소)으로 같은지가 아닌, 논리적 동치성을 확인해야할때, 상위클래스의 equals가 논리적 동치성을 비교하도록 재정의하지않았을 경우
    - 주로 값 객체
      - ex. Integer, String
      - 값 객체라해도, 인스턴스가 둘 이상 만들어지지않음을 보장하는 인스턴스(Ex. Enum)는 equals 재정의 필요x
        - 객체식별성(물리적주소)과 논리적 동치성이 결국 같은의미가 되니..
  - Object 명세에 적힌 equals 메서드 재정의 규약
    - 반사성: null이 아닌 모든 참조값 x에 대해 `x.equals(x)` 는 true 이다
      - 객체는 자기 자신과 같아야한다.
    - 대칭성: null이 아닌 모든 참조값 x,y에 대해 `x.equals(y)` 가 true 이면 `y.equals(x)` 도 true 이다
      - 두 객체의 서로에 대한 동치 여부에 똑같이 답해야한다
    - 추이성: null이 아닌 모든 참조값 x,y,z에 대해 `x.equals(y)` 가 true 이고 `y.equals(z)` 도 true 이면, `x.equals(z)` 도 true 이다
      - 구체클래스를 확장해(extends) 새로운값(필드)을 추가하면서 equals 규약을 만족시킬 방법은 존재하지않는다
      - 억지로 상위클래스를 무시하고 정확히 해당 클래스만 같도록 만들어버리면 리스코프 치환 원칙위배하게 됨
        - 리스코프 치환 원칙: 어떤 타입에 있어 중요한 속서잉라면, 그 하위타입에서도 마찬가지로 중요하다
        - 즉, Point의 하위클래스(Ex. CounterPoint extends Point)는 정의상 여전히 Point이므로, Point로써 활용될 수 있어야한다.
      - 대신 컴포지션을 활용해 우회방법으로 해결할수있다
        ```java
            public class ColorPoint {
                private final Point point;
                private final Color color;

                public ColorPoint(int x, int y, Color color) {
                    point = new Point(x,y);
                    this.color = Objects.requireNonNull(color);
                }

                public Point asPoint() { // 이렇게 Point를 반환해주는 뷰를 만들어서 따로 비교하도록 해준다
                    return point;
                }

                @Override
                public boolean equals(Object o) { // equals는 ColorPoint만 비교할수 있도록!
                    if (!(o instanceof ColorPoint)) 
                        return false
                    ColorPoint cp = (ColorPoint) o;
                    return cp.point.equals(point) && cp.color.equals(color);
                }
            }
        ```
      - 추이성을 위반한 잘못된 예
        - `java.sql.Timestamp`
          - Date 클래스를 상속하고있음.
          - api 설명에서도 Date와 같게 보지말라고 명시되어있음
    - 일관성: null이 아닌 모든 참조값 x,y에 대해 `x.equals(y)` 를 반복해서 호출하면 항상 true 이거나 항상 false 이어야한다
      - 불변 클래스로 만들기로 했다면, equals가 한번 같다고 한 객체와는 영원히 같아야하고, 다르다고한 객체와는 영원히 달라야한다
      - equals는 항시 메모리에 존재하는 객체만을 사용한 결정적(deterministic) 계산만 수행해야한다
        - 결정적(deterministic): 입력값이 같으면 출력값이 항상 같아야함.. (결정되어있어야한다는 의미인듯)
        - 위반사례: `URL` class
          ```java
            // URL class
            public boolean equals(Object obj) {
                if (!(obj instanceof URL))
                    return false;
                URL u2 = (URL)obj;

                return handler.equals(this, u2); // 여기 handler는 URLStreamdHandler
            }

            /////////////////////////////////////////////

            // URLStreamHandler class
            protected boolean equals(URL u1, URL u2) {
                String ref1 = u1.getRef();
                String ref2 = u2.getRef();
                return (ref1 == ref2 || (ref1 != null && ref1.equals(ref2))) &&
                      sameFile(u1, u2);
            }

            protected boolean sameFile(URL u1, URL u2) {
                // Compare the protocols.
                if (!((u1.getProtocol() == u2.getProtocol()) ||
                      (u1.getProtocol() != null &&
                      u1.getProtocol().equalsIgnoreCase(u2.getProtocol()))))
                    return false;

                // Compare the files.
                if (!(u1.getFile() == u2.getFile() ||
                      (u1.getFile() != null && u1.getFile().equals(u2.getFile()))))
                    return false;

                // Compare the ports.
                int port1, port2;
                port1 = (u1.getPort() != -1) ? u1.getPort() : u1.handler.getDefaultPort();
                port2 = (u2.getPort() != -1) ? u2.getPort() : u2.handler.getDefaultPort();
                if (port1 != port2)
                    return false;

                // Compare the hosts.
                if (!hostsEqual(u1, u2)) // 문제가 될 수 있는 부분!! => 여기서 host 주소를 불러와서 확인을 한다.. 즉, 도메인과 host ip가 달라지면 equals 에러유발! => 입력값이 같으나 출력값이 달라진다!
                    return false;

                return true;
            }

          ```

    - null 아님 : `x.equals(null)` 은 false 이다
      - nullPointerException 을 던지지말자
      - 묵시적 null 검사를하자
        - `if(!(o instanceof MyType)) return false`
        - instanceof 는 뒤에 피연산자와 상관없이 (여기서는)o 가 null 이면 false를 반환한다
  - 양질의 equals 메서드를 구현하는 방법 (단계별)
    1. `==` 연산자를 통해 입력이 자기 자신의 참조인지 확인한다
         - 단순 성능 최적화용으로, 비교작업이 복잡한 상황일때, 자기자신이면 바로 반환해줄수있으니 좋다
    2. instanceof 연산자로 입력이 올바른 타입인지 확인하자
         - 보통은 equals가 정의된 class를 instanceof의 피연산자로 사용하는데, 그 클래스가 구현한 인터페이스가 될수도있다
           - 이때, 이를 구현한 클래스끼리도 비교할수있도록 equals를 수정하기도함
           - `List`, `Map`, `Map.Entry`, `Set` 등
         ```java
           // ArrayList의 equals 메서드

           public boolean equals(Object o) {
               if (o == this) {
                   return true;
               }

               if (!(o instanceof List)) { // ArrayList를 확인하지않고, List 구현체 인지만 확인한다
                   return false;
               }

               final int expectedModCount = modCount;
               // ArrayList can be subclassed and given arbitrary behavior, but we can
               // still deal with the common case where o is ArrayList precisely
               boolean equal = (o.getClass() == ArrayList.class)
                   ? equalsArrayList((ArrayList<?>) o)
                   : equalsRange((List<?>) o, 0, size);    // List 구현체이나, ArrayList가 아니면 여기서 별도 확인해준다

               checkForComodification(expectedModCount);
               return equal;
           }
         ```
    3. 입력을 올바른 타입으로 형변환
    4. 입력객체와 자기자신의 대응되는 "핵심" 필드들이 모두 일치하는지 하나씩 검사
       - 비교시 double, float을 제외한 기본 타입필드는 `==` 연산자를 사용
       - 참조타입은 필드는 `equals` 사용
       - float, double은 각각 `Float.compare(float, float)`, `Double.compare(double,double)` 를 사용
         - 이는 부동소수점과 Float.NaN, -0.0f 등의 경우 때문
         - `Float.equals` 와 `Double.equals` 를 사용하지않는것은 오토박싱 수반할수있기때문..
       - 배열의 모든 원소를 모두 비교해야하는거면 `Arrays.equals` 사용하자
       - null도 정상값으로 취급하는 참조타입필드가 있다면, `Objects.eqauls(Object, Object)` 를 사용하자. 이는 NullPointerException을 예방해준다!

  - 마지막 주의사항..
    - equals 재정의할때 hashCode도 반드시 재정의하자!
    - 필드들의 동치성만 검사해도 equals 규약을 지킬수있으니, 너무 복잡하게 생각하지말자
    - Object외의 타입을 매개변수로 받은 equals 메서드 선언하지말자
      - `equals(Object)` 만 사용하고, `equals(MyType)` 과 같은 오버로딩을 하지말자..

  - 기타 팁
    - equals의 규약을 어긴다면, 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 예측할수가 없어진다.. (예상치못한 side effect 발생..)
    - 되도록 직접 equals를 만들지말고 AutoValue 같은 프레임워크를 사용하자 (IDE도 괜춘하지만 리팩토링필요하면 계속 변경해주어야..)
      - [AutoValue에 대한 사용방법 및 사용이유 등 나와있음](https://www.baeldung.com/introduction-to-autovalue)
    - 부동소수점
      - <span style="color:red">관련해서 내용추가해보자!</span>

---

- 아이템11_equals를 재정의하려거든 hashCode도 재정의하라
    