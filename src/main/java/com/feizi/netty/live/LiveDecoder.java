package com.feizi.netty.live;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 消息解码
 * Created by feizi on 2018/5/10.
 */
public class LiveDecoder extends ReplayingDecoder<LiveDecoder.LiveState>{

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveDecoder.class);

    public enum LiveState{
        TYPE,
        LENGTH,
        CONTENT
    }

    private LiveMessage message;

    public LiveDecoder() {
        super(LiveState.TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        LiveState state = state();
        LOGGER.debug("start decode, state: {}, message: {}", state, message);
        switch (state){
            case TYPE:
                message = new LiveMessage();
                byte type = in.readByte();
                LOGGER.info("type: " + type);
                message.setType(type);
//                out.add(message);
                checkpoint(LiveState.LENGTH);
                break;
            case LENGTH:
                int length = in.readInt();
                message.setLength(length);
                if(length > 0){
                    checkpoint(LiveState.CONTENT);
                }else {
                    out.add(message);
                    checkpoint(LiveState.TYPE);
                }
                break;
            case CONTENT:
                byte[] bytes = new byte[message.getLength()];
                in.readBytes(bytes);
                String content = new String(bytes);
                message.setContent(content);
                out.add(message);
                checkpoint(LiveState.TYPE);
                break;
            default:
                throw new IllegalArgumentException("invalid state: " + state);
        }
        LOGGER.info("end decode, state: {}, out: {}", state, out);
    }
}
