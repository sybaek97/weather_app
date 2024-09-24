package com.crepass.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crepass.weather.item.ChildItem;
import com.crepass.weather.R;
import com.crepass.weather.common.WeatherHelper;

import java.util.List;

public class ChildWeatherAdapter extends RecyclerView.Adapter<ChildWeatherAdapter.ChildViewHolder> {

    private List<ChildItem> childItems;

    public ChildWeatherAdapter(List<ChildItem> childItems) {
        this.childItems = childItems;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_time_group, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        ChildItem item = childItems.get(position);
        holder.txtTime.setText(WeatherHelper.formatTime(item.getTime()));
        holder.txtTemperature.setText(item.getTemperature()+"°C");
        holder.txtRainPercent.setText(item.getRainPercent()+"%");

        if(item.getIconRes()==1){
            holder.imgIcon.setImageResource(R.drawable.sun);


        }else if(item.getIconRes()==2){
            holder.imgIcon.setImageResource(R.drawable.rain);

        }
    }

    @Override
    public int getItemCount() {
        return childItems.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView txtTime, txtTemperature, txtRainPercent;
        ImageView imgIcon;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txt_time);
            txtTemperature = itemView.findViewById(R.id.txt_temperature);
            txtRainPercent = itemView.findViewById(R.id.txt_percent_2);
            imgIcon = itemView.findViewById(R.id.img_temperature);
        }
    }
}

