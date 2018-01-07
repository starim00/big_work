package com.example.starim.big_work;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    Button game_join;
    Button game_create;
    TextView userID;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_second);
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
        Intent intent = getIntent();
        username = intent.getStringExtra("userID");
        userID.setText(username);
        game_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(SecondActivity.this,GameCreateActivity.class);
                nextIntent.putExtra("userID",username);
                startActivity(nextIntent);
            }
        });
        game_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(SecondActivity.this,GameJoinActivity.class);
                nextIntent.putExtra("userID",username);
                startActivity(nextIntent);
            }
        });
    }
    private void initView(){
        game_join = findViewById(R.id.game_join);
        game_create = findViewById(R.id.game_create);
        userID = findViewById(R.id.userID);
    }
}
