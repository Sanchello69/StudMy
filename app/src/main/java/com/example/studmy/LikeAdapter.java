package com.example.studmy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studmy.models.Like;

import java.util.ArrayList;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.LikeViewHolder> {

    private Context context;

    ArrayList<Like> like = new ArrayList<>();

    public LikeAdapter(ArrayList<Like> like, Context context) {
        this.like = like;
        this.context = context;
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
            //вешаем слушателя, обрабатываем клик и предлагаем открыть это место на карте
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String uri = "geo:0,0?q=" + like.get(position).getName_like() + ' ' + like.get(position).getAddress_like();
                    Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(mapIntent);
                }
            });
        }
    }
}
