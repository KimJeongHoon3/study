csrf

- Cross Site Request Forgery
- 요청을 변조하는것.. 
  - 브라우저가 요청시 쿠키를 항상 포함해서 보내기떄문에, 이를 활용해서 악의의 작업을 수행하도록 요청만 변조
  - 즉, 캐시에 인증같은 정보가 없다면(ex. jwt를 헤더로 보냄), 다시말해 캐시로 인증을 수행하지않는다면 csrf는 문제되지않는다. (그래서 http header로 인증하는 api 서버들은 csrf 검증을 disable)
    - 그러나 캐시를 여전히써야한다? csrf token으로 막을 수 있다 (헤더로 요청을 보내야하니..)
- 예를들어, 내가 취약한 사이트에서 로그인하였을때, 공격자가 해당 사이트의 게시글에 피싱사이트를 게시하고 클릭하도록 유도하게되면, 피싱사이트를 클릭하게되는 순간 해당 사이트에서 나의 권한(인가)을 기반으로 공격자가 원하는 요청을 수행
  - 돈이 빠져나갈수도있고, 메일을 다른 사람에게 대량으로 보낼수도있고.. 등등
- [내용설명 간단 굿](https://devscb.tistory.com/123)
- [spring security 적용 및 rest api에서는 왜 csrf가 Diable인지 설명](https://zzang9ha.tistory.com/341)

- rest api 앱에서 spring security csrf를 Disable 하는이유?
  - jwt와 같이 client가 헤더를 통해서 인증을 하게되는 경우가 많은데, 이럴경우 csrf에 안전..
    - 하지만, xss는 여전히 취약할수있음..
  - 핵심은 쿠키에 넣으면 동일하게 csrf에 취약할수있다는것..! 
  - https://kchanguk.tistory.com/197?category=887999
  - https://www.baeldung.com/spring-security-csrf
    - If our stateless API uses token-based authentication, such as JWT, we don't need CSRF protection, and we must disable it as we saw earlier.
    - However, if our stateless API uses a session cookie authentication, we need to enable CSRF protection as we'll see next.
- spring security는 csrf token을 사용해서 제어해줌
  - csrf 토큰은 세션에 저장됨 (spring security 디폴트)
  - 세션에 저장한것과 헤더에 저장된 토큰 값을 비교하여 토큰 검증함