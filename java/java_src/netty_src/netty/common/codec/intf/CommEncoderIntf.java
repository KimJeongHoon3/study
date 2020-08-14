package com.biz.bgmsgw.netty.common.codec.intf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface CommEncoderIntf {
    void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception;
}
