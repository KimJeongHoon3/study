package com.toby.spring;

public class GenericSample2 {
    public void setGenericSample(GenericSample<? extends GenericSuper,?> genericSample){ //제네릭변수 참조하는것에 대한 정의.. 와일드카드와 extends 는 setGenericSample 호출시 타입검증정도가 전부인듯..?
//        genericSample.test1(new GenericSuper()); //이건 안됨.. 왜 그런지는 모르겠음..


    }

    public void setGenericRef(GenericSample<GenericSuper,?> genericRef){
        genericRef.test1(new GenericSuper());
        setGenericSample(genericRef);
    }

    public void setGenericRefByGenericChild(GenericSample<GenericChild,?> genericRef){
        genericRef.test1(new GenericChild());
        setGenericSample(genericRef);
    }
}
