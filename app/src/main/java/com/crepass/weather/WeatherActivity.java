package com.crepass.weather;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crepass.weather.adapter.WeatherAdapter;
import com.crepass.weather.databinding.ActivityWeatherBinding;
import com.crepass.weather.item.WeatherItem;
import com.crepass.weather.retrofit.WeatherHelper;
import com.crepass.weather.retrofit.WeatherRepository;
import com.crepass.weather.retrofit.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
    private TextView txtTemperature;
    private ActivityWeatherBinding binding;
    private WeatherAdapter weatherAdapter;
    private List<WeatherItem> weatherItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일", Locale.getDefault());
        weatherItems = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        String todayDate = dateFormat.format(calendar.getTime());
        weatherItems.add(new WeatherItem("오늘", todayDate, new ArrayList<>()));

        // 내일 날짜 설정
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowDate = dateFormat.format(calendar.getTime());
        weatherItems.add(new WeatherItem("내일", tomorrowDate, new ArrayList<>()));

        // 모레 날짜 설정
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String dayAfterTomorrowDate = dateFormat.format(calendar.getTime());
        weatherItems.add(new WeatherItem("모레", dayAfterTomorrowDate, new ArrayList<>()));

        weatherAdapter = new WeatherAdapter(weatherItems, new WeatherAdapter.WeatherCallback() {
            @Override
            public void onWeatherRequested(String baseDate, String baseTime, String nx, String ny, int position) {
                WeatherHelper.fetchWeatherData(baseDate, baseTime, nx, ny,700, new WeatherRepository.WeatherCallback() {
                    @Override
                    public void onSuccess(WeatherResponse weatherResponse) {
                        if (weatherResponse != null && weatherResponse.response != null &&
                                weatherResponse.response.body != null && weatherResponse.response.body.items != null &&
                                weatherResponse.response.body.items.itemList != null) {

                            List<WeatherResponse.Item> itemList = weatherResponse.response.body.items.itemList;
                            List<WeatherData> filteredData = new ArrayList<>();

                            for (WeatherResponse.Item item : itemList) {
                                // 온도(TMP)와 강수확률(POP)만 필터링
                                if ("TMP".equals(item.category) || "POP".equals(item.category)) {
                                    filteredData.add(new WeatherData(item.fcstDate,item.category, item.fcstTime, item.fcstValue));
                                }
                            }

                            // 필터링된 데이터 로그 출력
                            for (WeatherData data : filteredData) {
                                Log.d("Filtered Data", data.toString());
                            }

                        } else {
                            Log.e("WeatherActivity", "API 응답 데이터가 올바르지 않습니다.");
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("WeatherActivity", "날씨 데이터 가져오기 실패: " + errorMessage);
                    }
                });
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(weatherAdapter);
        txtTemperature = binding.txtTemperature;

        // 현재 날짜와 발표 시간 설정
        String baseDate = WeatherHelper.getCurrentDate();
        String baseTime = WeatherHelper.getBaseTime();

        // 격자 좌표 설정 (필요한 지역에 맞게 설정)
        String nx = "60"; // X 좌표
        String ny = "127"; // Y 좌표

        // 초기 API 호출
        WeatherHelper.fetchWeatherData(baseDate, baseTime, nx, ny,700, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                if (weatherResponse != null &&
                        weatherResponse.response != null &&
                        weatherResponse.response.body != null &&
                        weatherResponse.response.body.items != null &&
                        weatherResponse.response.body.items.itemList != null) {

                    List<WeatherResponse.Item> itemList = weatherResponse.response.body.items.itemList;

                    if (!itemList.isEmpty()) {
                        String temperature = null;
                        for (WeatherResponse.Item item : itemList) {
                            if (item.category.equals("TMP")) {
                                temperature = item.fcstValue;
                                break;
                            }
                        }
                        if (temperature != null) {
                            txtTemperature.setText(temperature + "°C");
                        } else {
                            txtTemperature.setText("기온 데이터 없음");
                        }
                    } else {
                        txtTemperature.setText("데이터가 없습니다.");
                    }
                } else {
                    txtTemperature.setText("API 응답 데이터가 올바르지 않습니다.");
                    Log.e("WeatherActivity", "응답 데이터가 null이거나 비어 있습니다."+weatherResponse.response.body.items.itemList);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                txtTemperature.setText("데이터 가져오기 실패");
                Log.e("MainActivity", errorMessage);
            }
        });

    }

}
