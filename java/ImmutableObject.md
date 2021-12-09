# 불변격체(Immutable Object)
- 불변객체는 재할당은 가능하지만, 한번 할당하면 내부 데이터를 변경할 수 없는 객체
- 대표적인 예로는 String, Integer, Boolean 등
- 장점
  - 멀티스레드에 안전
  - 객체에 대한 신뢰도 높아짐. 객체가 한번 생성되어서 그게 변하지않는다면 트랜잭션 내에서 그 객체가 변하지않기에 우리가 믿고 쓸수있음
- 단점
  - 객체가 가지는 값마다 새로운 객체가 필요.. 즉 지속적인 메모리 차지..(GC에 부담이 갈수도..)

- 불변 객체 예
    ```java
        class ImmutablePerson {
            private final int age; //final이 핵심
            private final int name;
            
            public ImmutablePerson(int age, int name) {
                this.age = age;
                this.name = name;
            }

            //setter메소드 없음
        }

        //여기서 중요한것은 원시타입일 경우에는 상관없으나, 참조타입일 경우에는 참조타입 또한 불변이어야함!(참조타입을 final로 선언해봤자 안에 내용을 변경할수있으니까)

        //Array일 경우
        public class ArrayObject {
            private final int[] array;

            public ArrayObject(final int[] array) {
                this.array = Arrays.copyOf(array,array.length);
            }

            public int[] getArray() {
                return (array == null) ? null : array.clone();
            }
        }

        //List일 경우
        public class ListObject {

            private final List<Animal> animals;

            public ListObject(final List<Animal> animals) {
                this.animals = new ArrayList<>(animals);
            }

            public List<Animal> getAnimals() {
                return Collections.unmodifiableList(animals);
            }
        }

    ```


- 참고사이트
  - https://velog.io/@conatuseus/Java-Immutable-Object%EB%B6%88%EB%B3%80%EA%B0%9D%EC%B2%B4