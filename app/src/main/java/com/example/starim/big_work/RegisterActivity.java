package com.example.starim.big_work;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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


public class RegisterActivity extends AppCompatActivity {
    Button check;
    EditText userID;
    EditText password1;
    EditText password2;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
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
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!password1.getText().toString().equals(password2.getText().toString())){
                    Toast.makeText(getApplicationContext(), "密码输入不一致", Toast.LENGTH_SHORT).show();
                }
                else if(password1.getText().toString().isEmpty()||password2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                }
                else if(userID.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();   //控制输入正确性
                }
                else{
                    thread = new Thread(new registerRunable());//启动注册线程
                    thread.start();
                }
            }
        });

    }
    private void initView(){
        check = findViewById(R.id.OK);
        userID = findViewById(R.id.userID);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String  object = (String)msg.obj;
            Gson gson = new Gson();     //Gson解析json判断注册结果
            Json json = gson.fromJson(object,Json.class);
            if(json.getCode()==1){
                AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("注册成功，将为你登录");
                dialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(RegisterActivity.this,SecondActivity.class);
                        intent.putExtra("userID",userID.getText().toString());
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
            else{
               if(json.getMsg().equals("user already have")){
                   Toast.makeText(getApplicationContext(), "用户名已存在", Toast.LENGTH_SHORT).show();
               }
               else {
                   Toast.makeText(getApplicationContext(), "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
               }
        }
        }
    };
    class registerRunable implements Runnable{
        @Override
        public void run() {
            String name = userID.getText().toString();
            String pwd = password1.getText().toString();
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name",name));
            list.add(new BasicNameValuePair("pwd",pwd));
            try {
                String object = HttpReq.toPostdata(list,2);//发起注册进程
                Message message = handler.obtainMessage();
                message.obj = object;
                handler.sendMessage(message);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
