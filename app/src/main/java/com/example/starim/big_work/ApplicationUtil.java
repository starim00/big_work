package com.example.starim.big_work;

import android.app.Application;

import java.net.Socket;

/**
 * Created by starim on 2018/1/5.
 */

public class ApplicationUtil extends Application{
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
