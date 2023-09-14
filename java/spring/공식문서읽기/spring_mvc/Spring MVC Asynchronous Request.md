Spring MVC Asynchronous Request

- DeferredResult
  - DeferredResult로 컨트롤러에서 응답해준다면, 일단 해당 요청은 필터를 모두 태우고 스레드 종료. 그러나, 여기서 요청은 마무리되지않고 열려져 있는 상태이고, setResult를 통해서 요청에 실질적인 응답값을 셋팅했으면, servletContainer가 다시 DispatcherServlet에 요청을 전송하여 응답을 마무리한다. 즉, 이때 비로소 클라이언트는 응답을 받게됨
- exception handling
  - 동작은 위와 같음. 결국 다시한번 DispatcherServlet이 처리를하는데, 예외를 처리해주는것
    - DispatcherServlet에서 예외처리하는동작과 동일
- interception
  - AsyncHandlerInterceptor(HandlerInterceptor의 구현체)와 DeferredResultProcessingInterceptor, CallableProcessingInterceptor 를 사용가능. 
    - DeferredResultProcessingInterceptor, CallableProcessingInterceptor는 좀더 상세하게 비동기 요청의 라이프사이클에 대해 좀더 상세하게 인터셉팅 가능
  - 위의 동작방식을 기반으로 보자면, interceptor는 두번 타게된다. 다만, 첫 요청시에는 HandlerInterceptor.postHandle과 HandlerInterceptor.afterCompletion 은 동작하지않는다. 대신, AsyncHandlerInterceptor.afterConcurrentHandlingStarted 이를 호출하게된다.
    - 그리고 결과 처리를 위한 두번째 요청을 DispatcherServlet이 다룰때에는 기존 요청처리와 동일하게 모든 preHandle, postHandle, afterCompletion을 타게된다(AsyncHandlerInterceptor.afterConcurrentHandlingStarted는 호출안됨)
  - 이렇게 두번씩 태우고 싶지않으면, DeferredResultProcessingInterceptor를 사용하면 된다
  

- WebFlux와 Spring MVC의 비동기 차이?
  - 간단하게는.. Spring MVC는 응답 write시 별도스레드를 만들어서 처리. WebFlux는 Non-blocking I/O를 사용하기에, 응답에 write시에 추가적인 스레드가 필요없음
  - 또 다른 근본적인 차이점은 Spring MVC는 컨트롤러 메서드 인수(예: @RequestBody, @RequestPart 등)에서 비동기 또는 반응형 유형을 지원하지 않으며, 모델 속성으로 비동기 및 반응형 유형을 명시적으로 지원하지 않는다. Spring WebFlux는 이 모든 것을 지원
  
