package com.example.starim.big_work;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by starim on 2018/1/3.
 */

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {
    ArrayList<Card> data;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View cardView;
        ImageView imageView;
        public ViewHolder(View view){
            super(view);
            cardView = view;
            imageView = view.findViewById(R.id.card_image);
        }

    }
    public CardListAdapter(ArrayList<Card> datas){
        this.data = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallBack() {
            @Override
            public void onDoubleClick() {

            }
        }));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Card card = data.get(position);
        holder.imageView.setImageResource(card.getImageID());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

}
