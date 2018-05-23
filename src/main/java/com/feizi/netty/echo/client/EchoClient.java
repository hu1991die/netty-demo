package com.feizi.netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 客户端
 * Created by feizi on 2018/5/10.
 */
public class EchoClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EchoClient.class);

    /*主机IP*/
    private final String host;

    /*服务端口*/
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 客户端入口
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(null != args && args.length != 2){
            LOGGER.info("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>.");
            return;
        }

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        for (int i = 0; i < 5; i++){
            new EchoClient(host, port).start();
        }
        LOGGER.info("EchoClient has been started SUCCESS...");
    }

    /**
     * 客户端启动
     */
    public void start() throws Exception {
        /*客户端启动引导类*/
        Bootstrap bootstrap = new Bootstrap();
        /*线程池*/
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            LOGGER.info("EchoClient initChannle ch: {}", socketChannel);
                            socketChannel.pipeline()
                                    .addLast(new EchoClientHandler());
                        }
                    });

            /*客户端连接服务器*/
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
