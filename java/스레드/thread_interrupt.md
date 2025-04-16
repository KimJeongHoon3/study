thread_interrupt

- Thread의 interrupt를 호출하는것은 스레드에 대한 중단을 이야기함. 
- 이를 호출하면 스레드의 인터럽트 상태가 변경됨.
  - java8에서는 native 메서드로 관리가 되는데, java11?(17?)에서는 그냥 필드+native메서드로 관리되는듯함
- 스레드가 wait, join, sleep 등을 호출하고있는 상태에서 interrupt가 호출되면, 인터럽트상태가 초기화되고 InterruptedException이 던져짐
- `Thread.currentThread().isInterrupted()` 는 단순히 현재 스레드의 Interrupt 상태값을 반환해줌
- `Thread.interrupted()` 를 사용하면, 현재 스레드 Interrupt 상태값을 반환해주면서, 인터럽트 상태를 초기화 해준다
  - queue에서 사용하고 있는 부분
    - LinkedTransferQueue는 아래와 같이 take 메서드 호출시 interrupt가 호출되면 아래와 같이 사용됨
      ```java
        // LinkedTransferQueue 내부
        public E take() throws InterruptedException {
            E e = xfer(null, false, SYNC, 0);
            if (e != null)
                return e;
            Thread.interrupted();
            throw new InterruptedException();
        }
      ``` 
    - queue의 구현체마다 다르겠지만, ReentrantLock을 사용하는 queue에서는 ReentrantLock의 lockInterruptibly 메서드를 사용해서 현재 스레드의 인터럽트 상태를 확인해서 lock을 잡음. 인터럽트되어있으면 InterruptedException 발생.. 
      - 요게 보니 java.util.concurrent.locks 패캐지 클래스에서 interrupt 관련 처리들이 잘 되어있는듯.. (ex. await)

- effective java(item81) 에서 나온 관용어구
    ```java
         static long time(Executor executor, int concurrency, Runnable runnable) throws InterruptedException {
            CountDownLatch ready = new CountDownLatch(concurrency);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(concurrency);

            for (int i = 0; i < concurrency; i++) {
                executor.execute(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        runnable.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 여기!! 여기로 들어왔을때 Thread의 인터럽트상태는 이미 초기화되어있다.(보통 InterruptedException 던질때 Thread.interrupted()호출해서 초기화해주는듯) 그래서 이렇게 잡게되면 다시 인터럽트 상태를 변경해주어야한다
                    } finally {
                        done.countDown();
                    }

                });
            }


            ready.await();
            long startNanos = System.nanoTime();
            start.countDown();
            done.await();

            return System.nanoTime() - startNanos;
        }
    ```
    - [추가참고](https://stackoverflow.com/questions/20934817/why-thread-currentthread-interrupt-be-called)

---

- 코파일럿 설명
```md

`InterruptedException`를 catch 블록 안에서 처리할 때 `Thread.currentThread().interrupt();`를 호출하는 주된 이유는 **인터럽트 상태(interrupted status)를 복원하기 위해서**입니다. 이게 왜 필요한지 좀 더 자세히 설명해볼게요.

1. **인터럽트 상태는 클리어됨**  
   자바에서 스레드가 인터럽트되면, 인터럽트가 블로킹 메서드(예: `Thread.sleep()`, `Object.wait()` 등)에서 감지되어 `InterruptedException`이 발생합니다. 이 예외가 던져지는 순간, 자바 런타임은 해당 스레드의 인터럽트 플래그를 자동으로 클리어합니다.  
   - 즉, catch 블록에 진입할 때는 스레드에 "인터럽트 신호가 있었다"는 정보가 사라진 상태입니다.

2. **인터럽트 신호 저장 및 전달**  
   catch 블록 내에서 `Thread.currentThread().interrupt();`를 호출하면, 클리어된 인터럽트 플래그를 다시 설정하게 됩니다.  
   - 이렇게 함으로써 이 스레드가 인터럽트되었음을 상위 로직이나 후속 처리 코드가 감지할 수 있게 됩니다.
   - 만약 인터럽트 플래그를 재설정하지 않는다면, 이후에 `Thread.interrupted()` 같은 메서드 호출에서 인터럽트가 발생했다는 사실을 알 수 없게 되어, 의도치 않게 인터럽트 신호가 "소실"되는 문제를 초래할 수 있습니다.

3. **인터럽트 정책 준수**  
   대부분의 경우, 스레드 인터럽트를 감지한 후에는 스레드의 실행을 중단하거나 적절하게 처리하는 것이 좋습니다.  
   - 이미 catch 블록 내에서 일부 처리(예: 로깅, 정리 작업 등)를 진행한 후에도, 상위 메서드나 스레드 관리자가 인터럽트 상태를 확인해서 그에 맞는 추가 동작(예: 리소스 해제, 스레드 종료 등)을 수행할 수 있도록 하는 것이 바람직합니다.

4. **예시 코드**  
   아래 코드는 `InterruptedException`을 catch할 때 인터럽트 상태를 재설정하는 예시입니다:
   
   ```java
   try {
       // 블로킹 작업: 예를 들어, sleep
       Thread.sleep(1000);
   } catch (InterruptedException e) {
       // InterruptedException 발생 시, 인터럽트 상태를 복원합니다.
       Thread.currentThread().interrupt();
       // 추가적으로, 필요한 클린업 작업 등을 수행할 수 있습니다.
       System.out.println("스레드가 인터럽트되었습니다.");
   }
   ```

결론적으로, `Thread.currentThread().interrupt();`를 호출하는 것은 catch 블록이 인터럽트 신호를 "먹어치우는" 것을 방지하고, 이후에 상위 로직이나 다른 코드에서 해당 상태를 인지할 수 있도록 하기 위함입니다. 이는 스레드 안전성과 예외 처리를 보다 견고하게 만드는 중요한 습관입니다.

---

더 알아보고 싶은 경우, 스레드 인터럽트 처리 정책, 인터럽트 플래그 사용과 같은 주제에 대해 이야기해봐도 좋을 것 같아요. 예를 들어, **어떻게 하면 스레드 종료나 자원 해제를 제대로 처리할 수 있을지, 그리고 회사나 프로젝트에서 스레드 안전성을 높이기 위한 다른 방법들**에 대해 논의해보면 좋겠네요.


```