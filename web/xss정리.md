xss란

- 사용자가 입력한 정보를 출력할때 스크립트가(보통javascript) 실행되도록하는 공격기법
- 예를 들어 공격자가 댓글에 스크립트 코드를 심어놓았을때, 어떤 누군가가 해당 글을 클릭하게되면 댓글에 스크립트 코드가 있기때문에 xss에 대한 방어가 안되어있는 사이트라면 스크립트 코드가 그대로 실행된다
  - 여기서 스크립트 코드에는 쿠키값을 사용해서 삭제api를 호출하는 등 다양한 공격을 할 수 있다.
- [참고 사이트](https://opentutorials.org/module/411/3961)
- [xss와 csrf 차이](https://program-developer.tistory.com/99)
  - xss는 Client공격. csrf는 서버공격(서버 공격으로 인해 client가 궁극적으로 피해를 입겠지..)