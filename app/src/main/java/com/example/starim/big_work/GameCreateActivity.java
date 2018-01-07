package com.example.starim.big_work;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GameCreateActivity extends AppCompatActivity {

    EditText homename,password;
    RadioGroup haspwd;
    RadioButton pwd_true,pwd_false;
    Button ok;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_create);
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
        Intent preIntent = getIntent();
        userID = preIntent.getStringExtra("userID");
        password.setFocusable(false);
        password.setFocusableInTouchMode(false);    //初始密码框无法输入
        haspwd.check(pwd_false.getId());    //设置默认选中无密码
        haspwd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==pwd_true.getId()){
                    password.setFocusableInTouchMode(true);
                    password.setFocusable(true);
                    password.requestFocus();
                }
                else if(i==pwd_false.getId()){
                    password.setFocusable(false);
                    password.setFocusableInTouchMode(false);
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameCreateActivity.this,HomeActivity.class);
                intent.putExtra("type","Create");
                if(haspwd.getCheckedRadioButtonId()==pwd_false.getId()){
                    intent.putExtra("isPwd",false);
                }
                else{
                    intent.putExtra("isPwd",true);
                    intent.putExtra("pwd",password.getText().toString());
                }
                intent.putExtra("homeName",homename.getText().toString());
                intent.putExtra("userID",userID);
                startActivity(intent);
            }
        });
    }
    private void initView(){
        homename = findViewById(R.id.home_name);
        password = findViewById(R.id.password);
        haspwd = findViewById(R.id.pwdRadioGroup);
        pwd_true = findViewById(R.id.pwd_true);
        pwd_false = findViewById(R.id.pwd_false);
        ok = findViewById(R.id.OK);
    }
}
