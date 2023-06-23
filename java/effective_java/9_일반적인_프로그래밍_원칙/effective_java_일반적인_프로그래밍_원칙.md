effective_java_일반적인_프로그래밍_원칙

- 아이템57_지역변수의 범위를 최소화하라
  - 지역변수의 유효범위를 최소로 줄이면, 코드 가독성과 유지보수성이 높아지고 오류가능성은 낮아진다!
  - 어떻게 지역변수의 범위를 줄일 수 있을까?
    - 가장 처음 쓰일때 선언하기!
      - 굳이 미리 선언해서 언제써야할지를 까먹게되는.. 즉, 코드가독성을 잃어버리지말자..
      - 또한, 실제로 사용하는곳의 블록 바깥에 앞서서 선언하게되면, 실수로 블록이 끝난 이후에 변수를 사용해서 망할수도..
      - 그리고 선언과 동시에 초기화하자
        - 초기화에 필요한 정보가 충분하지않다면 선언을 미뤄야한다
        - 하지만, try-catch는 예외~
          - 변수를 초기화하는 표현식에서 검사 예외를 던질 가능성이 있다면 try 블록 안에서 초기화
            - 그리고 이후에 try블록 나와서 계속해서 작업수행하면되겠지..
          - 변수 값을 try블록 바깥에서 사용해야한다면, try 블록 앞에서 선언해야한다..
    - 메서드를 작게 유지하고, 한가지 기능에 집중하도록 하는것!
      - 한 메서드에 여러기능이 있다면, 기능마다 필요한 지역변수가 혼재할 수 밖에 없으니.. 쪼개자!!
  - 반복문(전통적인for, for-each)은 변수 범위를 최소화해줄 수 있음
    - 반복 변수가 루프 안에서만 사용되고 쓸일이 없다면, for문을써라! (while쓸 이유가없음..)
    - 기본적인 컬렉션 순회 관용구
      ```java
        for (Element e : collection) {

        }
      ```
    - 반복자가 필요할때의 관용구 (ex. 반복자의 remove 써야할경우)
      ```java
        for(Iterator<Element> i = collection.iterator(); i.hasNext(); ) {
            i.remove();
        }

        ////////////////////////////////
        // 이걸 while문으로 쓰게되면
        Iterator<Element> iterator = collection.iterator();
        while(iterator.hasNext()) {
            doSomething(iterator.next()); // 사실 iterator는 여기 block외에서 쓸데가 없음..
        }

        Iterator<Element> iterator2 = collection2.iterator();
        while(iterator.hasNext()) { // 범위를 최소화하지않으니 이런 실수를 유발하여.. iterator2의 원소를 못가져오게됨.. (물론 컴파일 에러도 안나니깐 더 큰 문제..)
            doSomething(iterator2.next());
        }

      ```
    - for의 한계값을 별도로 구해야하고, 비용이 많이 든다면 아래와 같은 관용구 사용가능
      ```java   
        for (int i = 0, n = expensiveComputation(); i < n; i++) { // 이렇게 사용하면 n은 i의 한계값이기때문에 블럭 밖에서 쓸 필요도 없고, 그렇다고 계속 블록 안에서 n을 계산해서 확인할 필요도 없다 
            // ...
        }
      ```

---

- 아이템58_전통적인 for문보다는 for-each문을 사용하라
  - for-each문의 정식이름은 "향상된 for문(enhanced for statement)"
  - 하나의 관용구로 컬렉션(**정확하게는 Iterable인터페이스를 구현한 구현체**)과 배열 모두 처리
    - Iterable 구현체들은 컴파일러가 while문과 Iterator를 호출하여 만들어줌
    - 배열은 컴파일러가 전통적인 for문으로 만들어줌
    - 증명한 코드는 "아이템47" 참고 
    - => for-each문이 만들어내는 코드가 결국 사람이 손으로 최적화한것과 같기때문에 성능에 차이가 없다..
  - for-each를 사용하기 어려운 상황
    - 파괴적인 필터링(destructive filtering)
      - 그냥.. 루프돌면서 원소 지워야하면 이거 못쓴다
        - 반복자의 remove를 호출해야함..
        - 자바 8부터는 컬렉션에 removeIf메서드 쓰는게 좋음
          - removeIf 내부에 알아서 루프돌아서 제거해줌
          ```java
            // Collection 내부
            default boolean removeIf(Predicate<? super E> filter) {
                Objects.requireNonNull(filter);
                boolean removed = false;
                final Iterator<E> each = iterator();
                while (each.hasNext()) {
                    if (filter.test(each.next())) {
                        each.remove();
                        removed = true;
                    }
                }
                return removed;
            }
          ```
    - 변형(transforming)
      - 리스트나 배열을 순회하면서 해당 자리에 그 원소의 값 일부 혹은 전체를 교체해야한다면, 반복자나 배열의 인덱스를 사용해야한다.
        - 물론, 불변객체가 아니라면, 루프돌면서 해당 원소 내부의 값을 변경할수는 있겠지만.. 불변객체인 상태에서 해당 원소를 변경해야한다면 인덱스에 맞추어 다시 할당해주어야하니깐..
    - 병렬반복(parallel iteration)
      - 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해서 엄격하고 명시적으로 제어해야한다
        - 보통 중첩된 for문을 쓸때 안쪽에 있는 루프를 다 돌면 다시 바깥 루프의 다음 원소로 가게되는 일련의 순차적인 과정이 있는데, 바깥루프와 안쪽루프를 순차적인 과정으로 도는게아닌 왔다갔다해야하는 즉, 병렬로 처리해야하는 경우가 있을때를 이야기하는듯함.. 
    - 원소들의 묶음을 표현하는 타입을 작성해야한다면 Iterable 구현을 고민해보자~ 그래서 이를 사용하는 사용자가 for-each를 사용할수있도록 해주자~

---

- 아이템59_라이브러리를 익히고 사용하라
  - 표준라이브러리 사용이점
    - 코드를 작성한 전문가의 지식과 나보다 앞서 사용한 다른 프로그래머들의 경험활용 가능
    - 일과 크게 관련 없는 문제를 해결하느라 시간허비않아도됨
      - 수학적인거라든지, 복잡한 알고리즘 만드는걸로 시간을 쓰기보다 애플리케이션 기능개발에 초점 가능
    - 따로 노력하지 않아도 성능이 지속해서 개선됨
      - 계속해서 벤치마크하여 개선..
    - 개발자 커뮤니티에서 이야기 나온것들을 논의하여 필요한 기능들이 계속 추가됨
    - 이를 활용하면 당연히 많은 사람이 쓰고있기때문에, 낯익은 코드가 되어 유지보수나 읽기 쉬운 코드가 됨
  - 필요한 기능이 있다면 
    - 일단 표준 라이브러리에 해당 기능이 있는지 보고
    - 없으면 좋은 서드파티 라이브러리가 있는지 보고
    - 이 또한 없으면 개발하라
  - 기타 팁
    - 제공해주는 기능이 있는지 몰라서 직접 개발하는 경우가 있기에, 메이저 릴리즈마다 주목할만한 기능들을 확인해보자!
      - [java10 릴리즈노트의 신규기능](https://www.oracle.com/java/technologies/javase/10-relnote-issues.html#NewFeature)
      - java.lang, java.util, java.io 는 잘 숙지하자!

