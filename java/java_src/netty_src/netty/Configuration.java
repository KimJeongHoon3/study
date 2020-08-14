package com.biz.bgmsgw.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public NioEventLoopGroup getNioEventLoopGroup(){
        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    }

    @Bean
    public TaskScheduler getTaskScheduler(){
        return new ThreadPoolTaskScheduler();
    }
}
