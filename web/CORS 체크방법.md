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


- cors가 항상 위와같은(Preflight)방식만으로 확인하지않기때문에, 서버에서 어떤방식으로 CORS처리하고잇는지 확인이필요.. 
  - ex. OPTIONS를 허용하지않는 서버가 있을수 있기떄문에..(요청시 403리턴..) 이럴땐 preflight 방식이아닌, simple request 방식으로 접근필요.. (물론, 서버를 우리가 핸들링 할 수 있다면 Options 메서드관련 처리를 하면됨)
    - simple request 방식으로 접근하기위한 조건들이 필요..
      - ex. Content-Type 헤더를 사용할 경우에는 application/x-www-form-urlencoded, multipart/form-data, text/plain만 허용.. (더 있음)

- ajax에서 redirect응답시 cors..
  - ajax 사용시 redirect 응답을 받게되면 항상 preflight 방식인가?
    - 요청대상과 출처가 같을때
      - 당연안날림..
    - 요청대상과 출처가 다를때
      - 브라우저가 예비요청을 날림..
      - ajax에서 옵션으로 `crossDomain: true` 로 놓으면 예비요청안날림..
        - 그래도 브라우저에서 서버쪽 응답으로 교차출처 관련한 응답을 헤더에 안넣어주면 cors 에러뱉음
        - side effect?
  - 해결책?
    - ajax 가 redirect 받앗을 경우 내부 라이브러리에서 자동으로 Follow 되지않게하고, 직접 핸들링 (바로 location.href 와 같은걸 사용해서 페이지 이동..?)
    - ajax 가 redirect 받았을 경우 브라우저가 Preflight 방식으로 Options 메서드로 요청 보내지않도록
      - ajax에서 옵션으로 `crossDomain: true`
        - 현재, 티링은 사용하는곳이 없는데, 해당 옵션에 대한 side effect?
    - ajax에서 redirect로 응답시 main 페이지