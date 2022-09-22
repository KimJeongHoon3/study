GSLB

- DNS의 단점을 보완해준거라 볼수있음
  - 기존 DNS는 라운드로빈으로 로드밸런서 역할을 해줄 수는 있었지만, 헬스체크나 해당 요청을 어떤 서버가 더 빠르게 처리해줄수있는지를 알 수 없음
  - GSLB는 그게 가능..
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