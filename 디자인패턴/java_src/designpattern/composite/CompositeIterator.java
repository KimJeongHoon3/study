package com.biz.netty.test.designpattern.composite;

import java.util.Iterator;
import java.util.Stack;

public class CompositeIterator implements Iterator<MenuComponent> { // 데코레이터 패턴과 유사하네..
    private Stack<Iterator<MenuComponent>> stack=new Stack<>(); //여기에 두 종류의 객체가 들어감..

    public CompositeIterator(Iterator<MenuComponent> iterator){
        stack.push(iterator);
    }

    @Override
    public boolean hasNext() {
//        Iterator<MenuComponent> iterator=stack.peek();
//        if(iterator!=null && stack.size()>0){
//            return true;
//        }
//        return false;
        if(stack.empty()){
            return false;
        }else{
            Iterator<MenuComponent> iterator=stack.peek(); // 여기서는 두종류.. compositeIetator 이거나 ArrayListIterator 이거나
            if(!iterator.hasNext()){    // 결국에는 arrayListIterator를 체크하게되어있음..
                stack.pop();
                return hasNext();
            }else{
                return true;
            }
        }
    }

    @Override
    public MenuComponent next() {
        /*MenuComponent menuComponent=null;
        if(hasNext()){
            Iterator<MenuComponent> iterator=stack.peek();
            if(iterator.hasNext()){
                menuComponent=iterator.next();
                if(menuComponent instanceof Menu){
                    stack.push(menuComponent.getIterator());
                }
            }else{
                stack.pop();
            }

        }
        return menuComponent;*/

        if(hasNext()){
            Iterator<MenuComponent> iterator = stack.peek();
            MenuComponent next = iterator.next();
            if(next instanceof Menu){
                stack.push(next.getIterator());
            }

            return next;
        }else{
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
