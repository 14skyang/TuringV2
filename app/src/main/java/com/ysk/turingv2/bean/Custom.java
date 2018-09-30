package com.ysk.turingv2.bean;

import org.litepal.crud.LitePalSupport;

//自定义话术数据表
public class Custom extends LitePalSupport{
    private int id;
    private String sendtext;
    private String receivetext;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSendtext() {
        return sendtext;
    }

    public void setSendtext(String sendtext) {
        this.sendtext = sendtext;
    }

    public String getReceivetext() {
        return receivetext;
    }

    public void setReceivetext(String receivetext) {
        this.receivetext = receivetext;
    }
}
