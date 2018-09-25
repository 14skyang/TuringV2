package com.ysk.turingv2.bean;

import org.litepal.crud.LitePalSupport;

public class ChatHistory extends LitePalSupport {//聊天记录历史纪录实体类
    public static final int TYPE_RECEIVED = 0;//接收的数据
    public static final int TYPE_SENT = 1;//发送的数据
    private String time;
    private int id;
    private int type;
    private String chattext;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChattext() {
        return chattext;
    }

    public void setChattext(String chattext) {
        this.chattext = chattext;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
