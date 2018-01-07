package com.example.starim.big_work;

import java.util.ArrayList;

/**
 * Created by starim on 2017/12/29.
 */

public class JsonHomeList {
    private String msg;
    private int code;
    private ArrayList<Home> data;

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

    public ArrayList<Home> getData() {
        return data;
    }

    public void setData(ArrayList<Home> homeWithIPS) {
        this.data = homeWithIPS;
    }
}
