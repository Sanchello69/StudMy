package com.vas.studmy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.MarkerOptions;
import com.vas.studmy.models.Like;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

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
        return new LikeViewHolder(view, context);
    }
    //Заполнение виджетов View данными из элемента списка с номером position
    @Override
    public void onBindViewHolder(@NonNull final LikeViewHolder holder, int position) {
        holder.name_text.setText(like.get(position).getName_like());
        holder.address_text.setText(like.get(position).getAddress_like());
        holder.cardView.setVisibility(View.GONE);
    }
    //возвращает общее количество элементов списка. Значения списка передаются с помощью конструктора.
    @Override
    public int getItemCount() {
        return like.size();
    }
    //вызывается для освобождения ресурсов
    @Override
    public void onViewRecycled(@NonNull LikeViewHolder holder) {
        if (holder.gMap != null)
        {
            holder.gMap.clear();
            holder.gMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            holder.click = !holder.click;
        }
    }

    class LikeViewHolder extends RecyclerView.ViewHolder{

        TextView name_text;
        TextView address_text;
        CardView cardView;
        CardView cardView_item;
        boolean click = true;

        GoogleMap gMap;
        MapView map;

        public LikeViewHolder(View itemView, final Context c) {
            super(itemView);
            name_text = itemView.findViewById(R.id.name_like);
            address_text = itemView.findViewById(R.id.address_like);
            cardView_item = itemView.findViewById(R.id.card_list_item);
            cardView = itemView.findViewById(R.id.card_map);
            //cardView.setVisibility(View.GONE); //скрываем cardview с мини картой
            map = (MapView) itemView.findViewById(R.id.map_lite);

            cardView_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (click){
                        final int position = getAdapterPosition();
                        if (map != null) {
                            map.onCreate(null);
                            map.onResume();
                            map.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    MapsInitializer.initialize(c.getApplicationContext());
                                    map.setClickable(false);
                                    gMap = googleMap;
                                    LatLng coordinates = new LatLng(like.get(position).
                                            getLatitude_like(), like.get(position).getLongitude_like());
                                    googleMap.addMarker(new MarkerOptions().position(coordinates));
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 15f);
                                    googleMap.moveCamera(cameraUpdate);
                                }
                            });
                            cardView.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                        cardView.setVisibility(View.GONE);
                    click=!click;
                }
            });

            //вешаем слушателя, обрабатываем клик и предлагаем открыть это место на карте
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String uri = "geo:0,0?q=" + like.get(position).getName_like() + ' ' + like.get(position).getAddress_like();
                    Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    c.startActivity(mapIntent);
                }
            });
        }
    }
}
