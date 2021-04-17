equals(), hashcode(), ==연산자비교 
- "equals()" : deafault로는 primitive type은 내용이 같은지비교, reference type은 객체의 주소가 같은지 검사.. 해당 함수가 오버라이딩되면 그에 맞춰서 비교 
- "==연산자" : primitive타입일때는 값이 같은지, reference type에는 객체의 주소가 같은지 검사 
- "hashcode()" : 메모리에서 가진 hash주소값을 기본적으로반환..(메모리 주소라고 생각해도무방하긴함). 하지만 다른 객체여도 같을수있기때문에 비교에 애매한부분이있다..!(String은 new 키워드로 같은 내용을 생성하면, hashCode는 같은데 객체는 다름)

https://jeong-pro.tistory.com/172