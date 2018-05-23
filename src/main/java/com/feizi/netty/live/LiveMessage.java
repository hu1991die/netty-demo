package com.feizi.netty.live;

/**
 * 消息封装
 * Created by feizi on 2018/5/10.
 */
public class LiveMessage {

    static final byte TYPE_HEART = 1;
    static final byte TYPE_MESSAGE = 2;

    /*消息类型*/
    private byte type;
    /*消息长度*/
    private int length;
    /*消息体内容*/
    private String content;

    public LiveMessage() {
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LiveMessage{" +
                "type=" + type +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }
}
