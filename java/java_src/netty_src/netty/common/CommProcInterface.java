package com.biz.bgmsgw.netty.common;

import io.netty.channel.ChannelHandlerContext;

public interface CommProcInterface {
    void sessionOpened(ChannelHandlerContext ctx);

    void sessionClosed(ChannelHandlerContext ctx);

    String messageReceived(ChannelHandlerContext ctx, Object msg);

    void sessionIdle(ChannelHandlerContext ctx, Object evt);

    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

    public default void sendMessage(ChannelHandlerContext ctx, byte[] msg)
    {
        if(ctx ==null)
            return ;
        ctx.write(msg);
        ctx.flush();
    }
}
