package com.biz.bgmsgw.netty.common.codec;

import com.biz.bgmsgw.netty.common.codec.intf.CommEncoderIntf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class CommEncoder extends MessageToByteEncoder {
    private CommEncoderIntf commEncoderIntf;

    public CommEncoder(CommEncoderIntf commEncoderIntf) {
        this.commEncoderIntf=commEncoderIntf;
    }

    /*@Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        if(commEncoderIntf!=null){
            commEncoderIntf.encode(ctx,msg,out);
        }
    }*/

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(commEncoderIntf!=null){
            commEncoderIntf.encode(ctx,msg,out);
        }
    }
}
