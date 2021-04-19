package com.biz.netty.test.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

@EnableCaching
@Configuration
public class CacheConfig {

    @Primary
    @Bean("cacheManager")
    public CompositeCacheManager cacheManager() {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
        compositeCacheManager.setFallbackToNoOpCache(true);
        compositeCacheManager.setCacheManagers(Collections.singleton(ehCacheManager()));
        return compositeCacheManager;
    }

    @Bean("ehCacheManager")
    public EhCacheCacheManager ehCacheManager() {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(getCustomCacheManager());
        return ehCacheCacheManager;
    }

    private CacheManager getCustomCacheManager() {
        CacheManager cacheManager = CacheManager.create(getEhCacheConfiguration());
        cacheManager.addCache(createCache("test1"));
        cacheManager.addCache(createCache("test2"));
        return cacheManager;
    }

    private Cache createCache(String cacheName) {
        CacheConfiguration cacheConfig = new CacheConfiguration(cacheName, 1000)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .eternal(false)
                .timeToLiveSeconds(3600)
                .timeToIdleSeconds(3600);
        return new Cache(cacheConfig);
    }

    private net.sf.ehcache.config.Configuration getEhCacheConfiguration() {
        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
        diskStoreConfiguration.setPath("java.io.tmpdir");
        configuration.addDiskStore(diskStoreConfiguration);
        return configuration;
    }

}
