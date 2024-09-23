package com.crepass.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crepass.weather.R;
import com.crepass.weather.item.WeatherItem;
import com.crepass.weather.retrofit.WeatherHelper;
import com.crepass.weather.retrofit.WeatherRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<WeatherItem> weatherItems;
    private WeatherCallback weatherCallback;
    public WeatherAdapter(List<WeatherItem> weatherItems, WeatherCallback weatherCallback) {
        this.weatherItems = weatherItems;
        this.weatherCallback = weatherCallback;
    }
    public interface WeatherCallback {
        void onWeatherRequested(String baseDate, String baseTime, String nx, String ny, int position);
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
        holder.txtDate.setText(item.getDate());

        // btn_expand 클릭 시 expandableLayout의 visibility를 변경
        holder.boxDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.expandableLayout.getVisibility() == View.GONE) {
                    holder.expandableLayout.setVisibility(View.VISIBLE);
                    holder.btnExpand.setBackgroundResource(R.drawable.arrow_up);

                    // 날짜에 따라 데이터를 가져옴
                    if (weatherCallback != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, position); // position에 따라 오늘, 내일, 모레 설정

                        String baseDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());
                        String baseTime = WeatherHelper.getBaseTime(); // 기본적인 발표 시간 사용
                        String nx = "60"; // X 좌표
                        String ny = "127"; // Y 좌표

                        weatherCallback.onWeatherRequested(baseDate, baseTime, nx, ny, position);
                    }

                } else {
                    holder.expandableLayout.setVisibility(View.GONE);
                    holder.btnExpand.setBackgroundResource(R.drawable.arrow_down);
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

        TextView groupTitle, txtDate;
        ImageView imgTemperature;
        LinearLayout expandableLayout, boxHigh, boxLow;
        RecyclerView childRecyclerView;
        ConstraintLayout boxDate;
        AppCompatImageButton btnExpand;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            groupTitle = itemView.findViewById(R.id.groupTitle);
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


