package com.example.starim.big_work;

/**
 * Created by starim on 2017/12/25.
 */

public class User{
    String userID;
    String userPwd;
    public User(String userID,String userPwd){
        this.userID = userID;
        this.userPwd = userPwd;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}