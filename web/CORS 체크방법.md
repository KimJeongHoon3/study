CORS 체크

- curl -I -X OPTIONS \
  -H "Origin: http://127.0.0.1:5003" \
  -H 'Access-Control-Request-Method: GET' \
  -H 'Content-Type: application/json' \
  http://localhost:8889/distribution 2>&1

  - "-I" : 헤더만 전달한다!
  - "-X" : Specifies a custom request method to use when communicating with the HTTP server. (default인 GET말고 다른거 쓰고자할때)
  - "-v" : 디버깅하는건데 사용해도되고 안해도됨

  - OPTIONS 메소드를 왜 쓰나?
    - 보통 CORS는 preflightRequest 방식(예비요청 후 본요청)을 사용하는데, 예비요청할때 OPTIONS 메소드를 사용
    - 예비요청의 응답으로 Access-Control-Allow-Origin과 요청한 origin을 비교하여 본요청을 보내도 괜찮을지 판단하여 진행
      - 응답값
        - HTTP/1.1 200  
            < Vary: Origin  
            < Vary: Access-Control-Request-Method  
            < Vary: Access-Control-Request-Headers  
            < Access-Control-Allow-Origin: * // 브라우저는 이를 통해 서버가 모든 Origin을 받는구나.. 로 생각함   
            < Access-Control-Allow-Methods: GET,HEAD,POST  
            < Access-Control-Max-Age: 1800  // 요청할때마다 예비요청을 계속하기 그러니 캐싱함
            < Content-Length: 0  
            < Date: Tue, 26 Oct 2021 02:00:50 GMT