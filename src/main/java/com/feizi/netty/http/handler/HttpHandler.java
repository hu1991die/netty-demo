package com.feizi.netty.http.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http处理器
 * Created by feizi on 2018/5/9.
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

    /*定义请求的内容类型(如：text/plain; charset=UTF-8)*/
    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;

    /**
     * 读取请求
     * @param ctx 上下文
     * @param request http请求对象
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        LOGGER.info("request class is: " + request.getClass().getName());

        /*定义响应对象*/
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                /*超文本传输协议HTTP/1.1版本*/
                HttpVersion.HTTP_1_1,
                /*正常响应200 ok*/
                HttpResponseStatus.OK,
                /*携带的内容*/
                Unpooled.wrappedBuffer("test http request".getBytes())
        );

        HttpHeaders httpHeaders = response.headers();
        /*请求的内容类型: 如文本类型*/
        httpHeaders.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        /*请求传输的报文长度*/
        httpHeaders.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        /*在http1.1中request和reponse header中都有可能出现一个connection的头，此header的含义是当client和server通信时对于长链接如何进行处理*/
        httpHeaders.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    /**
     * 读取完毕
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("read complete...");
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    /**
     * 读取过程中的异常捕获处理
     * @param ctx 上下文
     * @param cause 异常
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("read encounter a exception...");
        if(null != cause){
            /*打印异常堆栈信息*/
            cause.printStackTrace();
        }

        if(null != ctx){
            /*关闭上下文*/
            ctx.close();
        }
    }
}
