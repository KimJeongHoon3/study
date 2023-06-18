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
    