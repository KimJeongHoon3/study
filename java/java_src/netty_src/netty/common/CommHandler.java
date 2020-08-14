package com.biz.bgmsgw.netty.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class CommHandler extends ChannelInboundHandlerAdapter {
    CommProcInterface commProcInterface;

    public void setProcInterface(CommProcInterface commProcInterface){
        this.commProcInterface=commProcInterface;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(commProcInterface!=null){
            commProcInterface.sessionOpened(ctx);
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(commProcInterface!=null){
            commProcInterface.sessionClosed(ctx);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(commProcInterface!=null){
            String ret=commProcInterface.messageReceived(ctx,msg);
            if(ret!=null){
                ctx.writeAndFlush(ret);
            }
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(commProcInterface!=null){
            commProcInterface.sessionIdle(ctx,evt);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(commProcInterface!=null){
            commProcInterface.exceptionCaught(ctx,cause);
        }
        ctx.close();
    }


}
