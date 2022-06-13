apache + tomcat 연동

- 아파치로 ajp 사용하여 톰캣 로드 밸런싱 관련 설명 굿 : https://taetaetae.github.io/2019/08/04/apache-load-balancing/


- apache 셋팅관련 설명
  - https://webdir.tistory.com/178
  - 쉽게 설명 굿 : https://joont.tistory.com/46
  - proxypass & proxypassreverse 관련 설명 : https://httpd.apache.org/docs/2.4/mod/mod_proxy.html#proxypass
    - proxypass는 말그대로 리버스 프록시 서버(서버 앞단에서 클라이언트의 요청을 받는..)에서 지정한 경로로 포워딩해주는것..
    - proxypassreverse 는 응답할때 헤더(location, content-location)와 url을 조정해준다함.. 
    - 보통 위 두개의 경로는 같은데.. 정확하게 이해안감..