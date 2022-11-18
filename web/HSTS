# HSTS (HTTP Strict Transport Security, RFC 6797)
- 웹 사이트 접속시 Https를 강제하도록 하는 스펙
- 서버가 header에 HSTS 사용에 대한것을 응답으로 전달해주면, HSTS를 지원하는 브라우저(클라이언트)는 이를 해석해서 적용
- 대부분의 최신 브라우저는 HSTS를 지원
- 왜 필요한가?
  - 기존에 301, 302, 307, 308 등의 redirect로 http -> https로 전환을 하였는데, 그로 인해 서버에 대해 기본적으로 두번의 요청이 가게되는것 뿐만 아니라 ssl strip 으로 보안에 취약
    - ssl strip?
      - 브라우저가 웹 서버 사이에 공격자가 암호화 되지않은 데이터를 감청하는것
      1. 사용자는 http://bank 로 연결 시도. 공격자는 그 연결을 그대로 웹서버에 전달
      2. 웹서버는 https 를 이용하여 연결하도록 응답
      3. 공격자는 웹서버의 응답을 조작하여 http로 연결하는 것처럼 사용자에게 응답
      4. 사용자는 응답받은 내용을 근거로 http로 연결. 공격자는 내용을 조회 한 후 https로 전환하여 웹서버에 연결을 전달
      5. 최종적으로 공격자는 ssl 을 strip하여 중간에서 내용을 감청 할 수 있음
  - hsts는 내부적으로 307 (internal redirect. Temperal redirect와 다름.. Temperal Redirect는 서버로부터 307응답을 받은것.. 여기는 브라우저 내부적으로 redirect해준것. 즉, 서버쪽으로 요청이 안감) 을 사용
  - 그렇기때문에 브라우저가 https를 셋팅해서 요청하는것이기 때문에 SSL strip을 막을 수 있음
  - 그리고 또한 그렇기에 https 통신이 한번은 일어나야 HSTS가 가능
    - 브라우저 입장에서 https로 문제없이 통신이 되었다는것을 알아야 http에 대한 요청을 https로 변경해주는게 안전하것지

```java
    //haproxy에서 셋팅방법
    frontend 
        http-response set-header Strict-Transport-Security "max-age=16000000; includeSubDomains; preload;"

        // max-age : 필수값. 단위는 초. 해당시간만큼 HSTS 적용
        // includeSubDomains : HSTS가 해당 도메인의 서브 도메인에도 적용되고 있음을 알려줌
        // preload : 브라우저의 Preload List에 해당 도메인을 추가할 것을 알려줌
    
```


- [hsts 개념설명](https://rsec.kr/?p=315)
  - [추가 설명 굿 - preload list관련 내용 안맞는거같음..](https://brunch.co.kr/@sangjinkang/40)
- [haproxy에서 hsts 적용하는방법](https://www.haproxy.com/blog/haproxy-and-http-strict-transport-security-hsts-header-in-http-redirects/)
- [크롬에서 hsts 목록 관련 Controller](chrome://net-internals/#hsts)
- [hsts가 적용되기 위해서는 브라우저가 한번은 https로 요청을해야한다..](https://superuser.com/questions/1107285/hsts-not-working-with-chrome)

- 적용시 참고 사이트
  - https://www.uknew.co/hsts-%EC%84%A4%EC%A0%95%EB%B0%A9%EB%B2%95/
  - https://hstspreload.org/


HSTS 적용하기위해서는 내용을 살펴보니, 웹서버측에서 헤더에 HSTS 사용한다는 응답을 보내야하고, 최초 1회 HTTPS로 정상 통신이 이루어져야지만 해당 스펙이 적용됩니다. 하여, 현재 적용할 수 있는 방향으로는 아래 3가지가 있을것 같습니다.

1. https redirect(307) + HSTS 헤더 적용
   - http로 요청이 들어오면 최초 1회 https로 haproxy가 redirect 해주게되고, 이때부터 HSTS 적용. 즉, 바로 적용됨
   - 주의점
      - ticketlink 메인 페이지부터 차근차근 접속한다면 큰 문제가 없을것 같으나, 307로 응답을 주었을때 body를 항상 포함해서 다시 redirection 할지 모름 (웹 브라우저 포함 client가 다양..)
        - 특히 app 같은 경우, 307을 적용햇을때 body가 사라지지않게.. (라이브러리나 대부분의 브라우저는 거의 지원되긴하긴하나..)
2. HSTS 헤더만 적용
   - 예매진행간에 https로 전환해주는곳이 잇는것으로 알고 있는데, 그런 부분을 만나야 HSTS 적용시작됨
   - 만약 만료기간이 지나면, 다시 http로 들어와도 https 전환안됨. 1번을 다시 만나야함
   - 주의점
      - 적용 후에 큰 문제는 없을것 같으나, HSTS 만료기간 지나면 다시 http로 전환되는데, 언제다시 https로 적용될지 알 수 없음
3. 대기

