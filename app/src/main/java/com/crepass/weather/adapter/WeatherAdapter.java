package com.crepass.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crepass.weather.R;
import com.crepass.weather.item.WeatherItem;
import com.crepass.weather.common.WeatherHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {
    private int expandedPosition = -1;
    private List<WeatherItem> weatherItems;

    public WeatherAdapter(List<WeatherItem> weatherItems) {
        this.weatherItems = weatherItems;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_date_group, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem item = weatherItems.get(position);
        holder.groupTitle.setText(item.getGroupTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        String dayAfterTomorrowDate = dateFormat.format(item.getDate());

        holder.txtDate.setText(dayAfterTomorrowDate);
        holder.txtHighTemperature.setText(WeatherHelper.formatTemperature(item.getTmx()));
        holder.txtLowTemperature.setText(WeatherHelper.formatTemperature(item.getTmn()));

        if(item.getRainProbability()==1){
            holder.imgTemperature.setImageResource(R.drawable.sun);
        }else if(item.getRainProbability()==2){
            holder.imgTemperature.setImageResource(R.drawable.rain);

        }


        // 확장/축소 상태 설정
        final boolean isExpanded = position == expandedPosition;
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.btnExpand.setImageResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);

        holder.boxDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    expandedPosition = -1;
                    notifyItemChanged(position);
                } else {
                    int previousExpandedPosition = expandedPosition;
                    expandedPosition = position;
                    notifyItemChanged(previousExpandedPosition);
                    notifyItemChanged(position);

                    if (previousExpandedPosition != -1) {
                        // 부드럽게 스크롤
                        holder.itemView.getParent().requestChildFocus(holder.itemView, holder.itemView);
                    } else {
                        // 현재 항목을 부드럽게 스크롤
                        holder.itemView.getParent().requestChildFocus(holder.itemView, holder.itemView);
                    }
                }
            }
        });


        ChildWeatherAdapter childAdapter = new ChildWeatherAdapter(item.getChildItems());
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.childRecyclerView.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        return weatherItems.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {

        TextView groupTitle, txtDate, txtHighTemperature, txtLowTemperature;
        ImageView imgTemperature;
        LinearLayout expandableLayout, boxHigh, boxLow;
        RecyclerView childRecyclerView;
        ConstraintLayout boxDate;
        ImageView btnExpand;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            groupTitle = itemView.findViewById(R.id.groupTitle);
            txtHighTemperature = itemView.findViewById(R.id.txt_high_temperature);
            txtLowTemperature = itemView.findViewById(R.id.txt_low_temperature);
            txtDate = itemView.findViewById(R.id.txt_date);
            imgTemperature = itemView.findViewById(R.id.img_temperature);
            boxHigh = itemView.findViewById(R.id.box_high);
            boxLow = itemView.findViewById(R.id.box_low);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);
            boxDate = itemView.findViewById(R.id.box_date); // box_date 레이아웃
            btnExpand = itemView.findViewById(R.id.btn_expand);
        }
    }
}


