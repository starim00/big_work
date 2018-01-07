package com.example.starim.big_work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by starim on 2017/12/28.
 */

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>{
    ArrayList<Home> datas = null;
    String userName;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View homeView;
        TextView homeName;
        Button join;
        public ViewHolder(View view){
            super(view);
            homeView = view;
            homeName = view.findViewById(R.id.home_name);
            join = view.findViewById(R.id.game_join);
        }
    }
    public HomeRecyclerViewAdapter(ArrayList<Home> datas,String userName){
        this.datas = datas;
        this.userName = userName;
    }
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                final Home home = datas.get(position);
                if(home.isHasPwd()==false){
                    Intent intent = new Intent(view.getContext(),HomeActivity.class);
                    intent.putExtra("type","join");
                    intent.putExtra("userID",userName);
                    intent.putExtra("homeName",home.getHomeName());
                    intent.putExtra("homeOwn",home.getHomeOwner());
                    intent.putExtra("port",home.getPort());
                    view.getContext().startActivity(intent);
                }
                else{
                    final EditText editText = new EditText(view.getContext());
                    final View vv = view;
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    dialog.setTitle("提示").setView(editText);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(editText.getText().toString().equals(home.getHomePwd())){
                                Intent intent = new Intent(vv.getContext(),HomeActivity.class);
                                intent.putExtra("type","join");
                                intent.putExtra("userID",userName);
                                intent.putExtra("homeName",home.getHomeName());
                                intent.putExtra("homeOwn",home.getHomeOwner());
                                intent.putExtra("port",home.getPort());
                                vv.getContext().startActivity(intent);
                            }
                            else{
                                Toast.makeText(vv.getContext(), "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Home home = datas.get(position);
        holder.homeName.setText(home.getHomeName());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
