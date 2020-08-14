package com.biz.bgmsgw.netty.common.codec.intf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public interface CommDecoderIntf {
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
}
