package com.biz.netty.test.designpattern.proxy;

public class PersonBeanImpl implements PersonBean{
    String name;
    String gender;
    String interests;
    int rating;
    int ratingCount;

    @Override
    public String getName() { //all
        return name;
    }

    @Override
    public String getGender() { //all
        return gender;
    }

    @Override
    public String getInterests() { //all
        return interests;
    }

    @Override
    public int getHotOrNotRating() { //all
        if(ratingCount==0) return 0;
        return (rating/ratingCount);
    }

    @Override
    public void setName(String name) { //Owner
        this.name=name;
    }

    @Override
    public void setGender(String gender) { //owner
        this.gender=gender;
    }

    @Override
    public void setInterests(String interests) { //owner
        this.interests=interests;
    }

    @Override
    public void setHotOrNotRating(int rating) { //non-owner
        this.rating+=rating;
        ratingCount++;
    }
}
