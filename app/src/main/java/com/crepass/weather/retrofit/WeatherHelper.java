package com.crepass.weather.retrofit;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeatherHelper {

    // 정적 메서드로 Weather 데이터를 가져오는 함수
    public static void fetchWeatherData(String baseDate, String baseTime, String nx, String ny,int numOfRows,WeatherRepository.WeatherCallback callback) {

        WeatherRepository weatherRepository = new WeatherRepository();
        Log.d("tag",baseDate+baseTime);

        weatherRepository.getWeather(baseDate, baseTime, nx, ny,numOfRows, callback);
    }
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    public static String getDateFromTitle(String title) {
        Calendar calendar = Calendar.getInstance();
        if (title.equals("내일")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        } else if (title.equals("모레")) {
            calendar.add(Calendar.DAY_OF_YEAR, 2);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static String getBaseTime() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Base Time 계산
        if (minute < 10) {
            hour -= 1; // 10분 이전인 경우 이전 시간을 사용
        }

        // Base Time을 3시간 간격으로 설정
        if (hour >= 23) {
            return "2300";
        } else if (hour >= 20) {
            return "2000";
        } else if (hour >= 17) {
            return "1700";
        } else if (hour >= 14) {
            return "1400";
        } else if (hour >= 11) {
            return "1100";
        } else if (hour >= 8) {
            return "0800";
        } else if (hour >= 5) {
            return "0500";
        } else {
            return "0200";
        }
    }

}
