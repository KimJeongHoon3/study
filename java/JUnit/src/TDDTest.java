package com.biz.netty.test;

import com.biz.netty.test.junittest.Bank;
import com.biz.netty.test.junittest.Expression;
import com.biz.netty.test.junittest.Money;
import com.biz.netty.test.junittest.Sum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TDDTest {

    @Test
    @DisplayName("dollar times 테스트")
    public void testMultiplication(){
        Money five=Money.dollar(5);
        assertEquals(Money.dollar(10),five.times(2));
        assertEquals(Money.dollar(15),five.times(3));

    }

    /**
     * 로직상 dollar와 동일하므로 지우자..
     * */
//    @Test
//    @DisplayName("franc times 테스트")
//    public void testFranMultiplication(){
//        Money five=Money.franc(5);
//        assertEquals(Money.franc(10),five.times(2)); //times에서 반환하는 객체가 Money이고, Money.franc에서 반환하는 객체는 Franc이라 다른데, 여기서 중요한것은 currency의 일치이기때문에 equals를 변경해야하고, 먼저 테스트를 새로이 만들어서 타입이 달라도 currency만 맞아도 equals가 맞는지 확인
//        assertEquals(Money.franc(15),five.times(3));
//
//    }

    @Test
    @DisplayName("객체 동등 테스트")
    public void testEquality(){
        assertTrue(Money.dollar(5).equals(Money.dollar(5)));
        assertFalse(Money.dollar(6).equals(Money.dollar(5)));
//        assertTrue(Money.franc(5).equals(Money.franc(5))); //위의 테스트와 동일
//        assertFalse(Money.franc(6).equals(Money.franc(5))); //위의 테스트와 동일
        assertFalse(Money.dollar(5).equals(Money.franc(5)));

    }

    @Test
    @DisplayName("통화 테스트")
    public void testCurrency(){
        assertEquals("USD",Money.dollar(1).currency());
        assertEquals("CHF",Money.franc(1).currency());
    }

    /**
     * Franc 객체는 사용하지않으므로 지워도될듯
     * */
//    @Test
//    @DisplayName("객체 비교시 통화가 동일하면 eqauls 가능하도록 테스트")
//    public void testDifferentClassEquality(){
//        assertTrue(new Money(10,"CHF").equals(new Franc(10,"CHF")));
//        assertFalse(new Money(10,"CHF").equals(new Franc(9,"CHF")));
//    }

    @Test
    @DisplayName("화폐 더하기 테스트")
    public void testSimpleAddition(){
        Money five=Money.dollar(5);

        Expression sum=five.plus(five); //Money가 아닌, Expression으로 뺄수있는 센스필요... 왜? 다중통화 연산과 같은경우를 고려하여 여러 환율도 표현하며, 산술연산도 표현할수있도록 하기위함

        Bank bank=new Bank();
        Money reduced=bank.reduce(sum,"USD");
        assertEquals(Money.dollar(10),reduced);
    }

    @Test
    public void testPlusReturnsSum(){
        Money five=Money.dollar(5);
        Expression result=five.plus(five);
        Sum sum=(Sum) result;
        assertEquals(five,sum.augend);
        assertEquals(five,sum.addend);
    }

    @Test
    public void testReduceSum(){
        Expression sum=new Sum(Money.dollar(3),Money.dollar(4));
        Bank bank=new Bank();
        Money result=bank.reduce(sum,"USD");
        assertEquals(Money.dollar(7),result);
    }

    @Test
    public void testReduceMoney(){
        Bank bank=new Bank();
        Money result=bank.reduce(Money.dollar(1),"USD");
        assertEquals(Money.dollar(1),result);

    }


    @Test
    public void testReduceMoneyDifferentCurrency(){
        Bank bank=new Bank();
        bank.addRate("CHF","USD",2); // 프랑/2 = 달러
        Money result=bank.reduce(Money.franc(2),"USD");
        assertEquals(Money.dollar(1),result);
    }

    @Test
    public void testMixedAddition(){
        Expression fiveBucks=Money.dollar(5);
        Expression tenFrancs=Money.franc(10);
        Bank bank=new Bank();
        bank.addRate("CHF","USD",2);
        Money result=bank.reduce(fiveBucks.plus(tenFrancs),"USD");
        assertEquals(Money.dollar(10),result);

    }

    @Test
    public void testSumPlusMoney(){
        Expression fiveBucks=Money.dollar(5);
        Expression tenFrancs=Money.franc(10);
        Bank bank=new Bank();
        bank.addRate("CHF","USD",2);
        Money result=bank.reduce(new Sum(fiveBucks,tenFrancs).plus(fiveBucks),"USD");
        assertEquals(Money.dollar(15),result);
    }

    @Test
    public void testSumTimes(){
        Expression fiveBucks=Money.dollar(5);
        Expression tenFrancs=Money.franc(10);
        Bank bank=new Bank();
        bank.addRate("CHF","USD",2);
        Expression sum=new Sum(fiveBucks,tenFrancs).times(2);
        Money result=bank.reduce(sum,"USD");

        assertEquals(Money.dollar(20),result);


    }

//    @Test  //통과는 못했지만 결국 필요없는 테스트
//    public void testPlusSameCurrencyReturnsMoney(){
//        Expression sum=Money.dollar(1).plus(Money.dollar(1));
//        assertTrue(sum instanceof Money);
//
//    }
}
