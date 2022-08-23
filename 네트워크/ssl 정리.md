- ssl 정리!
  - ssl 목적
    - 안전한 서버인지 확인
    - 안전한 클라이언트인지 확인
      - 클라이언트가 서버에게 제공할 인증서 필요 (보통은 서버가 발급해주는듯)
    - 데이터전송시 암호화
  - tcp 3-way 핸드쉐이크 끝나고 진행
  - 아래와 같은 과정을 가짐
    - ![](ssl_handshake.png)
    1. Client Hello (클라이언트 -> 서버)
       - ssl 시작하니 본인 알려주는것인데, 3가지 전달
       - client가 원하는 TLS버전, 자신이 지원하는 cipher list, 자신이 생성한 난수정보
    2. Server Hello (클라이언트 <- 서버)
       - 서버에서 전달하는 인사.. 얘 또한 3가지 전달
       - 자신이 쓰고있는 ssl 버전, 자신이 생성한 난수정보, 클라의 cipher list중 하나 선택하여 전달
    3. Server certificate or Server Key Exchanges (클라이언트 <- 서버)
       - 자신이 가지고 있는 인증서 전송
    4. Certificate Request (클라이언트 <- 서버)
       - 클라이언트랑 마찬가지로 클라이언트에게 요놈이 정상인지 확인하기위해서 인증서를 요청!
       - 이는 옵션임
       - 보통의 naver와 같은 웹사이트의 ssl은 서버가 정상인지 확인하기 위해서 서버의 인증서만 받고 제대로된 인증서인지 확인하는데(브라우저, 즉 클라에서..), 클라이언트의 인증도 필요한 경우 이를 활용 (클라이언트에게 인증서 전달해주는 서비스는 이를 사용한다 보면될듯)
    5. Server hello done (클라이언트 <- 서버)
       - 서버 응답 끝..
    6. Client Certificate (클라이언트 -> 서버)
       - 서버가 Certificate Request를 보냈다면 이를 전달.. Client 인증서 없으면 패스
       - (이를 자바에서 사용하려면 클라이언트 인증서를 keystore에 등록..)
    7. Client key exchange (클라이언트 -> 서버)
       - 클라이언트는 자신이 만든 난수와 서버가 만든난수를 통해 pre-master-secret를 생성
       - pre-master-secret 이 바로 통신할때 사용할 대칭키로 보면되고, 이를 서버에게 전달받은 공개키로 암호화하여 전달
    8. Client verify (클라이언트 -> 서버)
       - 클라이언트 인증서의 무결성에 대해 검증하는부분. client 인증서 없으면 패스
    9. Change cipher spec / finished  (클라이언트 -> 서버)
       - 클라이언트가 협상된 알고리즘과 키를 활용해 Finish메세지를 암호화하여 전송
    10. Change cipher spec / finished (클라이언트 <- 서버)
       - 서버 또한 클라이언트의 메세지를 확인하고 동일하게 Change Cipher Specs 메시지 전송 후 (Finished)메세지를 통해 암호화 된 통신을 사용


- [참고사이트](https://run-it.tistory.com/29)
- [간결하게 잘 나옴](https://cheapsslsecurity.com/p/what-is-2-way-ssl-and-how-does-it-work/)