package com.biz.netty.test.junittest;

import java.util.Objects;

public class Pair {

    private String from;
    private String to;

    public Pair(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        Pair pair=(Pair)o;
        return pair.from==from && pair.to==to;

//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Pair pair = (Pair) o;
//        return Objects.equals(from, pair.from) &&
//                Objects.equals(to, pair.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
