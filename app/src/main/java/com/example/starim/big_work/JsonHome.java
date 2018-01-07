package com.example.starim.big_work;

/**
 * Created by starim on 2017/12/25.
 */

public class JsonHome {
    private String msg;
    private int code;
    private Home data;

    public JsonHome(String msg,int code,Home data){
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

    public Home getData() {
        return data;
    }

    public void setData(Home data) {
        this.data = data;
    }
}
