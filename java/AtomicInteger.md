AtomicInteger

- java5 의 concurrent 패키지에 있음
- 원자성을 보장해주는것으로, 스레드에 안전 (low level의 cpu 동작임.. 별도 synchronization 필요x)
- compare-and-swap 방식
  - java 에서 native method를 호출하게되는데, 아래와 같음
```java
    /**
    * An {@code int} value that may be updated atomically.  See the
    * {@link java.util.concurrent.atomic} package specification for
    * description of the properties of atomic variables. An
    * {@code AtomicInteger} is used in applications such as atomically
    * incremented counters, and cannot be used as a replacement for an
    * {@link java.lang.Integer}. However, this class does extend
    * {@code Number} to allow uniform access by tools and utilities that
    * deal with numerically-based classes.
    *
    * @since 1.5
    * @author Doug Lea
    */
    public class AtomicInteger extends Number implements java.io.Serializable {
        private static final long serialVersionUID = 6214790243416807050L;

        // setup to use Unsafe.compareAndSwapInt for updates
        private static final Unsafe unsafe = Unsafe.getUnsafe();

        ...

        /**
        * Atomically increments by one the current value.
        *
        * @return the updated value
        */
        public final int incrementAndGet() {
            return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
        }
    }


    public final class Unsafe {

        ...

        public final int getAndAddInt(Object o, long offset, int delta) { 
            int v;
            do {
                v = getIntVolatile(o, offset); // v는 현재 값으로 보면됨
            } while (!compareAndSwapInt(o, offset, v, v + delta));  // delta는 추가할 값인데, 여기서는 1이 될것이다. 즉, 현재값이 현재값+1이 될때까지 계속 루프를 돌고, 성공하면 루프를 빠져나오게된다.. 이게 compare-and-swap
            return v;
        }

        ...
    }
    
```

- [참고하면 괜찮은 사이트](https://stackoverflow.com/questions/9749746/what-is-the-difference-between-atomic-volatile-synchronized)