GSLB

- DNS의 단점을 보완해준거라 볼수있음
  - 기존 DNS는 라운드로빈으로 로드밸런서 역할을 해줄 수는 있었지만, 헬스체크나 해당 요청을 어떤 서버가 더 빠르게 처리해줄수있는지를 알 수 없음
  - GSLB는 그게 가능..
    - 부하상태, 헬스체크 결과, 클라이언트의 지리적위치 등을 고려
- 대략적인 동작시나리오 (GSLB)
  1. 브라우저에 도메인 입력
  2. LDNS(local DNS)에 DNS 쿼리 날림
  3. LDNS가 없으면 DNS서버에 DNS 쿼리 날림
  4. DNS에 해당 도메인에 정보가 있으면, 정보를 포함한 응답을 반환 (GSLB를 사용하기에, GSLB한테 물어볼 수 있도록 GSLB의 IP를 반환) 
  5. 다시 LDNS는 DNS로 부터 받은 정보를 기반으로 GSLB에 IP 요청
  6. GSLB는 해당 도메인과 연결된모든 IP에 대한 헬스체크 및 서비스 응답시간/지연 등을 고려하여 최적의 IP주소를 전달
  7. LDNS는 GSLB로 전달받은 IP주소를 받고, 이를 캐싱한뒤 클라에게 전달
  8. 클라는 해당 IP주소로 패킷전송


- Enterprise를 위한 GSLB(Global Server Load Balancing) - 1편: 개념 및 서비스 로직 | NETMANIAS - https://www.netmanias.com/ko/post/blog/5620/dns-data-center-gslb-network-protocol/global-server-load-balancing-for-enterprise-part-1-concept-workflow
- KT 유클라우드, GSLB로 무중단 서비스 지원 | NETMANIAS - https://www.netmanias.com/ko/?m=view&id=blog&no=5617&kw=gslb
- [제일정리깔끔](https://haeunyah.tistory.com/m/115)


- CNAME과 GSLB (챗GPT 답변 - 결국 위에 정리한거랑 동일한 내용이긴함)
  ```md
    특정 도메인에 CNAME이 설정되어 있고, GSLB(Global Server Load Balancing)를 사용하고 있다면, 일반적으로 다음과 같은 과정이 일어납니다:

    1. **DNS 쿼리**: 사용자가 특정 도메인(예: `example.com`)에 대한 DNS 요청을 보냅니다.

    2. **CNAME 해석**: DNS 서버는 `example.com`에 대한 요청을 받고, 이 도메인에 설정된 CNAME 레코드(예: `cname.example.com`)를 반환합니다.

    3. **GSLB 처리**: 사용자의 장치는 CNAME에 해당하는 실제 주소(`cname.example.com`)에 대한 새로운 DNS 요청을 보냅니다. 이때 GSLB 시스템이 활성화되어 있다면, GSLB는 사용자의 위치, 서버 상태 등을 고려하여 적절한 서버의 IP 주소를 결정합니다.

    4. **최종 IP 주소 반환**: GSLB 시스템은 최적의 서버 IP 주소를 DNS 응답으로 반환합니다.

    결국, 사용자는 GSLB 시스템에 의해 선택된 최적의 서버로 연결됩니다.
  ```
  
- CNAME은 아래와 같이 사용가능
  ```md
    example.com.   IN   A   192.0.2.23
    www.example.com.   IN   CNAME   example.com
    a.example.com.   IN   CNAME   example.com
    b.example.com.   IN   CNAME   example.com
    c.example.com.   IN   CNAME   example.com
  ```

