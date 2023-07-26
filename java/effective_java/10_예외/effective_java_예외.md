effective_java_예외

- 아이템69_예외는 진짜 예외 상황에만 사용하라
  - 예외를 단순히 일상적인 제어 흐름용으로 쓰면안된다..
    - ex. loop 빠져나오는 방법으로 별도의 검사없이, ArrayIndexOutOfBoundsException 예외처리를 사용하는경우..
  - 특정 상태에서만 호출할수 있는 "상태 의존적" 메서드를 제공하는 클래스는 "상태 검사" 메서드도 함께 제공해야한다
    - ex. Iterator.next()는 상태의존적 메서드이며, 그렇기에 Iterator.hasNext() 라는 상태 검사 메서드를 제공해준다
      - 그래서 아래와 같은 관용구 사용가능
        ```java
            for (Iterator<Foo> i = collection.iterator(); i.hasNext(); ) {
                Foo foo = i.next();
            }
        ```
  - 상태검사 메서드 대신 빈 옵셔널(Optional)이나 null 같은 특수한 값을 반환하기도한다
    - 언제쓰는게 좋을까?
      - 외부 동기화 없이 여러 스레드가 동시에 접근할 수 있거나 외부요인으로 상태가 변할 수 있다면, 옵셔널이나 특정 값을 사용
        - 상태 검사 메서드와 상태 의존적 메서드는 스레드에 안전하지않다! (상태 검사 메서드 호출하고 상태 의존적 메서드 호출하는 사이에 객체 변경가능..)
      - 성능이 중요한 상황에서 상태 검사 메서드가 상태 의존적 메서드의 작업 일부를 중복 수행한다면, 옵셔널이나 특정값을 사용하자
      - 다른 모든 경우엔 상태 검사 메서드 방식을 사용하자
        - 가독성도 좋고, 잘못사용시에 발견하기가 좀 더 수월하다
          - 상태검사 메서드 호출 못했을 경우, 금방 상태 의존적 메서드에서 에러를 뱉기에..
        - 특정값은 검사하지 않고 지나쳐도 발견하기가 어렵다 (옵셔널은 필수적으로 처리하도록 하므로 특정값만 해당)
          - 해당 값을 체크하도록 강제하지 않으니.. 당연
  - 기타 팁
    - 잘 설계된 API라면, 클라이언트가 정상적인 제어 흐름에서 예외를 사용할 일이 없어야한다


---

- 아이템70_복구할 수 있는 상황에서는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라
  - 자바의 문제 상황을 알리는 타입(Throwable)
    - 검사예외
      - 호출하는 쪽에서 복구하리라 여겨지는 상황이라 검사예외 사용
        - 비검사예외와 검사예외를 구분하는 기본 규칙
      - API 설계자 관점에서 검사예외를 사용했다는것은 호출자에게 해당 예외가 발생시 다시 회복해내라고 요구한 것
        - 예외를 잡고 별다른 조치를 취하지 않을수 있긴하나, 이 관점에서 그닥 좋지않은 생각
    - 런타임예외
      - 비검사 throwable
        - 복구 불가능 or 더 실행해봐야 득보다 실이 많다는 뜻
        - 그래서 해당 에러는 잡을 필요가 없거나 통상적으로 잡지 말아야한다는것
      - 전제조건 위배로 인한 프로그래밍 오류를 나타낼때 사용
        - 전제조건 위배는 클라이언트가 해당 API의 명세에 기록한 제약을 안지킨것!
          - ex. 배열의 인덱스가 0 ~ 배열크기-1 인데(전제조건), 이 외의 인덱스를 요청시에 ArrayIndexOutOfBoundsException이 발생
      - 검사예외를 던져야할지 런타임예외를 던져야 할지 확신이 안선다면, 일단 런타임예외로 가자
        - 과하게 사용한 검사예외가 있으면, 쓰기 불편한 API가 된다(ex. 스트림 사용못함..)
    - 에러
      - 비검사 throwable
        - 위와 동일
      - JVM의 자원부족, 불변식 깨짐 등 더 이상 수행을 계속할 수 없는 상황을 나타낼때 사용
      - Error 클래스를 상속해 하위 클래스를 만드는일은 없도록!
        - 우리가 만드는 비검사예외는 RuntimeException의 하위 클래스여야한다!
      - Error는 상속하지말아야할뿐아니라, 던져서도 안된다
        - 하지만 AssertionError는 예외! (근데 보통 그냥 `assert`를 쓸거같은데 AssertionError를 직접 던질일이 있으려나..) 
  - 예외 또한 완벽한 객체이기에, 예외를 일으킨 상황에 관한 정보를 코드 형태로 전달하는데 있어서 **예외의 메서드를 잘 활용**하자!
    - **검사예외는 일반적으로 복구할 수 있는 조건일때 발생하기에, 호출자가 예외상황에서 벗어나는데 필요한 정보를 알려주는 메서드를 함께 제공하자!**
      - ex. 카드 잔고 부족하여 checked exception이 발생하였다면, 잔고가 얼마나 부족한지 알려주는 접근자 메서드를 제공해주자
        ```java
          public class SQLException extends java.lang.Exception
                          implements Iterable<Throwable> {
                // ...
                public String getSQLState() {
                    return (SQLState);
                }

                public int getErrorCode() {
                    return (vendorCode);
                }
                // ...
          }
        ```


---

- 아이템71_필요없는 검사 예외 사용은 피하라
  - 검사예외는 발생한 문제를 프로그래머가 처리하여 안정성을 높이게끔 해준다
  - 그러나, 검사예외를 과하게 사용하면 오히려 쓰기 불편한 API가 될 수 있다
    - 사용자는 catch블록을 두어 그 예외를 붙잡아 처리하거나 더 바깥으로 던져야함 (바깥으로 던지면 또 그 예외를 처리해주어야함)
    - 스트림에서 사용불가
  - 언제쓰는게 좋나?
    - API를 제대로 사용해도 발생할 수 있는 예외
      - ex. 잔고부족..
    - 프로그래머가 의미 있는 조치를 취할 수 있는 경우
    - 이 외에는 비검사 예외로!
      - 특히 단 하나의 검사예외를 던지는 경우라면, 최대한 검사 예외를 안던지는 방법이 없는지 고민해보자
        - 검사예외를 "추가"하는건 catch문 하나에 더하면되지만.. 검사예외를 최초 만들게되면, 위의 불편사항을 이제 떠안아야한다
  - 검사예외를 어떻게 피할 수 있을까?
    - 옵셔널을 반환 (빈옵셔널 반환)
      - 하지만, 예외에 대한 부가정보를 담기 어렵기때문에 부가정보를 담아야하는 상황이라면 검사예외로..
    - 검사예외를 던지는 메서드를 2개로 쪼개 비검사 예외로 변경
      ```java
        // 검사예외 
        try {
            obj.action(args);
        } catch (TheCheckedException e) {
            // ...
        }

        // 2개로 쪼개기 + runtime으로 변경
        if (obj.actionPermitted(args)) { // 상태검사 메서드(아이템70참고)를 사용하는것과 동일. 그렇기에, 동기화에 대한 이슈나, 중복수행이 잇을때의 성능이슈에 대한 트레이드 오프가 있을 수 있다
            obj.actions(args);
        } else {
            // ...
        }

        // 한걸음더 나아가서, 프로그래머가 이 메서드가 성공하리라는 걸 안다거나, 실패 시 스레드를 중단하길 원한다면 그냥 한줄로도 가능
        obj.actions(args);

      ```
  - 결론
    - API가 복구할 방법이 없다면 비검사예외를 던지고
    - 복구할 수 있다면 그리고 호출자가 처리해야하는거라면 일단 옵셔널 가능한지 확인
    - 옵셔널만으로 상황을 처리하기에 충분한 정보제공이 어렵다면 검사예외

---

- 아이템72_표준 예외를 사용하라
  - 표준예외를 재사용하면 얻는 이점
    - 우리의 API가 다른 사람이 익히고 사용하기 쉬워짐
    - 예외 클래스 수가 적을 수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게걸림 (이건.. 예외 클래스를 엄청 많이 만들지않는 이상 크게 무리올지..의문..)
  - 대표적인 표준 예외 클래스
    - IllegalArgumentException
      - 호출자가 인수로 부적절한 값을 넘길때 던지는 예외
      - 예외적으로, null값을 허용하지않는 메서드에 null을 건내면 NullPointerException을 던짐
        - 비슷하게, 시퀀스의 허용 범위는 넘는 값을 던질때에도 IndexOutOfBoundsException
    - IllegalStateException
      - 객체가 메서드를 수행하기에 적절하지 않은 상태
      - ex. 아직 초기화 안된 객체를 사용하려할때
    - ConcurrentModificationException
      - 허용하지 않는 동시 수정이 발견됐을때
      - 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수정하려한다는 **추정**일때 던짐
        - 동시성 검출에 대한 완벽한 방법이 없음..
      - ex. ArrayList의 Iterator.next()에서 사용
        ```java
          // ArrayList 내부

          private class Itr implements Iterator<E> {
          //...

          @SuppressWarnings("unchecked")
          public E next() {
              checkForComodification();
              int i = cursor;
              if (i >= size)
                  throw new NoSuchElementException();
              Object[] elementData = ArrayList.this.elementData;
              if (i >= elementData.length)
                  throw new ConcurrentModificationException();
              cursor = i + 1;
              return (E) elementData[lastRet = i];
          }

          //...
        ```
    - UnsupportedOperationException
      - 호출한 메서드를 지원하지않을때
      - ex. 원소를 넣을 수 만있는 List 구현체에 remove 호출할때
    - NullPointerException
      - null을 허용하지 않는 메서드에 null 건낼때
    - IndexOutOfBoundException
      - 인덱스가 범위를 넘어섰을때
  - 표준예외를 사용할때는 **API문서를 참고**해 예외가 어떤 상황에서 던져지는 꼭 확인하자!!!
    - 이름뿐아니라 맥락도 부합해야한다!
  - 표준예외를 주요 쓰이는 상황이 상호 배타적이지 않다. 그래서 어떤 예외를 선택하기가 어려울때가 있다.
    - ex. IllegalStateException vs IllegalArgumentException
      - 인수값이 무엇이었든 이미 객체의 상태로 인해서 실패했을거라면 IllegalStateException
      - 그렇지않으면 IllegalArgumentException
    - 자바 표준 api에서 어떻게 사용하는지 살펴봐도좋을듯
    
       