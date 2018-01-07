package com.example.starim.big_work;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private RecyclerView myCardList;
    private TextView myName,yourName,myCount,yourCount;
    private ImageView cardImage;
    private Socket socket;
    private int port,myCountNum,yourCountNum,count=0,yourPreCard,myPreCard;
    private boolean isFirst,isYourTurn;
    private ArrayList<Card> cardList;
    private CardListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
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
        final ApplicationUtil appUtil = (ApplicationUtil)GameActivity.this.getApplication();
        socket = appUtil.getSocket();
        Intent preIntent = getIntent();
        isFirst = preIntent.getBooleanExtra("first",false);
        isYourTurn = isFirst;
        port = preIntent.getIntExtra("port",0);
        myCountNum = yourCountNum = 0;
        cardList = CardUtil.getCardList();
        adapter = new CardListAdapter(cardList){
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
                final ViewHolder holder = new ViewHolder(view);
                holder.cardView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallBack() {
                    @Override
                    public void onDoubleClick() {
                        if(isYourTurn){
                            int position = holder.getAdapterPosition();
                            isYourTurn = false;
                            Card card = data.get(position);
                            adapter.removeItem(position);
                            cardImage.setImageResource(card.getImageID());
                            if(card.getCardNumber()>1&&card.getCardNumber()<8){
                                myCountNum+=card.getCardNumber();
                                myPreCard = card.getCardNumber();
                            }
                            else if(card.getCardNumber()==1){
                                long firsTime = System.currentTimeMillis();
                                int i=0;
                                while(true){
                                    cardImage.setImageResource(R.drawable.c1+i);
                                    i++;
                                    i%=7;
                                    if(System.currentTimeMillis()-firsTime>1000)
                                        break;
                                }
                                card = CardUtil.getCard();
                                cardImage.setImageResource(card.getImageID());
                                myCountNum+=card.getCardNumber();
                                myPreCard = card.getCardNumber();
                            }
                            else if(card.getCardNumber()==8){
                                yourCountNum-=yourPreCard;
                                myPreCard = 0;
                            }
                            else if(card.getCardNumber()==9){
                                int temp = yourCountNum;
                                yourCountNum = myCountNum;
                                myCountNum = temp;
                                myPreCard = 0;
                            }
                            myCount.setText("我的分数"+myCountNum+"");
                            yourCount.setText("对手的分数"+yourCountNum+"");
                            count++;
                            //判断出牌次数
                            if((count==10&&!isFirst)||(myCountNum<yourCountNum)){//后手最后一张牌 或者分数小于对手 游戏结束
                                Thread thread = new Thread(new FinishGame(card));
                                thread.start();
                                //显示胜利或失败弹窗
                                AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
                                if(myCountNum<=yourCountNum)
                                {
                                    dialog.setTitle("提示").setMessage("失败");
                                }
                                else{
                                    dialog.setTitle("提示").setMessage("胜利");
                                }
                                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent nextIntent = new Intent(GameActivity.this,SecondActivity.class);
                                        nextIntent.putExtra("userID",myName.getText().toString());
                                        startActivity(nextIntent);
                                    }
                                });
                                dialog.show();
                            }
                            else{//游戏继续
                                Thread thread = new Thread(new GetOutCard(card));
                                thread.start();
                            }
                        }
                    }
                }));
                return holder;
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this){
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                detachAndScrapAttachedViews(recycler);
                for(int i = 0;i<getItemCount();i++){
                    View child = recycler.getViewForPosition(i);
                    addView(child);
                    measureChildWithMargins(child,0,0);
                    int width = getDecoratedMeasuredWidth(child);
                    int height = getDecoratedMeasuredHeight(child);
                    if(i==0){
                        layoutDecorated(child,0,0,width,height);
                    }
                    else{
                        layoutDecorated(child,100*i,0,width+100*i,height);
                    }
                }
            }
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if(isFirst){//判断是否先手
            myCardList.setLayoutManager(layoutManager);
        }
        else{
            myCardList.setEnabled(false);
            myCardList.setLayoutManager(layoutManager);
            //启动等待对手线程
            Thread thread = new Thread(new WaitFor());
            thread.start();
        }
        myCardList.setAdapter(adapter);
        myName.setText(preIntent.getStringExtra("myName"));
        yourName.setText(preIntent.getStringExtra("yourName"));
        yourCount.setText(0+"");
        myCount.setText(0+"");

    }
    private void initView(){
        myCardList = findViewById(R.id.card_list);
        myName = findViewById(R.id.myName);
        yourName = findViewById(R.id.yourName);
        myCount = findViewById(R.id.myCount);
        yourCount = findViewById(R.id.yourCount);
        cardImage = findViewById(R.id.card);
    }
    Handler getCard = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String obj = (String)msg.obj;
            Gson gson = new Gson();
            Log.d("msg",obj);
            JsonCard json = gson.fromJson(obj,JsonCard.class);
            Card card = json.getData();
            if(json.getCode()==16){//正常出牌
                cardImage.setImageResource(card.getImageID());
                if(card.getCardNumber()>1&&card.getCardNumber()<8){
                    yourCountNum+=card.getCardNumber();
                    yourPreCard = card.getCardNumber();
                }
                else if(card.getCardNumber()==8){
                    myCountNum-=myPreCard;
                    yourPreCard = 0;
                }
                else if(card.getCardNumber()==9){
                    int temp = yourCountNum;
                    yourCountNum = myCountNum;
                    myCountNum = temp;
                    yourPreCard = 0;
                }
                myCount.setText("我的分数"+myCountNum+"");
                yourCount.setText("对手的分数"+yourCountNum+"");
                isYourTurn = true;
            }
            else if(json.getCode()==17){//游戏结束
                cardImage.setImageResource(card.getImageID());
                if(card.getCardNumber()>1&&card.getCardNumber()<8){
                    yourCountNum+=card.getCardNumber();
                    yourPreCard = card.getCardNumber();
                }
                else if(card.getCardNumber()==8){
                    myCountNum-=myPreCard;
                    yourPreCard = 0;
                }
                else if(card.getCardNumber()==9){
                    int temp = yourCountNum;
                    yourCountNum = myCountNum;
                    myCountNum = temp;
                    yourPreCard = 0;
                }
                myCount.setText("我的分数"+myCountNum+"");
                yourCount.setText("对手的分数"+yourCountNum+"");
                isYourTurn = true;
                //判断胜负 显示胜利或失败弹窗
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
                if(myCountNum<yourCountNum)
                {
                    dialog.setTitle("提示").setMessage("失败");
                }
                else{
                    dialog.setTitle("提示").setMessage("胜利");
                }
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent nextIntent = new Intent(GameActivity.this,SecondActivity.class);
                        nextIntent.putExtra("userID",myName.getText().toString());
                        startActivity(nextIntent);
                    }
                });
                dialog.show();
            }
        }
    };
    class GetOutCard implements Runnable{
        private Card card;

        GetOutCard(Card card){
            this.card = card;
        }

        @Override
        public void run() {
            InputStream is;
            InputStreamReader isr;
            BufferedReader br;
            OutputStream os;
            PrintWriter pw;
            try{
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                os = socket.getOutputStream();
                pw = new PrintWriter(os);
                Gson gson = new Gson();
                JsonCard json = new JsonCard("Get Out Card",16,card);
                String res = gson.toJson(json);
                pw.println(res);
                pw.flush();
                String info;
                while ((info = br.readLine())==null) {
                }
                Message msg = getCard.obtainMessage();
                msg.obj = info;
                getCard.sendMessage(msg);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class WaitFor implements Runnable{
        @Override
        public void run() {
            InputStream is;
            InputStreamReader isr;
            BufferedReader br;
            try {
                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);
                String info;
                while((info = br.readLine())==null){
                }
                Message msg = getCard.obtainMessage();
                msg.obj = info;
                getCard.sendMessage(msg);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class FinishGame implements Runnable{
        private Card card;
        FinishGame(Card card){
            this.card = card;
        }
        @Override
        public void run() {
            OutputStream os;
            PrintWriter pw;
            try {
                os = socket.getOutputStream();
                pw = new PrintWriter(os);
                Gson gson = new Gson();
                JsonCard json = new JsonCard("finish",17,card);
                String res = gson.toJson(json);
                pw.println(res);
                pw.flush();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}