package com.ysk.turingv2.bean;

/**
 * 描述: 聊天消息实体类，这个不是根据json数据来的
 * 实体类有两个属性，用来标识是左边接受消息布局(TYPE_RECEIVED)还是右边布局的的type属性（TYPE_SENT）
 */

public class Chat {
    public static final int TYPE_RECEIVED = 0;//接收的数据
    public static final int TYPE_SENT = 1;//发送的数据
    //    对话文本
    private String text;
    //    标示
    private int type;

    public Chat(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChatLiatData{" +
                "text='" + text + '\'' +
                ", type=" + type +
                '}';
    }
}

