package com.feizi.netty.live;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息编码
 * Created by feizi on 2018/5/10.
 */
public class LiveEncoder extends MessageToByteEncoder<LiveMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, LiveMessage liveMessage, ByteBuf byteBuf) throws Exception {
        LOGGER.info("encode the message to byte array...");
        byteBuf.writeByte(liveMessage.getType());
        byteBuf.writeInt(liveMessage.getLength());

        if(!StringUtil.isNullOrEmpty(liveMessage.getContent())){
            byteBuf.writeBytes(liveMessage.getContent().getBytes());
        }
    }
}
