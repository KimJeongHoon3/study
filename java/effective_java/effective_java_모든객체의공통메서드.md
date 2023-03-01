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
  - equals를 재정의한 클래스에서 hashCode를 재정의하지않으면, hashCode의 일반 규약을 어긴것이며, 이는 HashMap이나 HashSet 같은 컬렉션의 원소로 사용될때 문제를 일으킨다.
  - Object 명세에서 발췌한 규약
    - equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇번을 호출해도 일관되게 항상 같은 값을 반환 (단, 애플리케이션을 다시 실행한다면 이 값이 달라져도 된다)
    - equals(Object)가 두 객체를 같다고 판단했다면, hashCode는 똑같은 값을 반환해야한다
      - 논리적으로 같은 객체이면(equals가 true), 같은 hashCode가 반환되어야한다!
      - equals 비교에 사용되지 않은 필드는 반드시 제외해야한다! 그래야지만, 위의 규약을 어기지 않을 수 있다!
    - equals(Object)가 두 객체를 다르다고 판단했더라도, hashCode가 항상 서로 다른값을 반환할 필요는 없다. (단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.)
  - 이상적인 해시함수는 주어진(서로 다른) 인스턴스들을 32비트 정수 범위에 균일하게 분배해야한다
  - `Objects.hash`
    - 적절한 hash 값을 반환해주지만, 성능이 아쉽다
      - 입력인수를 담기위한 배열이 만들어지고, 입력중 기본타입이 있다면 박싱과 언박싱도 거치게되기때문..
    - 불변클래스라면 캐시로 이를 해결가능
      - 인스턴스 생성시 해시코드 계산
      - 지연 초기화 전략
        ```java
        public class HashCodeLazyInitThreadSafe { // 불변클래스 전제!
            private String coreField1;
            private String coreFiled2;
            private int hashCode; // 캐싱

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                HashCodeLazyInitThreadSafe that = (HashCodeLazyInitThreadSafe) o;
                return Objects.equals(coreField1, that.coreField1) && Objects.equals(coreFiled2, that.coreFiled2);
            }

            @Override
            public int hashCode() {
                int result = hashCode; // 스레드에 안전하기위해서는 공유 가능한 멤버변수를 직접 수정하지말고 이렇게 지역변수로 새로이 만들어서 진행하자
                if (result == 0) {
                    result = coreField1.hashCode();
                    result = 31 * result + coreFiled2.hashCode();
        //            result = Objects.hash(coreField1, coreFiled2); // 위의 두줄을 이렇게 한줄로도 가능
                    
                    hashCode = result;
                }

                return result;
            }
        }
        ```
  - 기타 팁
    - HashMap은 같은 해시버킷에 있다할지라도, hashCode가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화 되어있다(해시코드는 다르지만, 같은 해시버킷에 들어갈 수 있다.)
        ```java
            public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
                
                //...

                public V get(Object key) {
                    Node<K,V> e;
                    return (e = getNode(key)) == null ? null : e.value;
                }

                final Node<K,V> getNode(Object key) {
                    Node<K,V>[] tab; Node<K,V> first, e; int n, hash; K k;
                    if ((tab = table) != null && (n = tab.length) > 0 &&
                        (first = tab[(n - 1) & (hash = hash(key))]) != null) { // 1
                        if (first.hash == hash && // always check first node   // 2
                            ((k = first.key) == key || (key != null && key.equals(k))))
                            return first;
                        if ((e = first.next) != null) {
                            if (first instanceof TreeNode)
                                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                            do {
                                if (e.hash == hash &&                          // 3
                                    ((k = e.key) == key || (key != null && key.equals(k))))
                                    return e;
                            } while ((e = e.next) != null);
                        }
                    }
                    return null;
                }

                //...
            }

            1. Node<K,V>[] 라는 배열(해시테이블)의 각 요소들은 해시 버킷이다. tab[(n - 1) & (hash = hash(key))] 을 통해서 해시 버킷을 찾게된다. 즉, "(n - 1) & (hash = hash(key))" 요게 해시 버킷을 찾는 로직이다. Node<K,V> 는 LinkedList 혹은 트리구조로 구성되어있는데, 이를 통해서 해시버킷이 같을때 해시코드가 같은 대상을 찾게 된다.

            2, 3. 모두 "== hash" 를 통해서 전달받은 인스턴스의 hash 값과 같은지 확인하고있다. 즉, 해시코드가 다른 엔트리끼리는 뒤의 동치성 비교(equals)도 시도하지않게 된다. 
        ```
    - equals나 hashCode계산이 많이 복잡해질것 같은 필드들은, 필드의 표준형을 만들어서 사용하자!
    - 참조 타입 필드의 값이 null 이면 hashCode는 0을 사용하자 (관례)
    - 필드가 배열인데, 핵심원소가 하나도 없다면 단순히 상수 0 을 사용하자
      - 모두가 핵심원소이면 Arrays.hashCode를 사용하자
    - 성능을 높인답시고 해시코드를 계산할때 핵심필드를 생략해서는 안된다! 해시 품질이 나빠져 해시테이블의 성능을 심각하게 떨어뜨릴수 있다
    - hashCode가 반환하는 값의 생성규칙을 API 사용자에게 구체적으로 공표하지말자
      - 추후에 계산방식 바꾸기 어려울수있다(더 좋은 해시알고리즘이 생겼음에도불구하고..)

---

- 아이템12_toString을 항상 재정의하라
  - Object의 기본 toString 메서드는 `클래스_이름@16진수로_표시한_해시코드` 이다
  - 이는 ***유익한 정보를 제공***하기 어렵기때문에 재정의가 필요할뿐만아니라, 모든 하위클래스에서 이 메서드를 재정의하라고 규약에 명시되어있다
  - 실전에서 toString은 그 객체가 가진 주요 정보 모두를 반환하는것이 좋다. 하지만, 객체의 모든 상태를 문자열로 담기에 적절하지않다면, 요약정보를 담아주는게 좋다
  - toString을 활용해서 포맷을 명시하였다면, 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공해주는게 좋다
  - toString에 포맷이 있던없던 toString에 특별한 의도가 있다면, 주석으로 명확하게 전달해주자!
  - 포맷 여부와 상관없이, toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공해주자
    - 프로그래머들이 toString의 값을 직접 파싱해서 작업하도록하면, 추후 toString의 내용이 변했을때(변할수 있다고 명시했을지라도..) 갑작스런 에러를 경험할 수 있다.
  - 상위 클래스에서 이미 알맞게 재정의한 경우는 재정의 놉

---

- 아이템13_clone 재정의는 주의해서 진행하라
  - `Cloneable` 인터페이스는 약간특이함
    - Cloneable 인터페이스에서 clone 메서드를 제공하지않는다(Object의 protected로 clone메서드가 있다)
    - Cloneable 인터페이스를 구현하지않은 클래스에서 clone을 호출하게되면, CloneNotSupportException을 던진다 (Cloneable을 구현하였다면 해당 객체의 필드들을 복사해준다)
      - 즉, Cloneable 인터페이스가 Object의 clone 동작방식을 결정한다..
    - 이런 구현방식은 정상적인 방식이 아닌 예외적인부분이기에 따라해서는 안된다 
  - clone 주의사항
    - 모든필드가 기본타입이거나 불변객체이면 완벽하게 복제가능
    - 필드가 가변객체라면(ex. 배열이나 컬렉션..) 동일한 메모리 주소를 바라보기때문에, 참조를 끊어줄수있는 별도의 copy가 필요함 (즉, 복제본이 가변객체를 변경시켰을때 원본도 변경..)
      - 물론, 가변객체이나 공유해도 상관없다면 문제없음
      - 복제할 수 있는 클래스를 만들기 위해서는 필드들이 final로 선언되어있으면 안되는데(해당 필ㄷ컴파일 에러..), 이는 가변 객체를 참조하는 필드는 final로 선언하라는 일반 용법과도 충돌한다.. (타협이 필요..)
    - 단순히 컨테이너만 관계를 끊는것이 아닌, 컨테이너 내부의 객체들도 연관성이 없어야한다면 각 객체별 *깊은 복사*가 필요
      
        
      