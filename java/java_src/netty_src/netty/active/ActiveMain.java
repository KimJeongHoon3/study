package com.biz.bgmsgw.netty.active;

import com.biz.bgmsgw.netty.common.CommChannelInitializer;
import com.biz.bgmsgw.netty.common.CommHandler;
import com.biz.bgmsgw.netty.common.CommProcInterface;
import com.biz.bgmsgw.netty.common.codec.intf.CommDecoderIntf;
import com.biz.bgmsgw.netty.common.codec.intf.CommEncoderIntf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class ActiveMain {
    private Bootstrap bootstrap;
    private NioEventLoopGroup nioEventLoopGroup;
    private CommHandler commHandler;
    private int port;
    private String ip;
    private Channel client;
    private CommProcInterface procInterface;
    private CommDecoderIntf decoder;
    private CommEncoderIntf encoder;
    private Logger logger= LoggerFactory.getLogger(ActiveMain.class);
    private boolean doWork=false;
    private boolean firstFlag=true;
    private int retryTime=300;

    public ActiveMain() {
        this.nioEventLoopGroup=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
    }

    public void setRetryConnectionTime(int retryTime){
        this.retryTime=retryTime;
    }

    public void setLogger(Logger logger){
        this.logger=logger;
    }

    public void setClientInfo(String ip,int port){
        this.ip=ip;
        this.port=port;
    }

    public void setProcInterface(CommProcInterface procInterface){
        this.procInterface=procInterface;
    }

    public void settingCodec(CommDecoderIntf decoder, CommEncoderIntf encoder){
        this.decoder=decoder;
        this.encoder=encoder;
    }

    public void start(){
        doWork=true;

        if(firstFlag){
            commHandler=new CommHandler();
            commHandler.setProcInterface(procInterface);
            CommChannelInitializer channelInitializer=new CommChannelInitializer(commHandler,decoder,encoder);
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
//                .remoteAddress(ip,port)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(channelInitializer);
            logger.info("Connecting... ip:"+ip+" ,port:"+port);
            firstFlag=false;
        }

        connectToClient();


//        while(doWork){
//            ChannelFuture future= null;
//            try {
//                logger.info("Connecting... ip:"+ip+" ,port:"+port);
//                future = bootstrap.connect().sync();
//                client=future.channel();
//                logger.info("Connected! ip:"+ip+" ,port:"+port);
//                future.channel().closeFuture().sync();
//                logger.info("disconnect... ip:"+ip+" ,port:"+port);
//            } catch (InterruptedException e) {
//                logger.error(e.getMessage(),e);
//                client.close();
//            }
//
//        }
    }
    ScheduledFuture<?> sf;
    private void connectToClient() {

        bootstrap.connect(ip,port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    future.channel().close();
                    sf=future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
//                            if(doWork==true){
                                if(retryTime>=10000){
                                    logger.info("Connecting... ip:"+ip+" ,port:"+port);
                                }
                                connectToClient();
//                            }
                        }
                    },retryTime, TimeUnit.MILLISECONDS );
                }else{
                    logger.info("Connected! ip:"+ip+" ,port:"+port);
                    client=future.channel();
                    future.channel().closeFuture().addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            logger.info("Disconnected... ip:"+ip+" ,port:"+port);
                            if(doWork==true){
                                if(retryTime<10000){
                                    logger.info("Connecting... ip:"+ip+" ,port:"+port);
                                }
                                connectToClient();
                            }

                        }
                    });
                }
            }
        });
    }

    public void dispose(){
        doWork=false;
        if(sf!=null) sf.cancel(true);
        if(client!=null) client.close();
    }

}
