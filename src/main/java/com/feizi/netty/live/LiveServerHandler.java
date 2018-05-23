package com.feizi.netty.live;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求处理器
 * Created by feizi on 2018/5/10.
 */
public class LiveServerHandler extends SimpleChannelInboundHandler<LiveMessage>{
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveServerHandler.class);

    private static Map<Integer, LiveChannelCache> channelCacheMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LiveMessage msg) throws Exception {
        Channel channel = ctx.channel();
        final int hashCode = channel.hashCode();
        LOGGER.debug("channel hashCode: {}, msg: {}, cache: {}", hashCode, msg, channelCacheMap.toString());

        if(!channelCacheMap.containsKey(hashCode)){
            LOGGER.debug("channelCacheMap.containsKey(hashCode), put key: {}", hashCode);
            channel.closeFuture().addListener(future -> {
                LOGGER.debug("channel close, remove key: {}", hashCode);
                channelCacheMap.remove(hashCode);
            });

            ScheduledFuture scheduledFuture = ctx.executor().schedule(() -> {
                LOGGER.debug("schedule runs, close channel: {}", hashCode);
                channel.close();
            }, 10, TimeUnit.SECONDS);

            channelCacheMap.put(hashCode, new LiveChannelCache(channel, scheduledFuture));
        }

        switch (msg.getType()){
            case LiveMessage.TYPE_HEART:
                LiveChannelCache cache = channelCacheMap.get(hashCode);
                ScheduledFuture future = ctx.executor().schedule(
                        () -> channel.close(), 5, TimeUnit.SECONDS);
                cache.getScheduledFuture().cancel(true);
                cache.setScheduledFuture(future);
                ctx.channel().writeAndFlush(msg);
                break;
            case LiveMessage.TYPE_MESSAGE:
                channelCacheMap.entrySet().stream().forEach(entry -> {
                    Channel ch = entry.getValue().getChannel();
                    ch.writeAndFlush(msg);
                });
                break;
            default:
                throw new IllegalArgumentException("Invalid msg: " + msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("channel read complete...");
        super.channelReadComplete(ctx);
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
