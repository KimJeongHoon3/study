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
    

--- 

- 아이템73_추상화 수준에 맞는 예외를 던져라
  - 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야한다
    - 이를 예외번역(exception translation)이라한다
    - 저수준예외를 바깥으로 전파하면, 내부 저수준의 구현방식이 변경되어 예외가 달라지면, 클라이언트 입장에서 기존에 잡던 예외가 아니게된다.. 즉, 런타임에 에러 유발.. (검사예외라면 컴파일타임)
  - 예외를 번역할때, 저수준 예외가 디버깅에 도움이 된다면 예외 연쇄(exception chaining)를 사용하자
    - 예외 연쇄란 문제의 근본 원인(cause)인 저수준예외를 고수준 예외에 실어보내는 방식
      - Throwable.getCause()를 통해 가져올 수 있음
    - 대부분 예외 연쇄용 생성자(생성자에 Throwable을 받고, super로 전달)가 있으나, 그게 없다면 Throwable.initCause를 통해 직접 전달 가능
      ```java
            // 연쇄용 생성자
            class HigherLevelException extends Exception {
                public HigherLevelException(Throwable cause) {
                    super(cause);
                }
            }

            // 연쇄용 생성자가 없을때 initCause를 사용하자
            // initCause는 단 한번만 호출해야하며, 던지려는 예외클래스에 원인 설정을 할 수 없는경우(생성자에 Throwable이 없다..) 사용할 수 있다
            try {
                lowLevelOp();
            } catch (LowLevelException le) {
                throw (HighLevelException) new HighLevelException().initCause(le); // Legacy constructor
            }
      ```
  - 예외번역 사용하는것은 좋은것이지만, 남용하지말고 최대한 저수준 메세드가 반드시 성공하도록 하는게 좋다
    - 상위 메서드의 매개변수 값을 아래 계층 메서드로 건네기 전에 미리 검사 (유효성 검사)
    - 상위 계층에서 그 예외를 처리해서 API 호출자에게까지 전파하지 않는것
      - 적절한 로깅 필수
      - 알람도 필수일듯
---

- 아이템74_메서드가 던지는 모든 예외를 문서화하라
  - 검사 예외는 항상 따로따로 선언하고, 각 예외가 발생하는 상황을 자바독의 @thorws 태그를 사용하여 정확히 문서화하자
  - 비검사예외도 되도록 검사예외처럼 문서화해두면 좋다
    - 잘 정비된 비검사 예외문서는 그 메서드를 성공적으로 수행하기 위한 전제조건을 알려주는것
    - 특히나, 인터페이스 메서드에서 중요
      - 인터페이스의 해당 메서드를 구현한 모든 구현체가 일관되게 동작하도록 해준다!
  - 메서드가 던질 수 있는 예외를 각각 @throws 태그로 문서화하되, 비검사 예외는 메서드 선언의 throws 목록에 넣지말자
    - <span style="color:red"> 이거 뭔소리인지.. @throws태그와 throws목록은 다른건가? @throws에는 비검사예외도 쓰이고잇음.. ex. List.get</span>
  - 기타
    - @throws와 @exception은 뭐가 다른가?
      - The tags @throws and @exception are synonyms.
      - => 같다
      - [참고](https://stackoverflow.com/questions/5510170/in-javadoc-what-is-the-difference-between-the-tags-throws-and-exception)

---

- 아이템75_예외의 상세 메시지에 실패 관련 정보를 담으라
  - 스택 추적은 예외 객체의 toString 메서드를 호출해 얻는 문자열로, 예외의 클래스 이름 뒤에 상세 메시지가 붙는 형태
  - 장애가 일어난 뒤 사후 분석을 위해 실패 순간의 상황을 정확히 포착해 예외의 상세메시지에 담아야한다
    - 실패 원인은 재현이 불가능한 장애 일때 더욱 알아내기가 어려우므로 예외의 상세메시지는 더욱 중요!
  - 상세메시지에는 뭘 담을까?
    - 발생한 예외에 관여된 모든 매개변수와 필드의 값
    - 비밀번호나 암호키 같은 정보까지 담아서는 안된다!
    - 필요데이터를 모두 담되, 장황할 필요없다!
  - 예외의 상세메시지와 최종 사용자에게 보여줄 메시지는 다르다
    - 최종 사용자에게는 친절한 안내 메시지
    - 예외 메시지는 가독성보다 담긴 내용이 더 중요!
      - 특히, 이런 메세지는 굳이 현지어로 번역도 필요없다.. (최종 사용자에게 전달해주는 메세지는 현지어 번역도 필요)
  - 상세 메시지 센스있게 만드는 코드
    ```java

        // 기존의 IndexOutOfBoundsException 
        public class IndexOutOfBoundsException extends RuntimeException {
            // ...
            public IndexOutOfBoundsException(int index) {
                super("Index out of range: " + index);
            }
        }

        // 개선된 IndexOutOfBoundsException
        public class IndexOutOfBoundsException extends RuntimeException {
            // ...
            public IndexOutOfBoundsException(int lowerBound, int upperBound, int index) { 
                super(String.format("min: %d, max: %d, index: %d", lowerBound, upperBound, index)); // 실패를 포착하기 유용한 정보가 담긴 상세 메시지를 작성 => 이렇게 세부적으로 잘 만들어 놓으면, 해당 예외를 사용하는 사용자가 반복해서 예외메세지를 남기는 수고를 덜 수 있음

                // 아래는 프로그램에서 별도로 사용할 수 있도록 실패정보를 저장 => 아래에 대한 접근자 제공해주자. 물론, 이 접근자는 검사예외일때 더 빛을 발함
                this.lowerBound = lowerBound;
                this.upperBound = upperBound;
                this.index = index;
            }
        }
    ```

---

- 아이템76_가능한 한 실패 원자적으로 만들라
  - 실패원자적(failure atomic)? 
    - 호출된 메서드가 실패하더라도, 해당 객체는 메서드 호출전 상태를 유지하는 특성!
      - 특정 객체의 메서드 호출시 문제발생하더라도, 해당 객체의 상태값이 고대로 메서드 호출이전과 같게 유지되는것..
    - 이를 사용하는 이점?
      - 작업도중 예외가 발생해도 해당 객체는 여전히 정상적으로 사용할 수 있는 상태
      - 검사예외를 던졌다면, 호출자가 오류 상태를 복구가능 (호출자가 해당 객체를 적절히 잘 조정하겠지..?)
  - 어떻게 실패원자적으로 만들 수 있나?
    - 불변 객체로 설계
      - 불변객체로 설계시 메서드가 실패하면 새로운 객체가 만들어지지는 않을 수 있으나, 기존 객체가 불안정한 상태(일부만 변경되는..)에 빠지는 일은 없다
    - 가변객체의 경우는 작업 수행에 앞서 매개변수의 유효성 검사를 수행하는것
      - 객체의 내부 상태를 변경하기전에 유효성 검사를 수행(+이에 더해 적절한 예외를 던져줌)
      - 꼭 전달받는 매개변수뿐아니라, 현재 객체의 상태값들이 유효한지도 확인
        - ex. Stack의 pop은 자신의 size를 확인하여 0이면 EmptyStackException을 던진다
          ```java
            // java11 Stack
            public synchronized E pop() {
                E       obj;
                int     len = size();

                obj = peek();
                removeElementAt(len - 1);

                return obj;
            }

            public synchronized E peek() {
                int     len = size();

                if (len == 0) // 자신의 상태값을 가져와서 검사한다
                    throw new EmptyStackException();
                return elementAt(len - 1);
            }

            public synchronized int size() {
                return elementCount; 
            }
          
          ```
    - 실패할 가능성이 있는 모든 코드를, 객체의 상태를 바꾸는 코드보다 앞에 배치 
      - 계산 수행전에 인수의 유효성 검사를 수행할 수 없을때.. 앞서 이야기한 방식에 덧붙여서 사용할 수 있는 방식
    - 객체의 임시 복사본에서 작업을 수행한 다음, 작업이 성공적으로 완료되면 원래 객체와 교체
    - 실패를 가로채는 복구 코드작성하여, 작업 전 상태로 되돌리는 방법
      - 주로 디스크기반의 durability 를 보장해야하는 자료구조에 쓰인다함
  - 실패원자성은 매우매우 권장되나, 항상 달성할 수는 없다
    - 두 스레드 동기화가 안되어서 ConcurrentModificationException 발생시 해당 객체를 여전히 쓸 수 있는 상태라고 가정하면 안됨!
    - Error는 복구할 수 없으니, AssertionError와 같은것들은 실패 원자적으로 만들려는 시도조차 필요없음
  - **메서드 명세에 기술한 예외라면, 예외가 발생시에 객체의 상태는 메서드 호출 전과 똑같이 유지되어야한다! (이게 기본 규칙)**
    - **만약 이 규칙이 지켜지지않는다면, 실패시의 객체 상태가 어떻게 될 수 있는지 API에 명시하자!**

---

- 아이템77_예외를 무시하지 말라
  - API설계자가 메서드 선언에 예외를 명시한 까닭은, 그 메서드를 사용할때 적절한 조치를 취해달라고 말하는것과 같음
  - catch 블럭에 아무것도 없다면, 예외가 존재할 이유가 없어진다
    - 화재경보를 무시하는 수준을 넘어, 그냥 꺼버린것과 같음
  - 예외를 무시해야할때?
    - ex. FileInputStream을 닫을때..(close)
      - 로그로 남기는것은 필요할듯.. (남기고 무시하는 방향으로..)
      - close 시에 예외 무시해도되는이유 (챗Gpt 답변)
        - 리소스의 정상적인 해제: close() 메서드는 자원을 정리하고 메모리 누수를 방지하는데 사용됩니다. 일반적으로 close()를 호출하면 리소스는 정상적으로 해제되어야 하며, 예외가 발생한 경우라도 자원은 정상적으로 해제됩니다.
        - 리소스 정리 중 발생하는 예외: close() 메서드에서 발생하는 예외는 리소스를 해제하는 과정에서 발생하는 것으로 예상됩니다. 이러한 예외는 주로 리소스와 관련된 하위 시스템과의 통신에서 발생하는 것으로, 코드에서 직접 제어할 수 없는 경우가 많습니다.
        - 예외 처리를 위한 자원 관리: I/O 리소스를 사용하는 코드에서 예외 처리는 중요합니다. 예외를 적절하게 처리하면 시스템이 더욱 견고하고 안정적으로 동작할 수 있습니다. 따라서 close() 메서드에서 발생한 예외를 로깅하여 추후에 디버깅 및 문제 해결에 도움이 되도록 할 수 있습니다.
      ```
    - 예외를 무시하기로했다면, catch 블록안에 그렇게 결정한 이유를 반드시 주석으로 남기고, 예외변수의 이름도 ignored로 바꿔주자!