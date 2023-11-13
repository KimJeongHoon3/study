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

