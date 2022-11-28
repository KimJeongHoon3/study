csrf

- Cross Site Request Forgery
- 요청을 변조하는것..
- 예를들어, 내가 취약한 사이트에서 로그인하였을때, 공격자가 해당 사이트의 게시글에 피싱사이트를 게시하고 클릭하도록 유도하게되면, 피싱사이트를 클릭하게되는 순간 해당 사이트에서 나의 권한(인가)을 기반으로 공격자가 원하는 요청을 수행
  - 돈이 빠져나갈수도있고, 메일을 다른 사람에게 대량으로 보낼수도있고.. 등등
- [내용설명 간단 굿](https://devscb.tistory.com/123)
- [spring security 적용 및 rest api에서는 왜 csrf가 Diable인지 설명](https://zzang9ha.tistory.com/341)