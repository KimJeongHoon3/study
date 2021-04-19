package com.biz.netty.test.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheTest {
    ConcurrentHashMap<String,CacheData> cacheDataConcurrentHashMap=new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        cacheDataConcurrentHashMap.put("cache",new CacheData("hi","value"));
    }

    @Cacheable("test1")
    public CacheData cacheTest(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new CacheData("hi","value");
    }

    public CacheData cacheTest2(){

        CacheData cacheData=new CacheData("hi","value");
        CacheData cacheData1=cacheDataConcurrentHashMap.putIfAbsent("cache",cacheData);
        if(cacheData1==null){
            cacheData1=cacheData;
        }

        return cacheData1;
    }
}
