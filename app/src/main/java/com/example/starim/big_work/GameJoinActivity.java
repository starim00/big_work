package com.example.starim.big_work;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class GameJoinActivity extends AppCompatActivity {
    private ArrayList<Home> home = new ArrayList<>();
    Button refresh;
    RecyclerView homeList;
    String username;
    Socket socket;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_join);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }       //设置全屏显示
        initView();
        Intent preIntent = getIntent();
        username = preIntent.getStringExtra("userID");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread = new Thread(new getHomeList());
                thread.start();
            }
        });
        thread = new Thread(new getHomeList());
        thread.start();
    }
    private void initView(){
        refresh = findViewById(R.id.refresh);
        homeList = findViewById(R.id.home_list);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String object = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",object);
            JsonHomeList json = gson.fromJson(object,JsonHomeList.class);
            home = json.getData();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(home,username);
            homeList.setLayoutManager(layoutManager);
            homeList.setAdapter(adapter);
        }
    };
    class getHomeList implements Runnable{
        @Override
        public void run() {
            try {
                socket = new Socket(HttpPath.getSocketIP(),6666);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                InputStream is;
                String json;
                Json json1 = new Json();
                json1.setCode(12);
                json1.setMsg("Get HomeList");
                json = new Gson().toJson(json1);
                Log.d("json",json);
                pw.println(json);
                pw.flush();
                is = socket.getInputStream();
                BufferedReader br =new BufferedReader(new InputStreamReader(is));
                String info;
                while ((info = br.readLine())==null){
                }
                Message msg =handler.obtainMessage();
                msg.obj = info;
                handler.sendMessage(msg);
                br.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
