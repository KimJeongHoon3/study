java_time관련정리

- LocalDateTime
  - 시스템시간으로 정의
  - 어떤 기준점이 없는 단순 날짜
    - 즉, Zone을 가지고있지않다 (now 메서드 호출시엔 시스템시간을 기준으로 보여주되, Zone은 따로 가지고있지않음)
- ZonedDateTime
  - Zone 기준으로 정의
  - ex. Asia/Seoul
- OffsetDateTime
  - UTC 기준 **상대적인 시간**으로 정의
- Instant
  - UTC 기준으로 정의
  - Instant를 생성하기위해서는 Zone이 반드시필요! Zone을 알아야 UTC기준으로 시간을 얼마나 빼거나 더할지를 알 수 있으니..
    - 그래서 LocalDateTime에서는 toInstant가 없음.. ZonedDateTime에만 toInstant존재
      ```java
        // ZonedDateTime 클래스 내부

        default Instant toInstant() {
            return Instant.ofEpochSecond(toEpochSecond(), toLocalTime().getNano());
        }

        default long toEpochSecond() {
            long epochDay = toLocalDate().toEpochDay();
            long secs = epochDay * 86400 + toLocalTime().toSecondOfDay();
            secs -= getOffset().getTotalSeconds(); // zone으로 알 수 있는 UTC기준 상대적 차이를 알수있는 offset을 더해줌
            return secs;
        }   
      ```
- Zone과 관련된 정보는 "tzdb.dat" 여기서 가져옴 (`~/Library/Java/JavaVirtualMachines/temurin-11.0.13/Contents/Home/lib` 안에있음)
  
- Date
  - new Date()는 System.currentTimeMillis()로 현재 시간을 생성
    - System.currentTimeMillis() 는 UTC 기준으로 1970년1월1일과 현재시간의 차이 (Instant 디폴트와 동일)
  - 그러나 toString을 할때, 시스템타임존(혹은 자바타임존)을 가져와서 계산
    - 즉, 결국 UTC기준으로 Date생성시에 만들었어도, 시스템타임존(혹은 자바타임존)에 맞추어 변경해준다
    - ![Date toString 내부 디버깅](2023-04-18-10-36-49.png)


- 시간 표현
  - UTC, GMT란?
    - UTC를 '협정세계시' 라고 번역하는데, 영어로는 Coordinated Universal Time이다. 단순하게 설명하자면, 영국을 기준(UTC+0:00)으로 각 지역의 시차를 규정한 것이다. 한국은 영국보다 9시간 빠르므로 UTC+9:00이라고 표시한다. 미국 뉴욕은 영국보다 5시간 느리므로 UTC-5:00라고 표시한다.
    - GMT는 Greenwich Mean Time(그리니치 평균시)의 뜻이다. GMT+09:00와 UTC+09:00은 같은 뜻인가? 일상 생활에서는 그냥 같은 뜻이다라고 봐도 아무런 지장이 없다. 일상 생활에서는 GMT와 UTC를 거의 구별하지 않고 섞어서 쓴다. 
    - 역사적으로 GMT가 UTC보다 훨씬 이전에 나온 개념이다. UTC는 20세기 후반에 등장한 개념이지만, GMT는 이미 16세기 후반에 나온 개념이다. GMT의 기준은 태양이다. 지구의 공전궤도가 타원이고, 지구의 자전축이 기울어져 있기 때문에 하루의 길이는 일정하지가 않다. 그래서 UTC는 태양 대신에 원자시계를 기준으로 한다. 전 세계에 400여개의 원자시계가 서로 데이터를 비교하면서 GMT 오차를 보정해나간다. 이 시간 체계를 '국제원자시'(International Atomic Time · IAT 또는 TAI)라고 한다.
    - 출처 : https://jjhwqqq.tistory.com/292

  - 오프셋 (Offset) 
    - 오프셋은 협정 세계시(UTC)와 지역 시간대와의 차이를 나타냅니다. 오프셋은 지역 시간이 UTC보다 몇 시간 빠르거나 느린지를 나타내는 시간 값입니다. 예를 들어, 한국 표준시(KST)는 UTC+9이며, 이는 한국 시간이 UTC보다 9시간 빠르다는 것을 의미합니다. 미국 동부 표준시(EST)는 UTC-5로, 동부 시간이 UTC보다 5시간 느리다는 것을 나타냅니다. 오프셋은 시간대 간의 변환을 쉽게 처리할 수 있게 해줍니다.
    - 출처 : chatGPT 4.0
  - 일광 절약 시간 (Daylight Saving Time, DST) 
    - 일광 절약 시간은 일부 국가와 지역에서 적용되는 시간 관리 체계입니다. 일광 절약 시간은 연간 일정 기간 동안 (대개 봄에서 가을 사이) 시간을 1시간 앞당겨서 저녁 시간의 적정한 밝기를 더 오래 유지하도록 하는 것입니다. 이렇게 하면 에너지 절약 및 교통사고 감소 등의 효과를 기대할 수 있습니다.
    - 일광 절약 시간이 시작되면 시간대의 오프셋은 1시간 증가합니다. 예를 들어, 미국 동부 표준시(EST)는 일광 절약 시간이 적용되면 미국 동부 일광 절약 시간(EDT)이 되고, 오프셋은 UTC-4가 됩니다. 일광 절약 시간이 끝나면 시간대는 원래의 오프셋으로 돌아갑니다.
    - 일광 절약 시간은 국가와 지역에 따라 적용 여부와 시작 및 종료 시점이 다를 수 있으며, 일부 국가와 지역에서는 전혀 적용하지 않을 수도 있습니다. 이로 인해 시간대 변환과 계산이 복잡해질 수 있으므로, 프로그래밍할 때 이를 처리하기 위해 시간 라이브러리를 사용하는 것이 좋습니다. Java의 경우, java.time 패키지의 ZonedDateTime 및 ZoneId 클래스를 사용하여 시간대와 일광 절약 시간을 처리할 수 있습니다. 이를 통해 시간대 간 변환을 쉽게 처리하고, 일광 절약 시간의 시작과 종료에 따른 오프셋 변화도 자동으로 처리할 수 있습니다. 이와 유사한 기능을 제공하는 다른 언어 및 라이브러리도 있으니, 적절한 도구를 사용하여 시간 관리 작업을 수행하는 것이 좋습니다.
    - 출처 : chatGPT 4.0
  
일광 절약 시간은 일부 국가와 지역에서 적용되는 시간 관리 체계입니다. 일광 절약 시간은 연간 일정 기간 동안 (대개 봄에서 가을 사이) 시간을 1시간 앞당겨서 저녁 시간의 적정한 밝기를 더 오래 유지하도록 하는 것입니다. 이렇게 하면 에너지 절약 및 교통사고 감소 등의 효과를 기대할 수 있습니다.
- 참고하기좋은 사이트 : https://jaimemin.tistory.com/1537


---

- 자바의 정석 정리

- java.util.Date
  - 날짜와 시간을 다룰 목적을 만듦
  - jdk 1.0
  
- Calendar
  - jdk 1.1 부터
  - Date의 부족한 기능을 보완하고자나옴
  - 날짜, 시간, 시간대(time-zone) 모두 가지고 있음
  - Calendar 자체는 추상클래스
    - 구현체
      - GregorainCalendar
        - 전세계 공통으로 사용하고있는 그레고리력에 맞게 구현
      - BuddhistCalendar
        - 태국에서 사용..
    - getInstance 메서드를 호출하여 시스템의 국가와 지역설정을 확인해서 태국이면 BuddhistCalendar, 그 외는 GregorainCalendar 반환
  - Date와 Calendar 간의 변환 가능
    ```java
        // Calendar -> Date
        Calendar cal = Calendar.getInstance();
        Date d = new Date(cal.getTimeInMillis());
        
        // Date -> Calendar
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
    ```
  - 두 날짜간의 차이를 구할때는 getTimeInMillis()를 호출하여 차이를 구한다
    - 이를 일 단위로 얻기 위해서는 `getTimeInMillis()로 구한 차이 / 24(시간) * 60(분) * 60(초) * 1000` 으로 나누어주어야한다
  - set 메서드를 통해서 Calendar의 시간 지정가능
  - add, roll 메서드를 통해서 특정 필드(ex. month)만 증가하거나 뺄 수 있다(빼는건 마이너스(-)를 붙여주면됨)
    - roll은 add와 동일한 기능이지만, 다른 필드에 영향을 미치지않는다 (ex. cal.add(Calendar.DATE, 31) 호출시 월(month)이 변경되나, roll은 그렇지않다)
  - 주의할점..
    - Calendar를 통해서 월을 가져오려면, get(Calendar.MONTH)로 확인할수있는데, 결과값의 범위가 1~12가 아닌, 0~11이기때문에 이를 주의해야한다


--- 

- DecimalFormat을 이용해서 콤마(,)가 있는 문자열을 숫자로 변환하자!!!@#!@#
  - 사진
- SimpleDateFormat
  - 사진1
  - 사진2

---

- java.time 패키지
  - Date와 Calendar의 단점을 해소하기위해 1.8부터 출현
  - 패키지 간단설명
    - java.time : 날짜와 시간을 다루는데 필요한 핵심 클래스 제공
    - java.time.chrono : 표준(ISO)이 아닌 달력 시스템을 위한 클래스들을 제공
    - java.time.format : 날짜와 시간을 파싱하고, 형식화하기 위한 클래스들을 제공
    - java.time.temporal : 날짜와 시간의 필드(field)와 단위(unit)를 위한 클래스들을 제공
    - java.time.zone : 시간대(time-zone)와 관련된 클래스들을 제공
  - 여기서 사용되는 클래스는 불변이다!!(ex. LocalDate, ZonedDateTime 등등) 
    - 즉, 스레드에 안전하다!
  - 핵심클래스
    - LocalDate : 날짜표현
    - LocalTime : 시간표현
    - LocalDateTime : 날짜 + 시간
    - ZonedDateTime : LocalDateTime + 시간대(time-zone)
    - Instant : 날짜와 시간을 초 단위(나노초)로 표현 
      - UTC가 기준
    - Period : 날짜간의 차이를 표현
    - Duration : 시간의 차이를 표현