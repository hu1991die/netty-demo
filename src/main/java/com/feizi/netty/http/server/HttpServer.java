package com.feizi.netty.http.server;

import com.feizi.netty.http.handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http服务器
 * Created by feizi on 2018/5/9.
 */
public class HttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    /*端口号*/
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Usage: " + HttpServer.class.getSimpleName() + " <port>.");
            return;
        }

        int port = Integer.parseInt(args[0]);
        new HttpServer(port).start();
        LOGGER.info("http server start SUCCESS...");
    }

    /**
     * 启动服务端
     */
    public void start(){
        /*服务端启动引导类*/
        ServerBootstrap bootstrap = new ServerBootstrap();
        /*可以理解为一个线程池，内部维护了一组线程，每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样就可以回避多线程下的数据同步问题*/
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            LOGGER.info("initChannel ch: " + socketChannel);
                            socketChannel.pipeline()
                                    /*对http请求进行解码*/
                                    .addLast("decoder", new HttpRequestDecoder())
                                    /*对response响应进行编码*/
                                    .addLast("encoder", new HttpResponseEncoder())
                                    /*消息聚合器 参数512 * 1024代表聚合的消息长度不超过512kb*/
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1034))
                                    /*处理Http请求逻辑*/
                                    .addLast("handler", new HttpHandler());
                        }
                    })
                    /*指定队列连接数*/
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            LOGGER.error("http server start failure...{}", e);
        }
    }
}
