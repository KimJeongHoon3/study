# REST API

- HTTP로 요청을 보낼때 HTTP의 메소드를 사용하여 URI를 통해서 어떤 동작을 할지를 유추하기 쉽도록 만든 API
즉, 보통 POST, GET, PUT(PATCH), DELETE 라는 HTTP의 메소드를 사용하게되는데 POST는 데이터를 입력, GET은 조회, PUT은 update, DELETE는 삭제이므로, 상황에 적절한 메소드를 사용하여 요청을하면 그에 맞는 작업이 이루어지는것! 
- 이런것들이 개발자들 사이에 공유가 된다면 일일이 URI에 어떤 동작을할것인지 명시할필요가없음
- 또한 그렇기에, URI는 명사를 사용! 행동을 나타내는 동사사용x

- 간단한 설명 굿 : https://moondol-ai.tistory.com/237