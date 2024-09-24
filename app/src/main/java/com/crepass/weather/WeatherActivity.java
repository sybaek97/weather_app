package com.crepass.weather;

import static com.crepass.weather.common.WeatherHelper.calculateAverageRainProbability;
import static com.crepass.weather.common.WeatherHelper.getBaseTime;
import static com.crepass.weather.common.WeatherHelper.getCurrentDate;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.crepass.weather.adapter.WeatherAdapter;
import com.crepass.weather.common.CoordinateConverter;
import com.crepass.weather.databinding.ActivityWeatherBinding;
import com.crepass.weather.item.ChildItem;
import com.crepass.weather.item.WeatherItem;
import com.crepass.weather.location.LocationHandler;
import com.crepass.weather.common.WeatherHelper;
import com.crepass.weather.retrofit.WeatherRepository;
import com.crepass.weather.retrofit.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WeatherActivity extends AppCompatActivity implements LocationHandler.LocationCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001; // 위치 권한 요청 코드
    private TextView txtTemperature;
    private ActivityWeatherBinding binding;
    private WeatherAdapter weatherAdapter;
    private List<WeatherItem> weatherItems;
    private FusedLocationProviderClient fusedLocationClient; // 위치 클라이언트
    private LocationHandler locationHandler;

    // 날짜 포맷 상수 정의
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH00", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding을 사용한 레이아웃 설정
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 초기화
        txtTemperature = binding.txtTemperature;
        weatherItems = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // 위치 클라이언트 초기화

        // 날짜별 WeatherItem 초기화
        initializeWeatherItems();

        // 리사이클러뷰 초기화 및 어댑터 설정
        setupRecyclerView();

        // 위치 권한 요청 및 현재 위치 가져오기
        locationHandler = new LocationHandler(this, fusedLocationClient, this);
        locationHandler.requestLocationPermission(LOCATION_PERMISSION_REQUEST_CODE);
        binding.layoutRefreshLocation.setOnClickListener(view -> {
            updateLocationAndWeather();
        });
    }

    private void updateLocationAndWeather() {
        Toast.makeText(this, "정보를 불러오고 있습니다...", Toast.LENGTH_SHORT).show();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // 현재 위치 좌표 가져오기
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        CoordinateConverter.GridCoordinate grid = CoordinateConverter.convert(latitude, longitude);
                        String nx = String.valueOf(grid.nx);
                        String ny = String.valueOf(grid.ny);

                        // 지역 이름 표시
                        displayLocationName(latitude, longitude);

                        // 날씨 데이터 요청
                        String baseDate = getCurrentDate();  // 현재 날짜
                        String baseTime = getBaseTime();     // 발표 시간
                        fetchWeatherData(baseDate, baseTime, nx, ny); // 현재 위치의 좌표로 날씨 데이터 요청

                        String baseMinMaxTime = "2300";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        String baseMinMaxDate = dateFormat.format(calendar.getTime());
                        fetchMinMaxWeatherData(baseMinMaxDate, baseMinMaxTime, nx, ny);
                    } else {
                        txtTemperature.setText("위치를 가져올 수 없습니다.");
                    }
                })
                .addOnFailureListener(e -> {
                    txtTemperature.setText("위치 정보 업데이트 실패");
                    Log.e("WeatherActivity", "위치 정보 업데이트 실패: " + e.getMessage());
                });
    }

    // 날짜별 초기 WeatherItem 리스트 생성
    private void initializeWeatherItems() {
        Calendar calendar = Calendar.getInstance();
        weatherItems.add(new WeatherItem("오늘", calendar.getTime(), "", "", 1, new ArrayList<>()));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        weatherItems.add(new WeatherItem("내일", calendar.getTime(), "", "", 1, new ArrayList<>()));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        weatherItems.add(new WeatherItem("모레", calendar.getTime(), "", "", 1, new ArrayList<>()));
    }

    // 리사이클러뷰 초기화 및 어댑터 설정
    private void setupRecyclerView() {
        weatherAdapter = new WeatherAdapter(weatherItems);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(weatherAdapter);
    }

    // 위치 콜백 메서드 구현 (현재 위치 좌표 전달)
    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // 위도/경도를 격자 좌표로 변환
            CoordinateConverter.GridCoordinate grid = CoordinateConverter.convert(latitude, longitude);
            String nx = String.valueOf(grid.nx);
            String ny = String.valueOf(grid.ny);

            displayLocationName(latitude, longitude);

            // 날씨 데이터 요청
            String baseDate = getCurrentDate();  // 현재 날짜
            String baseTime = getBaseTime();     // 발표 시간
            fetchWeatherData(baseDate, baseTime, nx, ny); // 현재 위치의 좌표로 날씨 데이터 요청

            String baseMinMaxTime = "2300";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String baseMinMaxDate = dateFormat.format(calendar.getTime());
            fetchMinMaxWeatherData(baseMinMaxDate, baseMinMaxTime, nx, ny); // 현재 위치의 좌표로 날씨 데이터 요청
        } else {
            txtTemperature.setText("위치를 가져올 수 없습니다.");
        }
    }

    private void displayLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String district = address.getSubLocality();

                if (district == null) {
                    district = address.getLocality();
                }


                Log.d("TAg", String.valueOf(address));
                String locationName = district;
                binding.txtRegion.setText(locationName);
            } else {
                binding.txtRegion.setText("주소를 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            binding.txtRegion.setText("주소 변환 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationHandler.getCurrentLocation();
            } else {
                txtTemperature.setText("위치 권한이 필요합니다.");
            }
        }
    }

    // 날씨 데이터 API 호출
    private void fetchWeatherData(String baseDate, String baseTime, String nx, String ny) {
        WeatherHelper.fetchWeatherData(baseDate, baseTime, nx, ny, 860, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                if (weatherResponse != null && weatherResponse.response != null && weatherResponse.response.body != null && weatherResponse.response.body.items != null && weatherResponse.response.body.items.itemList != null) {
                    List<WeatherResponse.Item> itemList = weatherResponse.response.body.items.itemList;
                    processWeatherData(itemList);
                    Toast.makeText(WeatherActivity.this, "업데이트가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    txtTemperature.setText("API 응답 데이터가 올바르지 않습니다.");
                    Log.e("WeatherActivity", "응답 데이터가 null이거나 비어 있습니다.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                txtTemperature.setText("데이터 가져오기 실패");
                Log.e("WeatherActivity", errorMessage);
            }
        });
    }

    // 날씨 데이터 API 호출
    private void fetchMinMaxWeatherData(String baseDate, String baseTime, String nx, String ny) {
        WeatherHelper.fetchWeatherData(baseDate, baseTime, nx, ny, 800, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                if (weatherResponse != null && weatherResponse.response != null && weatherResponse.response.body != null && weatherResponse.response.body.items != null && weatherResponse.response.body.items.itemList != null) {
                    List<WeatherResponse.Item> itemList = weatherResponse.response.body.items.itemList;
                    processMinMaxWeatherData(itemList);
                } else {
                    txtTemperature.setText("API 응답 데이터가 올바르지 않습니다.");
                    Log.e("WeatherActivity", "응답 데이터가 null이거나 비어 있습니다.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                txtTemperature.setText("데이터 가져오기 실패");
                Log.e("WeatherActivity", errorMessage);
            }
        });
    }

    // 날씨 데이터를 처리하여 WeatherItem 리스트 업데이트
    private void processMinMaxWeatherData(List<WeatherResponse.Item> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            txtTemperature.setText("데이터가 없습니다.");
            return;
        }

        Map<String, String> maxTempMap = new HashMap<>();
        Map<String, String> minTempMap = new HashMap<>();

        // 아이템 분류 및 데이터 저장
        for (WeatherResponse.Item item : itemList) {
            String dateKey = item.fcstDate;
            switch (item.category) {
                case "TMX":
                    maxTempMap.put(dateKey, item.fcstValue);
                    break;
                case "TMN":
                    minTempMap.put(dateKey, item.fcstValue);
                    break;
            }
        }

        // WeatherItem 업데이트
        for (WeatherItem weatherItem : weatherItems) {
            String dateKey = DATE_FORMAT.format(weatherItem.getDate());

            // 최고/최저 기온 설정
            if (maxTempMap.containsKey(dateKey)) {
                weatherItem.setTmx(maxTempMap.get(dateKey));
            }
            if (minTempMap.containsKey(dateKey)) {
                weatherItem.setTmn(minTempMap.get(dateKey));
            }
        }
        updateCurrentTemperatureAndPOP(itemList);
        weatherAdapter.notifyDataSetChanged();


    }

    // 날씨 데이터를 처리하여 WeatherItem 리스트 업데이트
    private void processWeatherData(List<WeatherResponse.Item> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            txtTemperature.setText("데이터가 없습니다.");
            return;
        }

        Map<String, List<ChildItem>> hourlyDataMap = new HashMap<>();
        Map<String, List<Integer>> rainProbabilityMap = new HashMap<>(); // 날짜별 강수 확률 저장

        // 아이템 분류 및 데이터 저장
        for (WeatherResponse.Item item : itemList) {
            String dateKey = item.fcstDate;
            switch (item.category) {
                case "TMP":
                    storeHourlyData(hourlyDataMap, item);
                    break;
                case "POP":
                    List<Integer> rainProbabilities = rainProbabilityMap.getOrDefault(dateKey, new ArrayList<>());
                    rainProbabilities.add(Integer.parseInt(item.fcstValue)); // 강수 확률을 리스트에 추가
                    rainProbabilityMap.put(dateKey, rainProbabilities);

                    storeHourlyData(hourlyDataMap, item);
                    break;
            }
        }
        for (WeatherItem weatherItem : weatherItems) {
            String dateKey = DATE_FORMAT.format(weatherItem.getDate());

            // 시간별 데이터 설정
            if (hourlyDataMap.containsKey(dateKey)) {
                weatherItem.setChildItems(hourlyDataMap.get(dateKey));
            }

            if (rainProbabilityMap.containsKey(dateKey)) {
                int averageRainProbability = calculateAverageRainProbability(Objects.requireNonNull(rainProbabilityMap.get(dateKey)));
                int rainLevel = averageRainProbability < 50 ? 1 : 2; // 50% 기준으로 1 또는 2 반환
                weatherItem.setRainProbability(rainLevel);
            }
        }
        weatherAdapter.notifyDataSetChanged();
    }

    // 시간별 데이터 저장 메서드
    private void storeHourlyData(Map<String, List<ChildItem>> hourlyDataMap, WeatherResponse.Item item) {
        String dateKey = item.fcstDate;
        List<ChildItem> hourlyData = hourlyDataMap.getOrDefault(dateKey, new ArrayList<>());

        String time = item.fcstTime;
        String hourlyTemperature = item.category.equals("TMP") ? item.fcstValue : "";
        String rainPercent = item.category.equals("POP") ? item.fcstValue : "";
        int iconRes = getIconResource(rainPercent);

        // 중복된 시간대 데이터 업데이트
        for (ChildItem data : hourlyData) {
            if (data.getTime().equals(time)) {
                if (!hourlyTemperature.isEmpty()) data.setTemperature(hourlyTemperature);
                if (!rainPercent.isEmpty()) data.setRainPercent(rainPercent);
                data.setIconRes(iconRes);
                return;
            }
        }

        // 새로운 시간대 데이터 추가
        hourlyData.add(new ChildItem(time, hourlyTemperature, rainPercent, iconRes));
        hourlyDataMap.put(dateKey, hourlyData);
    }

    // 강수확률에 따른 아이콘 리소스 설정 메서드
    private int getIconResource(String rainPercent) {
        if (rainPercent.isEmpty()) return 1;
        try {
            int rain = Integer.parseInt(rainPercent);
            return (rain > 50) ? 2 : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // 현재 온도 및 강수 확률 업데이트 메서드
    private void updateCurrentTemperatureAndPOP(List<WeatherResponse.Item> itemList) {
        String currentDate = DATE_FORMAT.format(Calendar.getInstance().getTime());
        String currentTime = TIME_FORMAT.format(Calendar.getInstance().getTime());

        String currentTemperature = null;
        Integer currentRain = null;

        // 온도와 강수 확률을 모두 업데이트하기 위해 반복문에서 처리
        for (WeatherResponse.Item item : itemList) {
            if (item.fcstDate.equals(currentDate) && item.fcstTime.equals(currentTime)) {
                if (item.category.equals("TMP")) {
                    currentTemperature = item.fcstValue;
                } else if (item.category.equals("POP")) {
                    currentRain = Integer.parseInt(item.fcstValue);
                }
            }
        }
        // 온도 업데이트
        if (currentTemperature != null) {
            txtTemperature.setText(currentTemperature + "°C");
        } else {
            txtTemperature.setText("기온 데이터 없음");
        }

        // 강수 확률 업데이트
        if (currentRain != null) {
            binding.imgTemperature.setImageResource((currentRain > 50) ? R.drawable.rain : R.drawable.sun);
        } else {
            binding.imgTemperature.setImageResource(R.drawable.sun); // 기본 날씨 아이콘
        }
    }

}
