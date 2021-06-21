IntelliJ의 프로젝트 구조와 SDK 셋팅방법(+Project Language Level이란)

- Intellij는 Mdoule, Project, Global 이렇게 3가지 구조로되어있음
- <img src="https://resources.jetbrains.com/help/img/idea/2020.3/settings-types.png">

- module : 하나의 모듈에만 적용되는 것으로 프로젝트에 대해 구성된 것과 다른 SDK 및 Language level 및 자체 라이브러리르 가질수있음
- project : 현재 열려 있는 프로젝트에만 적용되는것을 의미. 프로젝트 내의 모든 모듈에 사용할수있는 설저ㅓㅇ이 가능
- global : 인텔리제이에서 생성된 프로젝트에 대해 일관된 설정을 해주는것을 의미

- intellij 에서 바로 SDK 다운받을수도잇음

- project SDK와 Laguage Level은 다를수있다!
  - 만약 SDK 는 자바 8을 사용하는데, laguage level은 자바6을 셋팅되도록하면 컴파일될때 6버전 기준으로 확인한다
  - 이는 컴파일러가 제공해주는기능임
    ```shell
    C:\>javac -help
    Usage: javac <options> <source files>
    where possible options include:
    -source <release>
            Provide source compatibility with specified release
    -target <release>            
            Generate class files for specific VM version
    ```


- 참고사이트 
  - https://atoz-develop.tistory.com/entry/JAVA-IntelliJ-IDEA-Project-language-level-%EC%84%A4%EC%A0%95%EC%9D%98-%EC%9D%98%EB%AF%B8
  - 적용방법 잘 나옴 : https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=rinjyu&logNo=222132661427