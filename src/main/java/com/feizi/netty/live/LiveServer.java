package com.feizi.netty.live;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 长连接服务
 * Created by feizi on 2018/5/10.
 */
public class LiveServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveServer.class);

    /*服务端口*/
    private final int port;

    public LiveServer(int port) {
        this.port = port;
    }

    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(null != args && args.length != 1){
            LOGGER.debug("Usage: " + LiveServer.class.getSimpleName() + " <port>.");
            return;
        }

        final int port = Integer.parseInt(args[0]);
        new LiveServer(port).start();
        LOGGER.debug("LiveServer start with port: " + port);
    }

    /**
     * 服务启动
     */
    public void start() throws Exception {
        /*服务启动引导*/
        ServerBootstrap bootstrap = new ServerBootstrap();
        /*线程池*/
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        LOGGER.debug("LiveServer initChannel ch: " + socketChannel);
                        socketChannel.pipeline()
                                .addLast("decoder", new LiveDecoder())
                                .addLast("encoder", new LiveEncoder())
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                .addLast("handler", new LiveServerHandler());
                    }
                })
                //determining the number of connections queued
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        bootstrap.bind(port).sync();
    }
}
