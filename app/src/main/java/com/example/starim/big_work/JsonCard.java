package com.example.starim.big_work;

/**
 * Created by starim on 2018/1/5.
 */

public class JsonCard {
    private String msg;
    private int code;
    private Card data;

    public JsonCard(String msg,int code,Card data){
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Card getData() {
        return data;
    }

    public void setData(Card data) {
        this.data = data;
    }
}
