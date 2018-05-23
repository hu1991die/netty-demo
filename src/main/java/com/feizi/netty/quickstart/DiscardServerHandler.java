package com.feizi.netty.quickstart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * handler是由Netty生成用来处理I/O事件的
 * Created by feizi on 2018/5/10.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscardServerHandler.class);

    /**
     * 每当从客户端收到新的请求数据时，这个方法会在收到消息时被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*丢弃收到的数据消息*/
        /*默默丢弃收到的消息数据*/
//        ((ByteBuf)msg).release();

        /*查看接收到的数据消息*/
        /*try {
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()){
                System.out.print((char)in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }*/

        /*服务端进行应答消息*/
        /*ctx.write(msg);
        ctx.flush();*/
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(null != cause){
            cause.printStackTrace();
        }

        if(null != ctx){
            ctx.close();
        }
    }
}
