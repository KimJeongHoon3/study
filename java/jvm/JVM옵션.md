# JVM옵션

1. -XX:-OmitStackTraceInFastThrow
   - hotspot JVM이 NullPointerException, ArithMethicException, ArrayIndexOutOfBoudsException, ClassCastException과 같은 implicit 예외들에 대해서 반복적으로 나타나면(사용자가 직접 명시한거말고) 이를 stack trace를 생략하는데(최적화를 위함이라함..), stact trace를 생략하지않으려면 해당 옵션을 넣어주어야한다!
   - 참고 사이트 : https://stackoverflow.com/questions/58696093/when-does-jvm-start-to-omit-stack-traces