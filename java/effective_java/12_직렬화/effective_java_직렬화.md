effective_java_직렬화

- 아이템85_자바 직렬화의 대안을 찾으라
  - 직렬화는 프로그래머가 어렵지 않게 분산 객체를 만들수 있다는 구호로 인기가 있었으나, 아래와 같은 문제도 수반되었다
    - 보이지 않는 생성자
    - API와 구현사이의 모호해진 경계
    - 잠재적인 정확성 문제
    - 성능
    - 보안
    - 유지보수성
  - ObjectInputStream.readObject
    - readObject 메서드는 Serializable을 구현한 클래스라면 클래스패스 안에 있는 모든 타입의 객체를 만들어 낼 수 있다
    - 역직렬화 과정에서 타입들 안의 모든 코드를 수행할수 있기에, 코드 전체가 공격 범위에 들어 갈 수 있게된다
    - 바이트 스트림을 역직렬화할때는 매우 주의가필요!!
  - 역직렬화 관련 대표 문제
    - 역직렬화 폭탄(deserialization bomb) 받게되면, 이를 처리하느라 다른 요청 처리못함 (서비스 거부 공격)
  - 어떻게 직렬화 위험 피할 수 있나?
    - 역직렬화를 하지 않는것..
    - 자바 직렬화를 쓰는것 대신, 객체와 바이트 시퀀스를 변환해주는 다른 메커니즘이 많이있다! (크로스-플랫폼 구조화된 데이터 표현(cross-platform structured-data representation))
      - 자바 직렬화보다 훨씬 간단
      - 속성-값 쌍의 집합으로 구성된 간단하고 구조화된 데이터 객체를 사용
      - ex. JSON, 프로토콜 버퍼(구글)
        - 효율엔 프로토콜 버퍼가 더 좋다함! 써야할일 있으면 나중에 꼭 찾아보자~
  - 레거시 때문에 자바 직렬화 완전배제 어려우면 어떻게?
    - 신뢰할 수 없는 데이터트 절대 역직렬화하지 말자
      - 객체 역직렬화 필터링(java.io.ObjectIinputFilter - 자바9부터)
  - 결론
    - 어떻게든 자바 직렬화 사용보단, JSON이나 프로토콜 버퍼와 같은 크로스-플랫폼 구조화된 데이터 표현으로 마이그레이션하자..


---

- 아이템86_Serializable을 구현할지는 신중히 결정하라
  - Serializable을 구현하면, 릴리즈한 뒤에는 수정하기 어려움
    - 직렬화된 바이트 스트림 인코딩(직렬화 형태)도 하나의 공개 API가 된다..
    - 커스텀 직렬화를 사용하지않고, 그냥 자바의 기본 방식을 사용하면, 그걸 도입해서 배포한 순간의 클래스의 내부구현방식에 묶여버린다.. 영원히..
    - private, package-private 예외없음..
    - 배포이후 내부 클래스 개선하고자할때, 아무생각없이 내부 편의 메서드 하나를 추가하게되면 직렬화(혹은 역직렬화)시 기존과 달라지게되어 예외가 떨어진다
      - ex. serialVersionUID를 직접 명시하지않으면, 시스템이 런타임에 암호 해시함수를 적용해 자동으로 클래스 안에 생성해넣는데, 이게 메서드 추가에 따라 달라지게됨
        - 달라지면 직렬화과정에서 예외
  - 버그와 보안 구멍이 생길 위험이 높아진다
    - 역직렬화시 객체생성을 수행하게되면, 생성자를 통해서 생성을 하게될텐데, 이게 생성자를 호출하는 로직이 눈에 보이지않기때문에 생성자에서 지켜야할 불변식같은 부분을 놓칠 수 있다
  - 신버전 릴리즈시에 테스트할 것이 늘어난다
    - 직렬화 가능 클래스가 수정되면 신버전 인스턴스를 직렬화 한 뒤에 구버전으로 역직렬화할 수 있는지, 그리고 그 반대도 가능한지 검사해야한다. 그에 따라 테스트 갯수는 증가
  - 상속용으로 설계된 클래스는 대부분 Serializable을 구현하면 안되며, 인터페이스도 대부분 Serializable을 확장해서는 안된다
    - 이를 사용하는 클라이언트가 직렬화를 신경써야하므로.. 매우부담
    - 물론, Serializable을 구현한 클래스만 지원하는 프레임워크면 어쩔수없음
  - 내부 클래스는 직렬화를 구현하지 말아야한다
    - 내부 클래스에는 바깥 인스턴스의 참조와 유효범위 안의 지역변수 값들을 저장하기 위해 컴파일러가 생성한 필드들이 자동으로 추가되어있는데, 이 필드들이 명세에도 어떻게 정의되어있는지 나와있지않다. 즉, 직렬화형태가 불분명.. 그래서 사용하면안됨!!
    - 정적 멤버 클래스는 Serializable을 구현해도 됨
  - 클래스의 인스턴스 필드가 직렬화와 확장이 모두 가능하다면 주의할 점
    - 불변식 보장이 필요한 필드가 있다면, finalize 메서드를 final로 선언하여 하위클래스에서 상속못하도록 해야한다 (finalizer 공격 막기위함)
    - 인스턴스 필드 중 기본값(정수형 0, boolean은 false, 객체 참조타입은 null)으로 초기화되면 위배되는 불변식이 있다면 클래스에 readObjectNoData 메서드를 추가해야한다
      - <span style="color:red">readObjectNoData 메서드를 넣으면, 초기화시에 기본값으로 초기화되면 알아서 호출되는건가..?</span>
      ```java
        private void readObjectNoData() throws InvalidObjectException {
            throw new InvalidObjectException("스트림 데이터가 필요합니다");
        }
      ```
    - 상속용 클래스인데, 직렬화 지원안하면..
      - 상위 클래스에 매개변수가 없는 생성자를 제공하지않는다면, 직렬화 프록시 패턴을 사용
- 기타 팁
  - 역사적으로 BigInteger와 Instant 같은 '값' 클래스와 컬렉션 클래스들은 Serializable을 구현하고, 스레드 풀처럼 '동작'하는 객체를 표현하는 클래스들은 대부분 Serializable을 구현하지 않았다

---

- 아이템87_커스텀 직렬화 형태를 고려해보라


---

- 아이템88_readObject 메서드는 방어적으로 작성하라
  - readObject 메서드는 실질적으로 또 다른 public 생성자와 같으므로, 생성자와 똑같은 수준으로 주의가 필요 (커스텀하게 만들어줘야한다!)
    - 인수가 유효한지 검사
      - 유효하지않으면 InvalidObjectException을 던져라
    - 매개변수를 방어적으로 복사
      - 가변요소일 경우
    - readObject 메서드를 구현할때, 생성자처럼 재정의 가능 메서드를 호출해서는 안된다
      - 생성자에서 재정의 가능 메서드를 호출하면, 아직 만들어지지않은 하위클래스에서 재정의한 메서드가 실행되어 문제를 유발시킬 수 있다
  - 그럼 언제 기본 readObject(단순 `implements Serializable` 만 붙은것)를 써도되나?
    - "transient 필드를 제외한 모든 필드의 값을 매개변수로 받아 유효성 검사 없이 필드에 대입하는 Public 생성자를 만들수 있는가?" 라는 질문을 해보고, "그렇다" 할때만 기본 readObject 사용


---

- 아이템89_인스턴스 수를 통제해야 한다면 readResolve보다는 열거 타입을 사용하라
  - readResolve 기능을 이용하여, readObject가 만들어낸 인스턴스를 다른것으로 대체할 수 있는데, 이를 통해서 직렬화사용시 싱글톤을 유지할 수 있따
    - readObject를 호출하여 인스턴스를 생성하게되면, 이는 항상 새로운 인스턴스이며, 기존에 있는것과는 항상다르다. 하지만, readResolve를 사용하면 인스턴스를 대체할 수 잇으므로 싱글톤을 유지할 수 있다. (새로 만들어진 인스턴스는 참조가 없으므로 GC가 알아서 수거해간다)
  - readResolve를 인스턴스 통제 목적으로 사용(ex. 싱글톤유지)한다면, 객체 참조 타입 인스턴스 필드는 모두 transient로 선언해야한다
  - 이보다 좋은것은 원소하나짜리 열거타입으로 바꾸자!
    ```java
        public enum Elvis {
            INSTANCE;
            private String[] favoirtSongs = {"Hound Dog", "Heartbreak Hotel"};

            public void printFavoirtes() {
                System.out.println(Arrays.toString(favoriteSongs));
            }
        }
    ```
  - 인스턴스 통제해야하는데, 어떤 인스턴스를 통제해야할지 컴파일 타임에 알 수 없고, 런타임에만 알수 있다면 열거타입이 불가능하기때문에 readResolve를 써야한다

---

- 아이템90_직렬화된 인스턴스 대신 직렬화 프록시 사용을 검토하라
  - 직렬화 프록시 패턴
    - 바깥 클래스의 논리적 상태를 정밀하게 표현할 수 있는 중첩 클래스를 설계해 private static으로 선언
    - 중첩클래스의 생성자는 오직 하나, 매개변수로는 바깥클래스를 받아야한다
    - 생성자에서는 인수로 넘어온 인스턴스의 데이터를 복사하는 일을 수행
      - 일관성 검사나 방어적 복사도 필요없음
    - 바깥 클래스와 중첩 클래스 모두 Serializable 구현
    - 바깥 클래스에 writeReplace 메서드 추가
      - 이 메서드는 자바의 직렬화 시스템이 바깥 클래스의 인스턴스 대신 SerializationProxy의 인스턴스를 반환하게 하는 역할
      - 즉, 직렬화가 이뤄지기 전에 바깥 클래스의 인스턴스를 직렬화 프록시로 변환
    - 바깥 클래스에 readObject 사용못하도록 정의
    - 바깥 클래스와 논리적으로 동일한 인스턴스를 반환하는 readResolve 메서드를 SerializationProxy 클래스(중첩클래스)에 추가
      - 이 메서드는 역직렬화시에 직렬화 시스템이 직렬화 프록시를 다시 바깥 클래스의 인스턴스로 변환하게 해줌
      - 이를 활용하여, 일반 인스턴스를 만들때와 똑같은 생성자, 정적 팩터리 혹은 다른 메서들르 사용해 역직렬화된 인스턴스를 생성가능
    - 추가이점
      - 역직렬화한 인스턴스와 원래의 직렬화된 인스턴스의 클래스가 달라도 정상 작동
    
    
  - 관련 내용 코드예시
    - EnumSet
    ```java
    public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, java.io.Serializable {
    
        // ...

        private static class SerializationProxy<E extends Enum<E>> implements java.io.Serializable
        {

            private static final Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum<?>[0];
            private final Class<E> elementType;
            private final Enum<?>[] elements;

            SerializationProxy(EnumSet<E> set) {
                elementType = set.elementType;
                elements = set.toArray(ZERO_LENGTH_ENUM_ARRAY);
            }


            @SuppressWarnings("unchecked")
            private Object readResolve() { // 바깥 클래스와 논리적으로 동일한 인스턴스를 반환하는 readResolve 메서드를 SerializationProxy 클래스(중첩클래스)에 추가
                EnumSet<E> result = EnumSet.noneOf(elementType);
                for (Enum<?> e : elements)
                    result.add((E)e);
                return result;
            }

            private static final long serialVersionUID = 362491234563181265L;
        }

        Object writeReplace() { // 바깥 클래스에 writeReplace 메서드 추가
            return new SerializationProxy<>(this);
        }

        private void readObject(java.io.ObjectInputStream s) throws java.io.InvalidObjectException {
            throw new java.io.InvalidObjectException("Proxy required"); // 바깥 클래스에 readObject 사용못하도록 정의
        }
    }
    ```

  - 직렬화 프록시 패턴의 한계
    - 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용 불가
    - 객체 클래프에 순환이 있는 클래스 적용 불가
      - 이런 객체의 메서드를 직렬화 프록시의 readReslove 안에서 호출하려하면 ClassCastException이 발생. 직렬화 프록시만 가졌을뿐 실제 객체는 아직 만들어진 것이 아니기 떄문
        - <span style="color:red">뭔소리??</span>
    - 좀 느리다..
    