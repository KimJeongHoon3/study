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

  - 기타 팁
    - equals의 규약을 어긴다면, 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 예측할수가 없어진다.. (예상치못한 side effect 발생..)