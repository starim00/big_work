package com.example.starim.big_work;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HomeActivity extends AppCompatActivity {
    TextView myName,yourName,homeName;
    Button check;
    Socket socket;
    Intent intent;
    Thread thread;
    int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
        initView();
        intent = getIntent();
        //设置房间名和用户名
        homeName.setText(intent.getStringExtra("homeName"));
        if(intent.getStringExtra("type").equals("Create")){
            myName.setText(intent.getStringExtra("userID"));
            check.setText(R.string.game_start);
            check.setClickable(false);
            thread = new Thread(new socketRunnable());  //启动房间创建的线程
            thread.start();
        }
        else{
            yourName.setText(intent.getStringExtra("userID"));
            myName.setText(intent.getStringExtra("homeOwn"));
            check.setText(R.string.ready);
            port = intent.getIntExtra("port",0);
            thread = new Thread(new JoinHome());
            thread.start();     //启动加入房间线程发送加入房间信息
        }
    }
    private void initView(){
        myName = findViewById(R.id.myName);
        yourName = findViewById(R.id.yourName);
        homeName = findViewById(R.id.home_name);
        check = findViewById(R.id.check);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {    //房间创建的处理
            String object = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",object);
            JsonHome json = gson.fromJson(object,JsonHome.class);
            if(json.getCode()==11){
                Toast.makeText(getApplicationContext(), "房间创建成功", Toast.LENGTH_SHORT).show();
                port = json.getData().getPort();
                thread = new Thread(new KeepHome());
                thread.start();     //启动线程监听加入房间的信息
            }
        }
    };
    Handler memberJoin = new Handler(){     //房间加入的处理
        @Override
        public void handleMessage(Message msg) {
            String obj = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",obj);
            JsonUser json = gson.fromJson(obj,JsonUser.class);
            if(json.getCode() == 13){
                if(json.getMsg().equals("Join")){   //判定返回为加入房间的信息
                    //获取并设定另外一玩家的情况，启动监听准备消息的线程
                    yourName.setText(json.getData());
                    thread = new Thread(new WaitForReady());
                    thread.start();
                }
                else{
                    check.setOnClickListener(new View.OnClickListener() {   //判定为确认加入信息
                        @Override
                        public void onClick(View view) {    //设置准备按钮的点击事件
                            thread = new Thread(new ReadyHome());
                            thread.start();     //启动线程发送准备消息
                        }
                    });
                }
            }

        }
    };
    Handler memberReady = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String obj = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",obj);
            JsonUser json = gson.fromJson(obj,JsonUser.class);
            if(json.getCode()==15){     //接受到游戏开始信号，跳转到开始游戏
                ApplicationUtil appUtil = (ApplicationUtil)HomeActivity.this.getApplication();
                appUtil.setSocket(socket);
                Intent nextIntent = new Intent(HomeActivity.this,GameActivity.class);
                nextIntent.putExtra("port",port);
                nextIntent.putExtra("myName",yourName.getText().toString());
                nextIntent.putExtra("yourName",myName.getText().toString());
                nextIntent.putExtra("first",true);
                startActivity(nextIntent);
            }
        }
    };
    Handler readyToStart = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String obj = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",obj);
            JsonUser json = gson.fromJson(obj,JsonUser.class);
            if(json.getCode()==14){
                check.setClickable(true);
                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//设定游戏开始按钮点击事件
                        thread = new Thread(new GameStart());
                        thread.start();
                    }
                });
            }
        }
    };
    Handler gameStart = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int code = (int)msg.obj;
            if(code == 1){
                ApplicationUtil appUtil = (ApplicationUtil)HomeActivity.this.getApplication();
                appUtil.setSocket(socket);
                Intent nextIntent = new Intent(HomeActivity.this,GameActivity.class);
                nextIntent.putExtra("port",port);
                nextIntent.putExtra("myName",myName.getText().toString());
                nextIntent.putExtra("yourName",yourName.getText().toString());
                nextIntent.putExtra("first",false);
                startActivity(nextIntent);
            }
        }
    };
    class socketRunnable implements Runnable{
        @Override
        public void run() {
            try{
                socket = new Socket(HttpPath.getSocketIP(),6666);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                InputStream is ;
                String json;
                if(intent.getStringExtra("type").equals("Create")){
                    if(intent.getBooleanExtra("isPwd",false)){
                        String pwd = intent.getStringExtra("pwd");
                        Home home = new Home(homeName.getText().toString(),pwd,myName.getText().toString(),0);
                        JsonHome jsonHome = new JsonHome("Create Home",11,home);
                        json = new Gson().toJson(jsonHome);
                        Log.d("json",json);
                    }
                    else{
                        Home home = new Home(homeName.getText().toString(),myName.getText().toString(),0);
                        JsonHome jsonHome= new JsonHome("Create Home",11,home);
                        json = new Gson().toJson(jsonHome);
                        Log.d("json",json);
                    }
                    pw.println(json);
                    pw.flush();
                    is = socket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String info;
                    while ((info = br.readLine())==null){
                    }
                    Message msg = handler.obtainMessage();
                    msg.obj = info;
                    handler.sendMessage(msg);
                    br.close();
                }

            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class KeepHome implements Runnable{
        @Override
        public void run() {
            try {
                socket = new Socket(HttpPath.getSocketIP(),port);
                Gson gson = new Gson();
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                PrintWriter pw = new PrintWriter(os);
                while(true){
                    String res;
                    while((res = br.readLine()) == null){
                    }
                    JsonUser member = gson.fromJson(res,JsonUser.class);
                    if(member.getCode()==13){
                        JsonUser user = new JsonUser("Join Success",13,myName.getText().toString());
                        String json = gson.toJson(user);
                        pw.println(json);
                        pw.flush();
                        Message msg = memberJoin.obtainMessage();
                        msg.obj = res;
                        memberJoin.sendMessage(msg);
                        break;
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class JoinHome implements Runnable{
        @Override
        public void run() {
            try{
                socket = new Socket(HttpPath.getSocketIP(),port);
                JsonUser join = new JsonUser("Join",13,yourName.getText().toString());
                Gson gson = new Gson();
                String json = gson.toJson(join);
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                PrintWriter pw = new PrintWriter(os);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                pw.println(json);       //发送加入房间的信息
                pw.flush();
                String res;
                while((res=br.readLine())==null){
                }
                Message msg = memberJoin.obtainMessage();
                msg.obj = res;
                memberJoin.sendMessage(msg);        //接收返回的信息并交由handler处理
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class ReadyHome implements Runnable{
        @Override
        public void run() {
            try{
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                InputStream is =socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                JsonUser user = new JsonUser("Ready",14,yourName.getText().toString());
                Gson gson = new Gson();
                String json = gson.toJson(user);
                pw.println(json);
                pw.flush();
                String info;
                while ((info = br.readLine())==null){
                }
                Message msg = memberReady.obtainMessage();
                msg.obj = info;
                memberReady.sendMessage(msg);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class WaitForReady implements Runnable{
        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String info;
                while((info = br.readLine())==null){
                }
                Message msg = readyToStart.obtainMessage();
                msg.obj = info;
                readyToStart.sendMessage(msg);
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
    }
    class GameStart implements Runnable{
        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                JsonUser json = new JsonUser("Start Game",15,myName.getText().toString());
                Gson gson = new Gson();
                String res = gson.toJson(json);
                pw.println(res);
                pw.flush();
                Message msg = gameStart.obtainMessage();
                msg.obj = 1;
                gameStart.sendMessage(msg);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
