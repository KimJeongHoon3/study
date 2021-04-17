- String 정리
  - 객체.. 그러나 객체는 기본적으로 변경이 가능하나, String은 원시타입같은 느낌으로 움직임..
    - *원시타입 : int, long, float 등의 타입을 이야기함. 힙영역에 저장되는것이 아닌, 스택영역에 저장되기때문에 주소값전달이 불가하다. (객체가 생성되면 힙영역의 주소값을 변수(스택)에 저장함)
      - *원시타입은 기본적으로 참조타입보다 접근속도가 빠르고 메모리도 적게먹는다..!

  - String은 연산( ex. "a"+"b" )이 많이 사용되면 효율적이지못하다.. 왜냐하면 연산이 수행될때마다 spring constant pool에 계속 생성되기때문이다. 그래서 StringBuilder(스레드안전x)나 StringBuffer(스레드안전o) 를 사용하여 연산을 수행하는것이 좋다.   
    - 단순성능 : StringBuilder > StringBuffer >>> String
  - new 키워드 사용 vs 그냥 리터럴
    - new 키워드 사용 : 힙영역에 객체를 생성하고(char[]값을 보면 주소가 다 다른것을 볼수있음..즉 새로생성.. 이로인해서 같은 내용으로 new로 만든것을 == 연산자로 비교하면 false뜬다!) value는 constant pool에 있는값을 참조한다
    - 리터럴 문자 사용 : constant pool에 객체를 저장하고, 이를 참조한다. 문자열이 계속 생성되면 계속해서 constant pool에 저장되는것이다.. 한번 생성하면 변하지않고 새로만든다!(immutable!!)
      - 리터를은 new String("").intern() 과 같음 
        - intern 메소드는 String이 상수풀(Constant Pool)에 등록된 경우 해당 스트링의 주소값을 반환하는 역할을 함.
        - string constant pool에 문자열을 저장하고 동일한 문자열이 저장되어있으면, 해당 주소를 반환해주고 만약 없으면, 새로 추가함 (같은 문자열을 계속 생성하여 메모리 낭비하는것을 줄여줌!)
	  - char[] 확인해보면 항상 같은 char배열의 주소를 바라보고있음!!
	  
- 당연한걸 헷갈리고있었다.. 일반적으로 객체를 메소드의 파라미터로넘기면 주소값이 동일하니깐 해당 주소값의 내부에있는 데이터들을 변경하였을 경우 이를 꼭 리턴하지않아도 적용이 잘되는데, immutalbe한 String에서는 메소드의 파리미터로 넘기면 주소를 전달해주기는 하나, 메소드 내부에서 데이터를 바꾼다한들 메소드를 호출한 부분에서는 주소값이 달라졌으니 알수있는 방법이없다.. 그래서 변경된 String값을 리턴해야만한다.. 어찌보면 프리미티브 타입과 같은 효과를 내는게 매우 상식적인듯하다..

- 그럼 왜 불변(immutable)일까?
  1. String 객체의 캐싱 기능 : 만약 new String("")과 같이 힙영역에 생성해서 접근하는방법은 메모리에 상당한 부담이된다.. 특히나 String은 너무너무너무 많이쓴다.. 그래서 이를 효율적으로 처리하기위해서 Perm 영역의 string constant pool에 생성하고(리터럴로 생성 또는 new String("").intern()) 이를 참조한다. string constant pool에 저장이되면 해당 pool에 처음들어온 string이라면 만들고 아니라면 재사용할수있도록 해준다.. 그렇게 캐싱이 가능하다! 참고로 perm 영역은 자바1.7부터는 heap영역에 배치가되어서 가바지컬렉터의 대상이되어, OOM 을 방지해준다 (1.6버전 이하에서는 Perm 영역은 고정되어있고 Runtime 시에도 확장되지 않기때문에 intern 메소드를 자주 호출하면 OOM이 발생할 수 있다.)
  2. 보안기능 : 불변하기떄문에 참조에 대한 문자열 값을 바꿀수 없음(return해서 주지않는 이상..)
  3. 스레드 안전 : 여러 스레드가 동시에 String 객체를 참조할때, 전역변수로 선언되어있지않는 이상은 불변이기때문에 안전! 하지만 전역변수에 선언되어있는 string에 여러 스레드가 접근하여 값을 바꾸게된다면 당연히 스레드에 안전하지않다..


*리터럴 : 
  - 변수에 할당되는 값을 의미( ex. int a=10, String str="abc" )
  - 프로그램에서 직접 표현한 값
  - 소스 코드의 고정된 값을 대표하는 용어
  - 종류 : 정수, 실수, 문자, 논리, 문자열 리터럴이 있다.


- 참고사이트 
  - https://dololak.tistory.com/699
  - https://brunch.co.kr/@kd4/1 (그림과 설명 매우 굿)
  - https://deep-dive-dev.tistory.com/14 (autoUnboxing에 대한 성능 비교,, autoUnboxing쓰면 느리다!)
  - https://java119.tistory.com/41 (autoBoxing, autoUnboxing)
  - https://siyoon210.tistory.com/139 (원시타입 vs 참조타입)
  - https://ict-nroo.tistory.com/18 (쉽게 설명)
  - https://creatordev.tistory.com/81 (친절한 string 설명)
  - https://medium.com/@joongwon/string-%EC%9D%98-%EB%A9%94%EB%AA%A8%EB%A6%AC%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EC%B0%B0-57af94cbb6bc (java string의 메모리에 대한 고찰)
  - https://aljjabaegi.tistory.com/465