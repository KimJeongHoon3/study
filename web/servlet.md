- 서블릿
	- 서블릿 컨테이너는 tomcat과 같은 was가 만들어주고, 그런 WAS를 통해서 내가 만든 비지니스 로직을 "호출" 하게된다(비지니스로직이 들어가있는게 서블릿객체.. 이 객체는 싱글톤임! 굳이 여러번 만들필요없으니! 대신, request와 response는 계속 생성해줘야겟지? 다르니깐). 호출할때, request와 response를 같이 전달하게 되는데, request는 http에서 요청한 데이터들을 넘겨주고, response는 응답값들에 대한 정보를 넣게된다. 즉, request에서 데이터를 받아서 비지니스 로직을 처리하고, 그에 대한 결과를 response에 담아서 넘겨주면 WAS는 알아서 요청한 웹브라우저에 내용을 전달하게된다.
	- 즉, 서블릿컨테이너와 서블릿이 있기때문에 HTTP 통신규약을 다 파싱해주어 개발자는 비지니스로직에 집중할수있게된것!!
	
- tomcat과 같은 웹서버는 알아서 멀티스레드를 관리하도록 되어잇다.. 그래서 요청이 순간적으로 많이 들어와도 처리가 가능하다.. 그런데 요청시에 스레드를 계속해서 하나씩 늘릴수없으므로 스레드풀을 사용하게되며, 이에 대한 적절한 값을 주는것이 백엔드 개발자가 해야할 중요한 처리이다. jmeter나 ngrinder를 통해서 적절한 스레드풀의 스레드 갯수를 찾을수있다. 무튼 핵심은 WAS는 멀티스레드가 지원이 되는것이고, 이에 따라 임계영역(멀티스레드에 공유되는영역)을 주의하여 개발하면된다!

- 스프링 말고 예전에 주소에 대한 맵핑은 주소를 맵핑하는 전용 FrontController를 만들어서 처리하였다. FrontController에서 모든 요청을 받을것이고, 초기화 작업시 각각의 요청 uri에 맵핑되는 컨트롤러들을 map에 셋팅해준다. 그렇게 되면 실제 로직을 수행할때( "service(HttpServletRequest request, HttpServletResponse response) 함수" ) 요청 uri에따라 map에 등록한 컨트롤러 값을 가져와서 작업을 수행하도록 한다.
  
```java

public class FrontController extends HttpServlet { 
	HashMap<String, Controller> controllerUrls = null; 
	@Override 
	public void init(ServletConfig sc) throws ServletException {
		 controllerUrls = new HashMap<String, Controller>(); controllerUrls.put("/memberInsert.do", new MemberInterController()); 
		 controllerUrls.put("/memberDelete.do", new MemberDeleteController()); 
	}
	public void service(HttpServletRequest request, HttpServletResponse response) {
		 String uri = request.getRequestURI(); 
		 Controller subController = controllerUrls.get(uri);
		 subController.execute(request, response); 
	} 
}

출처: https://12bme.tistory.com/555 [길은 가면, 뒤에 있다.]

```

- 스프링에서는 이런 FrontController 역할을 DispatcherServlet이 해준다..
  - AbstractHandlerMappingd에서 핸들러(컨트롤러) 가져오고, 인터셉터 로직들을 지나서 해당 컨트롤러의 메소드로 이동한다
  - 해당 핸들러는 ModelAndView를 리턴한다(@ResponseBody 어노테이션이있으면 컨버터를 이용해서 바로 response전달)
  - view에 대한 정보가있으면 viewResolver에 들려 뷰객체를 얻고 뷰를 통해 렌더링하여 응답해준다


- 톰켓과 같은 was(서블릿컨테이너)에 의해 web.xml이 로드된다.. web.xml에는 어플리케이션컨텍스트를 생성하는 설정들(dispatcherServlet에 넘겨줄 servlet-context도 포함)이 있다. 보통 루트 웹 어플리케이션컨텍스트와 dispatcherSerlvet에서 사용하게될 어플리케이션컨텍스트가 있게되는데, 이 둘의 관계는 부모(루트)/자식이다..
  - 이런 위의 생성과정을 보면, DispatcherServlet은 was에 의해서 만들어지는것이다(스프링을 통해 빈으로 제어하는 오브젝트가 아니다). 다만, DispatcherServlet이 스프링을 통해 만든 빈들을 가진,applicationContext를 "사용"하는 개념이다..
  - 긍까 dispatcherServlet은 was와 스프링을 연결해주는 역할!(이라고 봐도 되겠지?)
  - https://12bme.tistory.com/555

- 정리 굿(서블릿에 대해 상세하게 설명) : https://12bme.tistory.com/555#:~:text=%EC%84%9C%EB%B8%94%EB%A6%BF%20%EC%BB%A8%ED%85%8C%EC%9D%B4%EB%84%88%EC%9D%98%20%EC%A2%85%EB%A5%98%EB%A1%9C,Interface%EB%A5%BC%20%EA%B5%AC%ED%98%84%ED%95%B4%EC%A4%98%EC%95%BC%20%ED%95%9C%EB%8B%A4.

