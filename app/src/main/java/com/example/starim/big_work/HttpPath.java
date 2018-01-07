package com.example.starim.big_work;

/**
 * Created by starim on 2017/12/23.
 */

public class HttpPath {
    private static final String IP = "http://120.79.154.14:8080/";
    private static final String socketIP = "120.79.154.14";

    public static String getUserLoginPath(){
        return IP+"AndroidSevers-1.0-SNAPSHOT/app_userlogin";
    }
    public static String getUserRegister(){
        return IP+"AndroidSevers-1.0-SNAPSHOT/app_userregister";
    }
    public static String getSocketIP(){
        return socketIP;
    }
}
