package com.example.starim.big_work;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Thread thread;
    private EditText userID;
    private EditText password;
    private Button login;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userID.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();    //控制输入的正确性
                }
                else{
                    thread = new Thread(new loginRunnable());
                    thread.start();     //启动登录线程
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }
    private void initView(){    //控件初始化
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        userID = findViewById(R.id.userID);
        password = findViewById(R.id.password);

    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String  object = (String)msg.obj;
            Gson gson = new Gson();
            Json json = gson.fromJson(object,Json.class);   //Gson解析json数据判断登录结果
            if(json.getCode()==1){      //判定登录成功，跳转登录后界面
                Intent intent =new Intent(MainActivity.this,SecondActivity.class);
                intent.putExtra("userID",userID.getText().toString());
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();//登录失败
            }
        }
    };
    class loginRunnable implements Runnable{
        @Override
        public void run() {
            String name = userID.getText().toString();
            String pwd = password.getText().toString();
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name",name));
            list.add(new BasicNameValuePair("pwd",pwd));
            try {
                String object = HttpReq.toPostdata(list,1);     //发起http登录请求
                Message message = handler.obtainMessage();
                message.obj = object;
                handler.sendMessage(message);       //调用handler处理
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
