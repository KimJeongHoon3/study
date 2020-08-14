package com.biz.bgmsgw.netty.common;

import com.biz.bgmsgw.netty.common.codec.CommDecoder;
import com.biz.bgmsgw.netty.common.codec.CommEncoder;
import com.biz.bgmsgw.netty.common.codec.intf.CommDecoderIntf;
import com.biz.bgmsgw.netty.common.codec.intf.CommEncoderIntf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


public class CommChannelInitializer extends ChannelInitializer<SocketChannel> {
    private CommHandler handler;
    private CommDecoderIntf decoder;
    private CommEncoderIntf encoder;

    public CommChannelInitializer(CommHandler handler, CommDecoderIntf decoder, CommEncoderIntf encoder) {
        this.handler = handler;
        this.decoder = decoder;
        this.encoder = encoder;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline=ch.pipeline();
        pipeline.addLast(new IdleStateHandler(30,0,0));
        pipeline.addLast("decoder",new CommDecoder(decoder));
        pipeline.addLast("encoder",new CommEncoder(encoder));
        pipeline.addLast("handler",handler);
    }
}
