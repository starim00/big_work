package com.example.starim.big_work;

/**
 * Created by starim on 2017/12/27.
 */

public class Home {
    private String homeName;
    private boolean hasPwd;
    private String homePwd;
    private String homeOwner;
    private int port;

    public Home(String homeName,String homePwd,String homeOwner,int port){
        this.homeName = homeName;
        hasPwd = true;
        this.homePwd = homePwd;
        this.homeOwner = homeOwner;
        this.port = port;
    }
    public Home(String homeName,String homeOwner,int port){
        this.homeName = homeName;
        hasPwd = false;
        this.homePwd = null;
        this.homeOwner = homeOwner;
        this.port = port;
    }
    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public boolean isHasPwd() {
        return hasPwd;
    }

    public void setHasPwd(boolean hasPwd) {
        this.hasPwd = hasPwd;
    }

    public String getHomePwd() {
        return homePwd;
    }

    public void setHomePwd(String homePwd) {
        this.homePwd = homePwd;
    }

    public String getHomeOwner() {
        return homeOwner;
    }

    public void setHomeOwner(String homeOwner) {
        this.homeOwner = homeOwner;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

