package com.example.studmy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeViewHolder> {

    ArrayList<Like> like = new ArrayList<>();

    public LikeAdapter(ArrayList<Like> like) {
        this.like = like;
    }

    @NonNull

     // Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
    @Override
    public LikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new LikeViewHolder(view);
    }
    //Заполнение виджетов View данными из элемента списка с номером position
    @Override
    public void onBindViewHolder(@NonNull LikeViewHolder holder, int position) {
        holder.name_text.setText(like.get(position).getName_like());
        holder.address_text.setText(like.get(position).getAddress_like());
    }
    //возвращает общее количество элементов списка. Значения списка передаются с помощью конструктора.
    @Override
    public int getItemCount() {
        return like.size();
    }

    class LikeViewHolder extends RecyclerView.ViewHolder {

        TextView name_text;
        TextView address_text;

        public LikeViewHolder(View view) {
            super(view);
            name_text = view.findViewById(R.id.name_like);
            address_text = view.findViewById(R.id.address_like);
        }
    }
}
