package com.feizi.netty.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * echo服务器端
 * Created by feizi on 2018/5/10.
 */
public class EchoServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);

    /*服务器端端口*/
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(null != args && args.length != 1){
            System.out.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
            return;
        }

        final int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    /**
     * 启动服务器
     */
    public void start() throws Exception {
        /*服务端启动引导类*/
        ServerBootstrap bootstrap = new ServerBootstrap();
        /*线程池*/
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            LOGGER.info("EchoServer initChannle ch: {}", socketChannel);
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            LOGGER.info(EchoServer.class.getName() + " has been started SUCCESS and listen on: {}", future.channel().localAddress());
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
