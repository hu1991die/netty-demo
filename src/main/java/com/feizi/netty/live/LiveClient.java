package com.feizi.netty.live;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * 长连接客户端测试
 * Created by feizi on 2018/5/10.
 */
public class LiveClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiveClient.class);

    String host = "localhost";
    int port = 8888;

    public void testLive() throws Exception {
        LOGGER.debug("======start test======");

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));

        LOGGER.debug("please input code :");
        Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            while (true){
                try {
                    byte[] input = new byte[64];
                    int readByte = socket.getInputStream().read(input);
                    LOGGER.debug("readByte: {}", readByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        int code;
        while (true) {
            code = scanner.nextInt();
            LOGGER.debug("input code: {}", code);
            if (code == 0) {
                break;
            } else if (code == 1) {
                //分配内存空间
                ByteBuffer byteBuffer = ByteBuffer.allocate(5);
                byteBuffer.put((byte) 1);
                byteBuffer.putInt(0);
                socket.getOutputStream().write(byteBuffer.array());
                LOGGER.debug("write heart package finish...");
            } else if (code == 2) {
                byte[] content = ("hello, I'm" + hashCode()).getBytes();
                ByteBuffer byteBuffer = ByteBuffer.allocate(content.length + 5);
                byteBuffer.put((byte) 2);
                byteBuffer.putInt(content.length);
                byteBuffer.put(content);
                socket.getOutputStream().write(byteBuffer.array());
                LOGGER.debug("write content package finish...");
            }
        }
        socket.close();

        LOGGER.debug("======end test======");
    }

    public static void main(String[] args) throws Exception {
        new LiveClient().testLive();
    }
}
