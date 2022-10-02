- nio 정리 <span style="color:yellow">(추가정리필요!!!!!)</span>
  - 기존 Java I/O에서의 JVM 내부 메모리로의 복사문제를 해결하기 위해 NIO에서는 커널 버퍼에 직접 접근할 수 있는 클래스를 제공해줍니다. Buffer클래스들이 그것.. 내부적으로 커널버퍼를 직접 참조
  - c나 c++로 만들어진 Server Program은 Thread를 생성하지 않고도 많은 수의 클라이언트를 처리할 수 있습니다. 이를 가능케 해주는 것이 OS 레벨에서 지원하는 Scatter/Gather 기술과 Select() 시스템 콜입니다. Scatter/Gather은 시스템콜의 수를 줄이는 기술인데요, 덕분에 I/O를 빠르게 만들 수 있죠. c나 c++에서는 이런 OS수준의 기술들을 이용하여 I/O속도를 향상시켜왔지만 java에서는 이런 시스템에서 제공하는 기술을 사용할 수 있는 방법이 없었죠. 하지만 NIO에서는 가능합니다. 이런 것을 가능하게 해주는 Class가 바로 Channel과 Selector입니다.
  - NIO의 Channel은 Buffer에 있는 내용을 다른 어디론가 보내거나 다른 어딘가에 있는 내용을 Buffer로 읽어들이기 위해 사용됩니다. 예를 들면 네트워크 프로그래밍을 할 때 Socket을 통해 들어온 내용을 ByteBuffer에 저장하기 위해서나, ByteBuffer로 Packet을 작성 후 Socket으로 흘려 보낼 때 Channel을 사용합니다. 이런 Channel을 ServerSocketChannel 이나 Socket Channel 이라고 합니다. ServerSocketChannel이나 SocketChannel의 경우 Selector를 이용하여 Non-Blocking 하게 입출력을 수행 할 수 있지만, FileChannel은 Blocking만 가능합니다. 이 점은, 운영체제나 시스템 마다 File 입출력시 Non-Blocking을 지원해주지 않는 시스템이 있어 그런 것이라고 합니다. FileChannel은 Blocking 모드만 가능합니다! 
  - FileChannel은 바로 File에 있는 내용을 ByteBuffer로 불러오거나 ByteBuffer에 있는 내용을 File에 쓰는 역할을 합니다.
  - Channel은 직접 인스턴스화 할 수가 없음. 직접 생성자를 이용해서 인스턴스화하는 것이 아니라, OutputStream이나 InputStream에서 getChannel() 메소드를 이용하여 만들어내야 합니다. 
- 아래내용 정리할것
  - channel은 buffer를 사용하는데, buffer를 사용할때 DMA 엔진을 사용하여 데이터를 변경하지않고 바로 전달할때 불필요한 복사가 없어져서 빠르게 전달가능하다(커널영역에서)
    - directbuffer(운영체제 buffer에 직접접근 - 제로카피 가능) 할수도, allocate(그냥 jvm heap을 사용)를 사용할수도있음
    - netty는 이를 또 사용하기 쉽게 ByteBuf로 만들어놨음.. ex. flip 필요없음..
    - directBuffer를 쓰는거랑 non-blocking이랑은 별개이야기.. 아래에 있는 링크중 두번쨰링크는 NIO를 사용해야하는것이 non-blocking이 안될수도있지만, DirectBuffer를 사용함에 성능개선이 된다는 이야기함
  - NIO에서 기존에 사용못했던 System Call을 간접적으로 사용가능하게 해주기떄문에 빠름! ex. select(), Scatter/Gather (요건 잘 모르겠음)
    -  Channel과 Selector 클래스에서 사용가능
https://homoefficio.github.io/2019/02/27/Java-NIO-Direct-Buffer를-이용해서-대용량-파일-행-기준으로-쪼개기/
https://homoefficio.github.io/2016/08/06/Java-NIO%EB%8A%94-%EC%83%9D%EA%B0%81%EB%A7%8C%ED%81%BC-non-blocking-%ED%95%98%EC%A7%80-%EC%95%8A%EB%8B%A4/
http://eincs.com/2009/08/java-nio-bytebuffer-channel-file/
http://eincs.com/2009/08/java-nio-bytebuffer-channel/
http://eincs.com/2009/09/compare-allocate-allocatedirect-method-of-bytebuffer/

- [NIO 간결하고 깔끔하게 소스랑 설명 굿](https://engineering.linecorp.com/ko/blog/do-not-block-the-event-loop-part2)