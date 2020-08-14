package com.biz.bgmsgw.netty.passive;

import com.biz.bgmsgw.netty.common.CommChannelInitializer;
import com.biz.bgmsgw.netty.common.CommHandler;
import com.biz.bgmsgw.netty.common.CommProcInterface;
import com.biz.bgmsgw.netty.common.codec.intf.CommDecoderIntf;
import com.biz.bgmsgw.netty.common.codec.intf.CommEncoderIntf;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class PassiveMain {
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private int port;
    private String ip;
    private ServerBootstrap serverBootstrap;
    private CommProcInterface proc;
    private CommHandler handler;
    private ChannelFuture future;
    private Logger logger= LoggerFactory.getLogger(PassiveMain.class);
    private CommDecoderIntf decoder;
    private CommEncoderIntf encoder;

    public PassiveMain() {}

    public void setLogger(Logger logger){
        this.logger=logger;
    }

    public void setCommProcInterface(CommProcInterface proc){
        this.proc=proc;
    }

    public void setPort(int port){
        this.port=port;
    }

    public void setIp(String ip){this.ip=ip;}

    public void settingCodec(CommDecoderIntf decoder, CommEncoderIntf encoder){
        this.decoder=decoder;
        this.encoder=encoder;
    }

    public void start(){
        handler=new CommHandler();
        handler.setProcInterface(proc);
        CommChannelInitializer channelInitializer=new CommChannelInitializer(handler,decoder,encoder);
        serverBootstrap=new ServerBootstrap();
        bossGroup =new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        workerGroup=new NioEventLoopGroup();

        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);
        if(ip==null || ip.equals("")){
            future=serverBootstrap.bind(port);
        }else{
            future=serverBootstrap.bind(ip,port);
        }

        logger.info("Open Passive Server! Ip : "+ip+" ,Port : "+port);
    }

    public void dispose() throws Exception{
        if(future!=null) future.channel().close();
        if(workerGroup!=null) workerGroup.shutdownGracefully().get(10, TimeUnit.SECONDS);
        if(bossGroup!=null) bossGroup.shutdownGracefully().get(10, TimeUnit.SECONDS);
    }

}
