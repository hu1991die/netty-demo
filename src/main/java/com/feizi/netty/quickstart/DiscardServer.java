package com.feizi.netty.quickstart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抛弃服务器
 * Created by feizi on 2018/5/10.
 */
public class DiscardServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscardServer.class);

    private final int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int port = 8888;
        if(null != args && args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        new DiscardServer(port).start();
    }

    /**
     * 启动服务
     * @throws Exception
     */
    public void start() throws Exception {
        //有2个 NioEventLoopGroup 会被使用
        /*第一个经常被叫做"boss", 用来接收进来的连接*/
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        /*第二个经常被叫做”worker“, 用来处理已经被接收的连接，一旦“boss”接收到连接，就会把连接信息注册到"worker"上*/
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            /*服务启动引导类*/
            ServerBootstrap bootstrap = new ServerBootstrap();
            /*用于处理ServerChannel和Channel的所有事件和IO*/
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /*将接收到的请求交给处理器进行处理*/
                                    .addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            /*绑定端口，开始接收连进来的连接*/
            ChannelFuture future = bootstrap.bind(port).sync();

            LOGGER.info(DiscardServer.class.getName() + " has been started SUCCESS and listen on: {}", future.channel().localAddress());

            /*等待服务器响应， socket关闭*/
            future.channel().closeFuture().sync();
        } finally {
            /*优雅关闭*/
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
