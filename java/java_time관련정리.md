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
      - 기존에는 TimeZone 클래스로 시간대를 다루었으나, 새로운 시간 패키지에서는 ZoneId라는 클래스를 사용
      - ZoneId는 일광절약시간(DST, Daylight Saving Time)을 자동적으로 처리해준다
      - LocalDateTime에 atZone메서드로 ZoneId를 추가하면 ZonedDateTime을 얻을 수 있다
    - Instant : 날짜와 시간을 초 단위(나노초)로 표현 
      - UTC가 기준
      - 에포크 타임(EPOCH TIME, 1970-01-01 00:00:00 UTC)부터 경과된 시간을 나노초 단위로 표현
        - Epoch time?
          - 컴퓨터 시스템에서 날짜와 시간을 표현하는 데 사용되는 특정 시간 체계. Epoch time은 보통 1970년 1월 1일 00:00:00 UTC(협정 세계시)를 기준으로한 경과 시간을 초 단위로 표현.
    - Period : 날짜간의 차이를 표현
    - Duration : 시간의 차이를 표현
  - 핵심 인터페이스로 묶어보기
    - Temporal(TemporalAccessor 확장), TemporalAdjuster 구현체
      - LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant 등
      - 주요 관련 메서드 살펴보기
        ```java 
          // Temporal 인터페이스 내부
          Temporal with(TemporalField field, long newValue); // 해당 메서드를 통해 필드의 값을 변경한다

          default Temporal with(TemporalAdjuster adjuster) {  // 해당 메서드를 통해 필드의 값을 변경한다
              return adjuster.adjustInto(this);
          }

          Temporal plus(long amountToAdd, TemporalUnit unit); // 특정 필드에 값을 더할수있다 (minus는 빼기) 

          default Temporal plus(TemporalAmount amount) { // TemporalAmount 구현체인 Period와 Duration 과 같은 기간의 차이도 더할 수 있다
              return amount.addTo(this);
          }
        ```
    - TemporalAmount 구현체
      - Peroid, Duration
    - TemporalUnit과 TemporalField
      - 날짜와 시간의 단위를 정의해놓은것이 TemporalUnit 인터페이스고 이를 구현한 열거형이 ChronoUnit
      - 년, 월, 일 등 날짜와 시간의 필드를 정의해놓은것이 TemporalField이고, 이를 구현한 열거형이 ChronoField
      - ChronoField vs ChronoUnit
        - ChronoField는 날짜 및 시간의 구성 요소에 직접 접근하고 조작하는 데 사용되는 반면, ChronoUnit은 시간의 경과나 차이를 계산하는 데 사용
        - 예를들어 Temporal 인터페이스는 `Temporal plus(long amountToAdd, TemporalUnit unit);`와 같이 정의가 되어있는데, 이때 값을 더하는 단위에는 ChronoUnit을 사용할수있다
          - ex. `localDateTime.plus(5, ChronoUnit.DAYS);`
        - 또한, TemporalAccessor 인터페이스는 `default int get(TemporalField field) {...}` 이란 디폴트 메서드를 정의하는데, TemporalAccessor 구현체의 월(month)과 같은 특정 필드의 값을 가져오는데 ChronoField를 사용할수있다
          - ex. `localDateTime.get(ChronoField.DAY_OF_MONTH) // 2023-06-24T12:18:54.133938 의 localDateTime일때 24를 반환해준다`
            - ChronoField 같은 경우 int나 long을 반환해주는데, 값의 범위를 알고싶을때는 range메서드를 호출해서 확인가능하다
              - ex. `ChronoField.HOUR_OF_DAY.range()`
      
  - 각 날짜 클래스별 특이사항 정리
    - LocalTime
      - truncatedTo(TemporalUnit) 인스턴스 메서드를 사용하여 전달받은 인자보다 작은 필드의 단위를 0으로만듦
        - LocalDate에는 0이 될수있는 필드가없으니.. 없음
      - isEquals vs equals
        - 연표(chronology)가 다른 두 날짜를 비교하기위함.. 연표가 다를때 equals를 사용하면 날짜가 같아도 false가 리턴됨. isEquals는 오직 날짜만 비교하기때문에 연표랑 상관없음
    - LocalDateTime
      - LocalDate로 아무 시간붙지않은 LocalDateTime 만들기
        ```java
          LocalDate now = LocalDate.now();
          System.out.println(now); // 2023-06-24
          System.out.println(now.atStartOfDay()); // 2023-06-24T00:00 => 요거 쓰면될듯
          System.out.println(now.atTime(LocalTime.of(0,0,0))); // 2023-06-24T00:00
        ```
    - Instant
      - Instant는 시간을 초단위와 나노초 단위로 나누어 저장
        - `Instant.now().getEpochSecond()`
        - `Instant.now().getNano()`
      - 밀리초 단위의 EPOCH TIME을 필요로 하는 경우를 위해 toEpochMilli() 이 있음
        - `Instant.now().toEpochMilli()`
      - Instant와 Date간의 변환
        - Instant는 기존 java.util.Date를 대체하기 위한것이기에, 변환할수 잇는 메서드가 8부터 도입
          ```java
            // Date 클래스 내부
            public static Date from(Instant instant) {
                try {
                    return new Date(instant.toEpochMilli());
                } catch (ArithmeticException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }

            public Instant toInstant() {
                return Instant.ofEpochMilli(getTime());
            }
          ```
    - ZonedDateTime
      - 현재 특정 시간대 구하기
        ```java
          System.out.println(ZonedDateTime.now(ZoneId.of("Asia/Seoul"))); // 현재 서울시간 ex. 2023-06-24T15:37:15.541160+09:00[Asia/Seoul]
          System.out.println(ZonedDateTime.now(ZoneId.of("America/New_York"))); // 현재 미국시간 ex. 2023-06-24T02:37:15.542595-04:00[America/New_York]
          System.out.println(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Asia/Seoul"))); // 현재 서울시간
        ```
    - OffsetDateTime
      - ZoneId가아닌 ZoneOffset을 사용하는것이 OffsetDateTime
        - ZoneOffset
          - UTC로부터 얼마만큼 떨어져 있는지를 ZoneOffset으로 표현
          - 서울은 +9 즉, UTC보다 9시간 빠름
      - 그렇기에 일광절약시간 같은 규칙들은 없고, 오직 UTC기준 시간의 차이로만 구분
      - ZonedDateTime에 toOffsetDateTime()을 호출하여 가져올수잇음
      - => ZonedDateTime으로 통일해서 쓰면되지않을까나..

    - TemporalAdjusters
      - 자주쓰일만한 날짜 계산들을 대신 해주는 메서드를 정의해놓은것이 TemporalAdjusters 클래스
        - ex. 지난주 토요일이 며칠인지, 이번달의 3번째 금요일은 며칠인지..
      - TemporalAdjuster의 구현체를 리턴해주는 헬퍼클래스(유틸리티 클래스)
      - 그렇기때문에 여기에 정의되어있지않은것은 TemporalAdjuster를 직접 구현하면됨
    - Period와 Duration
      ```java
        LocalDate date1 = LocalDate.of(2024, 10, 10);
        LocalDate date2 = LocalDate.of(2023, 7, 10);
        Period p = Period.between(date1, date2); // date1이 date2 보다 날짜상으로 이전이면 양수, 이후면 음수로 저장
        System.out.println(between.getDays()); // 0
        System.out.println(between.getMonths()); // -3
        System.out.println(between.getYears()); // -1
        System.out.println(between.toTotalMonths()); // -15 .. 일단위는 아예 무시함.. 있어도무시..
        System.out.println(between.negated().toTotalMonths()); // 양수로 구하고싶으면 negated 사용할것

        // Period는 년월일을 분리해서 저장하기때문에 D-day구하는거에는 적절하지않다 (위의 예시만 봐도 정확히 1년 3개월 차이나는 상황에서 days를 가져오니 0이다)
        // 이런 경우 until 인스턴스 메서드(Temporal 인터페이스의 메서드)를 사용해서 가져온다
        System.out.println(date1.until(date2, ChronoUnit.DAYS)); // -458

        // 기간이 같은지 혹은 앞의 날짜가 이전인지 확인 가능
        boolean sameDate = Period.between(date1, date2).isZero();
        Period.between(date1, date2).isNegative(); // true 반환하면 date1이 date2보다 더 느린것..

        ////////////////////////////////////////////

        // Duration에는 ChronoUnit.SECONDS와 ChronoUnit.NANOS 밖에 사용못함 .. 그래서 아래와 같이 변환해서 가져올수있음
        LocalTime localTime = LocalTime.of(0, 0).plusSeconds(duration.getSeconds());
        localTime.getHours();
        localTime.getMinute();
        localTime.getSecond();
        duration.getNano(); // nano 시간은 분리되어있으니 nano 가져오려면 이렇게..
      
        
      ```

  - 파싱과 포맷
    - 날짜와 시간을 원하는 형식으로 encode(to String)하고 Decode(from Temporal구현체들) 할수있음
    - java.time.format 패키지에 많이 있음.. 여기서 DateTimeFormatter가 핵심!
      - DateTimeFormatter는 자주쓰이는 다양한 형식들을 기본적으로 정의하고있고, 필요하다면 직접 정의해서 사용도 가능
      ```java
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt)); // 2023-06-24T16:28:21.784307
        System.out.println(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // 2023-06-24T16:28:21.784307

        // 커스텀도 가능
        DateTimeFormatter custom = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
        System.out.println(custom.format(ldt)); // 2023/06/24 16:30:49.973

        // locale에 종속된 형식화도 가능
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        System.out.println(dateTimeFormatter.format(ZonedDateTime.now())); // 2023년 6월 24일 오후 4시 11분 23초 KST

        // 반대로 decode 하기 (String -> LocalDateTime)
        LocalDateTime parse = LocalDateTime.parse("2023/06/24 16:30:49.973", custom);
        System.out.println(parse); // 2023-06-24T16:30:49.973

        
      ```
