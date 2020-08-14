package com.biz.bgmsgw.netty.common.codec;

import com.biz.bgmsgw.netty.common.codec.intf.CommDecoderIntf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class CommDecoder extends ByteToMessageDecoder {
    private CommDecoderIntf commDecoderIntf;

    public CommDecoder(CommDecoderIntf commDecoderIntf) {
        this.commDecoderIntf = commDecoderIntf;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(commDecoderIntf !=null){
            commDecoderIntf.decode(ctx,in,out);
        }
    }


}
